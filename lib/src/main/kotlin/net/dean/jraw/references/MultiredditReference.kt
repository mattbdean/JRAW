package net.dean.jraw.references

import net.dean.jraw.*
import net.dean.jraw.JrawUtils.jackson
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
            tryHandleNetworkException(e)
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

    /**
     * Saves a copy of this multireddit to the user's collection. Requires a logged-in user. Returns a the new
     * Multireddit reference.
     */
    @EndpointImplementation(Endpoint.POST_MULTI_COPY)
    fun copyTo(name: String): Multireddit {
        return copyOrRename(name, Endpoint.POST_MULTI_COPY)
    }

    /**
     * Gives this multireddit a new name. Returns the updated Multireddit reference.
     */
    @EndpointImplementation(Endpoint.POST_MULTI_RENAME)
    fun rename(newName: String): Multireddit {
        return copyOrRename(newName, Endpoint.POST_MULTI_RENAME)
    }

    /**
     * Both `POST /api/multi/copy` and `POST /api/multi/rename` have very similar request bodies, so to simplify things
     * their implementing methods just call this one with the correct Endpoint
     */
    private fun copyOrRename(targetName: String, endpoint: Endpoint): Multireddit {
        try {
            val request = reddit.request {
                it.endpoint(endpoint)
                    .post(mapOf(
                        "from" to multiPath,
                        "to" to multiPath(reddit.requireAuthenticatedUser(), targetName)
                    ))
            }
            return request.deserialize()
        } catch (e: NetworkException) {
            tryHandleNetworkException(e)
        }
    }

    /**
     * Returns a [MultiredditPatch.SubredditElement] for the given subreddit belonging to this multireddit. Honestly
     * this is a pretty useless method since right now it just returns what you already know.
     */
    @EndpointImplementation(Endpoint.GET_MULTI_MULTIPATH_R_SRNAME)
    fun subredditInfo(sr: String): MultiredditPatch.SubredditElement {
        return reddit.request {
            it.endpoint(Endpoint.GET_MULTI_MULTIPATH_R_SRNAME, multiPath, sr)
                .query(mapOf("expand_srs" to "true"))
        }.deserialize()
    }

    /** Adds a subreddit to this multireddit. */
    @EndpointImplementation(Endpoint.PUT_MULTI_MULTIPATH_R_SRNAME)
    fun addSubreddit(sr: String) {
        try {
            // API returns the SubredditElement we send it, so returning that model would just be a waste of resources
            reddit.request {
                it.endpoint(Endpoint.PUT_MULTI_MULTIPATH_R_SRNAME, multiPath, sr)
                    .put(mapOf(
                        "model" to jackson.writeValueAsString(MultiredditPatch.SubredditElement(sr))
                    ))
            }
        } catch (e: NetworkException) {
            tryHandleNetworkException(e)
        }
    }

    /** Removes a subreddit from this multireddit */
    @EndpointImplementation(Endpoint.DELETE_MULTI_MULTIPATH_R_SRNAME)
    fun removeSubreddit(sr: String) {
        try {
            // No useful response
            reddit.request {
                it.endpoint(Endpoint.DELETE_MULTI_MULTIPATH_R_SRNAME, multiPath, sr)
                    .delete()
            }
        } catch (e: NetworkException) {
            tryHandleNetworkException(e)
        }
    }

    fun posts(): Paginator.Builder<Submission> =
        DefaultPaginator.Builder<Submission>(reddit, multiPath, sortingAsPathParameter = true)

    private fun tryHandleNetworkException(e: NetworkException): Nothing {
        val json = e.res.json
        if (!json.has("explanation") || !json.has("reason")) throw e
        throw ApiException(json["reason"].asText(), json["explanation"].asText())
    }

    companion object {
        @JvmStatic private fun multiPath(username: String, multiName: String) =
            "user/${urlEncode(username)}/m/${urlEncode(multiName)}"
    }
}

