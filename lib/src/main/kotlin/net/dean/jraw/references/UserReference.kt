package net.dean.jraw.references

import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.treeToValue
import net.dean.jraw.*
import net.dean.jraw.databind.ListingDeserializer
import net.dean.jraw.http.NetworkException
import net.dean.jraw.models.Account
import net.dean.jraw.models.Trophy

class UserReference internal constructor(reddit: RedditClient, username: String) :
    AbstractReference<String>(reddit, username) {

    val isSelf = username == NAME_SELF

    @EndpointImplementation(Endpoint.GET_ME, Endpoint.GET_USER_USERNAME_ABOUT)
    fun about(): Account {
        val body = reddit.request {
            it.path(if (isSelf) "/api/v1/me" else "/user/$subject/about")
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
                it.endpoint(Endpoint.GET_USER_USERNAME_TROPHIES, subject)
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

            // Parse the ApiException data
            val root = e.res.json
            throw ApiException(root["reason"].asText(), root["explanation"].asText())
        }
    }

    companion object {
        const val NAME_SELF = "me"

        private val jackson = JrawUtils.defaultObjectMapper()
            .registerModule(ListingDeserializer.Module)
    }
}
