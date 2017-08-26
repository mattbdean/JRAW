package net.dean.jraw.references

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.RedditClient
import net.dean.jraw.models.VoteDirection

/**
 * Base class for References that can be publicly voted upon and saved (in essence, Submissions and Comments).
 *
 * Provides methods for [upvoting][upvote], [downvoting][downvote], and [removing the current vote][unvote], and also
 * one method for [manually setting the vote direction by enum value][setVote].
 */
abstract class PublicContributionReference internal constructor(reddit: RedditClient, id: String, kindPrefix: String) :
    AbstractReference<String>(reddit, id) {

    val fullName = "${kindPrefix}_$subject"

    /** Equivalent to `setVote(VoteDirection.UP)` */
    fun upvote() { setVote(VoteDirection.UP) }

    /** Equivalent to `setVote(VoteDirection.DOWN)` */
    fun downvote() { setVote(VoteDirection.DOWN) }

    /** Equivalent to `setVote(VoteDirection.NONE)` */
    fun unvote() { setVote(VoteDirection.NONE) }

    /**
     * Votes on a model on behalf of the user.
     *
     * From the reddit API docs:
     *
     * > Note: votes must be cast by humans. That is, API clients proxying a human's action one-for-one are OK, but bots
     *   deciding how to vote on content or amplifying a human's vote are not.
     */
    @EndpointImplementation(Endpoint.POST_VOTE)
    fun setVote(dir: VoteDirection) {
        val value = when (dir) {
            VoteDirection.UP -> 1
            VoteDirection.NONE -> 0
            VoteDirection.DOWN -> -1
        }
        reddit.request {
            it.endpoint(Endpoint.POST_VOTE).post(mapOf(
                "dir" to value.toString(),
                "id" to fullName
            ))
        }
    }

    /** Equivalent to `setSaved(true)` */
    fun save() = setSaved(true)

    /** Equivalent to `setSaved(false)` */
    fun unsave() = setSaved(false)

    /**
     * Saves or unsaves this model
     *
     * @see net.dean.jraw.models.PublicContribution.isSaved
     */
    @EndpointImplementation(Endpoint.POST_SAVE, Endpoint.POST_UNSAVE)
    fun setSaved(saved: Boolean) {
        val endpoint = if (saved) Endpoint.POST_SAVE else Endpoint.POST_UNSAVE
        // Returns '{}' on success
        reddit.request { it.endpoint(endpoint).post(mapOf("id" to fullName)) }
    }

    // TODO
//    /**
//     * Creates a comment in response to this submission of comment
//     *
//     * @throws ApiException Most commonly for ratelimiting.
//     */
//    @Throws(ApiException::class)
//    @EndpointImplementation(Endpoint.POST_COMMENT)
//    fun reply(text: String): Comment {
//        val res = reddit.request {
//            it.endpoint(Endpoint.POST_COMMENT)
//                .post(mapOf(
//                    "api_type" to "json",
//                    "text" to text,
//                    "thing_id" to fullName
//                ))
//        }
//
//        // For whatever reason reddit returns a 200 OK response when we're being ratelimited. Since RedditClient only
//        // checks for errors for non-successful status codes, we have to manually check for errors here
//        val errorStub = jackson.treeToValue(res.json, RedditExceptionStub::class.java)
//        if (errorStub != null)
//            throw errorStub.create(NetworkException(res))
//
//        // Deserialize specific JSON node to a Comment
//        return jackson.treeToValue(JrawUtils.navigateJson(res.json, "json", "data", "things", 0))
//    }

    @EndpointImplementation(Endpoint.POST_DEL)
    fun delete() {
        reddit.request {
            it.endpoint(Endpoint.POST_DEL)
                .post(mapOf("id" to fullName))
        }
    }

    @EndpointImplementation(Endpoint.POST_EDITUSERTEXT)
    fun edit(text: String) {
        reddit.request {
            it.endpoint(Endpoint.POST_EDITUSERTEXT)
                .post(mapOf(
                    "api_type" to "json",
                    "text" to text,
                    "thing_id" to fullName
                ))
        }
    }

    @EndpointImplementation(Endpoint.POST_SENDREPLIES)
    fun sendReplies(sendReplies: Boolean) {
        reddit.request {
            it.endpoint(Endpoint.POST_SENDREPLIES)
                .post(mapOf(
                    "id" to fullName,
                    "state" to sendReplies.toString()
                ))
        }
    }
}
