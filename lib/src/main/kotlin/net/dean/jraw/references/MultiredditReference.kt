package net.dean.jraw.references

import net.dean.jraw.*
import net.dean.jraw.JrawUtils.urlEncode
import net.dean.jraw.http.NetworkException
import net.dean.jraw.models.Multireddit
import net.dean.jraw.models.MultiredditPatch
import net.dean.jraw.models.Submission
import net.dean.jraw.pagination.DefaultPaginator
import net.dean.jraw.pagination.Paginator

class MultiredditReference internal constructor(reddit: RedditClient, val username: String, val multiredditName: String) :
    AbstractReference<String>(reddit, multiPath(username, multiredditName)) {

    /** Alias to [subject] */
    val multiPath = subject

    val authenticatedMultiPath: String by lazy {
        if (username == UserReference.NAME_SELF)
            multiPath(reddit.requireAuthenticatedUser(), multiredditName)
        else
            multiPath
    }

    /**
     * Updates this multireddit or creates it if it doesn't exist yet
     */
    @EndpointImplementation(Endpoint.PUT_MULTI_MULTIPATH, Endpoint.POST_MULTI_MULTIPATH)
    fun createOrUpdate(patch: MultiredditPatch): Multireddit {
        try {
            return reddit.request {
                it.endpoint(Endpoint.PUT_MULTI_MULTIPATH, authenticatedMultiPath)
                    .put(mapOf(
                        "model" to JrawUtils.jackson.writeValueAsString(patch)
                    ))
            }.deserialize()
        } catch (e: NetworkException) {
            val json = e.res.json
            if (!json.has("explanation") || !json.has("reason")) throw e
            throw ApiException(json["reason"].asText(), json["explanation"].asText())
        }
    }

    /** Gets a [Multireddit] instance that reflects this reference */
    @EndpointImplementation(Endpoint.GET_MULTI_MULTIPATH)
    fun about(): Multireddit {
        return reddit.request {
            it.endpoint(Endpoint.GET_MULTI_MULTIPATH, authenticatedMultiPath)
        }.deserialize()
    }

    /** Gets the multireddit description */
    @EndpointImplementation(Endpoint.GET_MULTI_MULTIPATH_DESCRIPTION)
    fun description(): String {
        return JrawUtils.navigateJson(reddit.request {
            it.endpoint(Endpoint.GET_MULTI_MULTIPATH_DESCRIPTION, authenticatedMultiPath)
        }.json, "data", "body_md").asText()
    }

    /** Updates the multireddit description */
    @EndpointImplementation(Endpoint.PUT_MULTI_MULTIPATH_DESCRIPTION)
    fun updateDescription(newDescription: String) {
        // Endpoint returns the new description, but we already have that, so don't return anything
        reddit.request {
            it.endpoint(Endpoint.PUT_MULTI_MULTIPATH_DESCRIPTION, authenticatedMultiPath)
                .put(mapOf(
                    "model" to JrawUtils.jackson.writeValueAsString(mapOf("body_md" to newDescription))
                ))
        }
    }

    /** Deletes this multireddit */
    @EndpointImplementation(Endpoint.DELETE_MULTI_MULTIPATH)
    fun delete() {
        // Response type is application/json, but it's just an empty string
        reddit.request {
            it.delete()
                .endpoint(Endpoint.DELETE_MULTI_MULTIPATH, authenticatedMultiPath)
        }
    }

    fun posts(): Paginator.Builder<Submission> =
        DefaultPaginator.Builder<Submission>(reddit, multiPath, sortingAsPathParameter = true)

    companion object {
        @JvmStatic private fun multiPath(username: String, multiName: String) =
            "user/${urlEncode(username)}/m/${urlEncode(multiName)}"
    }
}

