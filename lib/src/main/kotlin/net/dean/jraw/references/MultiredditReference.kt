package net.dean.jraw.references

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.JrawUtils
import net.dean.jraw.JrawUtils.urlEncode
import net.dean.jraw.RedditClient
import net.dean.jraw.models.Multireddit
import net.dean.jraw.models.MultiredditPatch
import net.dean.jraw.models.Submission
import net.dean.jraw.models.internal.LabeledMultiDescription
import net.dean.jraw.models.internal.SubredditElement
import net.dean.jraw.pagination.DefaultPaginator

/**
 * A reference to a multireddit
 *
 * @property username The user that this multireddit belongs to
 * @property multiredditName
 */
class MultiredditReference internal constructor(reddit: RedditClient, val username: String, val multiredditName: String) :
    AbstractReference(reddit) {

    /** The path relative to `reddit.com/` that this multireddit can be accessed with. No leading slash. */
    val multiPath = multiPath(username, multiredditName)

    /** Alias to [createOrUpdate] for the sake of semantics */
    fun update(patch: MultiredditPatch) = createOrUpdate(patch)

    /**
     * Updates this multireddit or creates it if it doesn't exist yet
     */
    @EndpointImplementation(Endpoint.PUT_MULTI_MULTIPATH, Endpoint.POST_MULTI_MULTIPATH)
    fun createOrUpdate(patch: MultiredditPatch): Multireddit {
        return reddit.request {
            it.endpoint(Endpoint.PUT_MULTI_MULTIPATH, multiPath)
                .put(mapOf(
                    "model" to JrawUtils.adapter<MultiredditPatch>().toJson(patch)
                ))
        }.deserializeEnveloped()
    }

    /** Gets a [Multireddit] instance that reflects this reference */
    @EndpointImplementation(Endpoint.GET_MULTI_MULTIPATH)
    fun about(): Multireddit {
        return reddit.request {
            it.endpoint(Endpoint.GET_MULTI_MULTIPATH, multiPath)
        }.deserializeEnveloped()
    }

    /** Gets the multireddit description */
    @EndpointImplementation(Endpoint.GET_MULTI_MULTIPATH_DESCRIPTION)
    fun description(): String {
        return reddit.request {
            it.endpoint(Endpoint.GET_MULTI_MULTIPATH_DESCRIPTION, multiPath)
        }.deserializeEnveloped<LabeledMultiDescription>().body
    }

    /** Updates the multireddit description */
    @EndpointImplementation(Endpoint.PUT_MULTI_MULTIPATH_DESCRIPTION)
    fun updateDescription(newDescription: String) {
        // Endpoint returns the new description, but we already have that, so don't return anything
        reddit.request {
            it.endpoint(Endpoint.PUT_MULTI_MULTIPATH_DESCRIPTION, multiPath)
                .put(mapOf(
                    "model" to JrawUtils.adapter<Map<String, String>>().toJson(mapOf("body_md" to newDescription))
                ))
        }
    }

    /** Deletes this multireddit */
    @EndpointImplementation(Endpoint.DELETE_MULTI_MULTIPATH)
    fun delete() {
        // Response type is application/json, but it's just an empty string
        reddit.request {
            it.delete()
                .endpoint(Endpoint.DELETE_MULTI_MULTIPATH, multiPath)
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
        val request = reddit.request {
            it.endpoint(endpoint)
                .post(mapOf(
                    "from" to multiPath,
                    "to" to multiPath(reddit.requireAuthenticatedUser(), targetName)
                ))
        }
        return request.deserializeEnveloped()
    }

    /**
     * Returns a [SubredditElement] for the given subreddit belonging to this multireddit. Honestly
     * this is a pretty useless method since right now it just returns what you already know.
     */
    @EndpointImplementation(Endpoint.GET_MULTI_MULTIPATH_R_SRNAME)
    fun subredditInfo(sr: String): SubredditElement {
        return reddit.request {
            it.endpoint(Endpoint.GET_MULTI_MULTIPATH_R_SRNAME, multiPath, sr)
                .query(mapOf("expand_srs" to "true"))
        }.deserialize()
    }

    /** Adds a subreddit to this multireddit. */
    @EndpointImplementation(Endpoint.PUT_MULTI_MULTIPATH_R_SRNAME)
    fun addSubreddit(sr: String) {
        // API returns the SubredditElement we send it, so returning that model would just be a waste of resources
        reddit.request {
            it.endpoint(Endpoint.PUT_MULTI_MULTIPATH_R_SRNAME, multiPath, sr)
                .put(mapOf(
                    "model" to JrawUtils.adapter<SubredditElement>().toJson(SubredditElement.create(sr))
                ))
        }
    }

    /** Removes a subreddit from this multireddit */
    @EndpointImplementation(Endpoint.DELETE_MULTI_MULTIPATH_R_SRNAME)
    fun removeSubreddit(sr: String) {
        // No useful response
        reddit.request {
            it.endpoint(Endpoint.DELETE_MULTI_MULTIPATH_R_SRNAME, multiPath, sr)
                .delete()
        }
    }

    /** Returns a Paginator Builder to iterate over posts in this multireddit */
    fun posts(): DefaultPaginator.Builder<Submission> =
        DefaultPaginator.Builder.create(reddit, multiPath, sortingAlsoInPath = true)

    /** */
    companion object {
        @JvmStatic private fun multiPath(username: String, multiName: String) =
            "user/${urlEncode(username)}/m/${urlEncode(multiName)}"
    }
}

