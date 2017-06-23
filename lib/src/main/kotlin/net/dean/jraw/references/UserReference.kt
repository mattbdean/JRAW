package net.dean.jraw.references

import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.treeToValue
import net.dean.jraw.*
import net.dean.jraw.JrawUtils.urlEncode
import net.dean.jraw.databind.ListingDeserializer
import net.dean.jraw.http.NetworkException
import net.dean.jraw.models.Account
import net.dean.jraw.models.Multireddit
import net.dean.jraw.models.PublicContribution
import net.dean.jraw.models.Trophy
import net.dean.jraw.pagination.DefaultPaginator
import net.dean.jraw.pagination.Paginator
import okhttp3.MediaType
import okhttp3.RequestBody

class UserReference internal constructor(reddit: RedditClient, username: String) :
    AbstractReference<String>(reddit, username) {

    val isSelf = username == NAME_SELF
    val username: String by lazy {
        if (subject == NAME_SELF) {
            reddit.requireAuthenticatedUser()
        } else {
            subject
        }
    }

    @EndpointImplementation(Endpoint.GET_ME, Endpoint.GET_USER_USERNAME_ABOUT)
    fun about(): Account {
        val body = reddit.request {
            it.path(if (isSelf) "/api/v1/me" else "/user/$username/about")
        }.body

        // /api/v1/me doesn't encapsulate the data with a "kind" and "data" node, use our custom ObjectMapper instance
        // when calling that endpoint
        return (if (isSelf) jackson else JrawUtils.jackson).readValue(body)
    }

    @EndpointImplementation(Endpoint.GET_ME_TROPHIES, Endpoint.GET_USER_USERNAME_TROPHIES)
    fun trophies(): List<Trophy> {
        val json = reddit.request {
            if (isSelf)
                it.endpoint(Endpoint.GET_ME_TROPHIES)
            else
                it.endpoint(Endpoint.GET_USER_USERNAME_TROPHIES, username)
        }.json

        val trophies = JrawUtils.navigateJson(json, "data", "trophies")
        return trophies.map { JrawUtils.jackson.treeToValue<Trophy>(it) }
    }

    /**
     * Gets a Map of preferences set at [https://www.reddit.com/prefs].
     *
     * Likely to throw an [ApiException] if authenticated via application-only credentials
     */
    @EndpointImplementation(Endpoint.GET_ME_PREFS)
    @Throws(ApiException::class)
    fun prefs(): Map<String, Any> {
        try {
            return reddit.request { it.endpoint(Endpoint.GET_ME_PREFS) }.deserialize()
        } catch (e: NetworkException) {
            if (e.res.code != 403) throw e
            handleUnauthorized(e)
        }
    }

    /**
     * Patches over certain user preferences and returns all preferences.
     *
     * Although technically you can send any value as a preference value, generally only strings and booleans are used.
     * See [here](https://www.reddit.com/dev/api/oauth#GET_api_v1_me_prefs) for a list of all available preferences.
     *
     * Likely to throw an [ApiException] if authenticated via application-only credentials
     */
    @EndpointImplementation(Endpoint.PATCH_ME_PREFS)
    @Throws(ApiException::class)
    fun patchPrefs(newPrefs: Map<String, Any>): Map<String, Any> {
        val body = RequestBody.create(MediaType.parse("application/json"), JrawUtils.jackson.writeValueAsString(newPrefs))
        try {
            return reddit.request { it.endpoint(Endpoint.PATCH_ME_PREFS).patch(body) }.deserialize()
        } catch (e: NetworkException) {
            if (e.res.code != 403) throw e
            handleUnauthorized(e)
        }
    }

    /**
     * Creates a new [Paginator.Builder] which can iterate over a user's public history.
     *
     * Possible `where` values:
     *
     * - `overview` — submissions and comments
     * - `submitted` — only submissions
     * - `comments` — only comments
     * - `gilded` — submissions and comments which have received reddit Gold
     *
     * If this user reference is for the currently logged-in user, these `where` values can be used:
     *
     * - `upvoted`
     * - `downvoted`
     * - `hidden`
     * - `saved`
     *
     * Only `overview`, `submitted`, and `comments` are sortable.
     */
    @EndpointImplementation(Endpoint.GET_USER_USERNAME_WHERE)
    fun history(where: String): Paginator.Builder<PublicContribution<*>> {
        // Encode URLs to prevent accidental malformed URLs
        return DefaultPaginator.Builder(reddit, "/user/${urlEncode(username)}/${urlEncode(where)}",
            sortingAsPathParameter = false)
    }

    /**
     * Creates a [MultiredditReference] for a multireddit that belongs to this user.
     */
    fun multi(name: String) = MultiredditReference(reddit, subject, name)

    /**
     * Lists the multireddits this client is able to view.
     *
     * If this UserReference is for the logged-in user, all multireddits will be returned. Otherwise, only public
     * multireddits will be returned.
     */
    @EndpointImplementation(Endpoint.GET_MULTI_MINE, Endpoint.GET_MULTI_USER_USERNAME)
    fun listMultis(): List<Multireddit> {
        return reddit.request {
            if (isSelf) {
                it.endpoint(Endpoint.GET_MULTI_MINE)
            } else {
                it.endpoint(Endpoint.GET_MULTI_USER_USERNAME, subject)
            }
        }.deserialize()
    }

    companion object {
        const val NAME_SELF = "me"

        private val jackson = JrawUtils.defaultObjectMapper()
            .registerModule(ListingDeserializer.Module)

        @Throws(ApiException::class)
        private fun handleUnauthorized(e: NetworkException): Nothing {
            // Parse the ApiException data
            val root = e.res.json
            if (!root.has("reason") || !root.has("explanation")) {
                throw IllegalArgumentException("Expected standard 403 Unauthorized response", e)
            }
            throw ApiException(root["reason"].asText(), root["explanation"].asText())
        }
    }
}
