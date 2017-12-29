package net.dean.jraw.references

import net.dean.jraw.*
import net.dean.jraw.databind.Enveloped
import net.dean.jraw.http.HttpResponse
import net.dean.jraw.models.Comment
import net.dean.jraw.models.DistinguishedStatus
import net.dean.jraw.models.VoteDirection
import net.dean.jraw.models.internal.GenericJsonResponse

/**
 * Base class for References that can be publicly voted upon and saved (in essence, Submissions and Comments).
 *
 * Provides methods for [upvoting][upvote], [downvoting][downvote], and [removing the current vote][unvote], and also
 * one method for [manually setting the vote direction by enum value][setVote].
 *
 * @property id The base 36 ID of the model.
 */
abstract class PublicContributionReference internal constructor(reddit: RedditClient, val id: String, kindPrefix: String) :
    AbstractReference(reddit) {

    /**
     * The full name of the subject. Takes the form "${kindPrefix}_$id", where ${kindPrefix} is a value specified in
     * [net.dean.jraw.models.KindConstants].
     */
    val fullName = "${kindPrefix}_$id"

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
     *
     * reddit responds with an HTTP 400 if the post is too old to be voted on.
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

    /**
     * Creates a comment in response to this submission of comment
     *
     * @throws net.dean.jraw.ApiException Most commonly for ratelimiting.
     */
    @Throws(ApiException::class)
    @EndpointImplementation(Endpoint.POST_COMMENT)
    fun reply(text: String): Comment {
        val res = reddit.request {
            it.endpoint(Endpoint.POST_COMMENT)
                .post(mapOf(
                    "api_type" to "json",
                    "text" to text,
                    "thing_id" to fullName
                ))
        }.deserialize<GenericJsonResponse>()

        val comment = (res.json?.data?.get("things") as? List<*>)?.get(0) ?:
            throw IllegalArgumentException("Unexpected JSON structure")

        return JrawUtils.adapter<Comment>(Enveloped::class.java).fromJsonValue(comment)!!
    }

    /** Attempts to delete the model. The authenticated user must have created the model for this call to succeed. */
    @EndpointImplementation(Endpoint.POST_DEL)
    fun delete() {
        reddit.request {
            it.endpoint(Endpoint.POST_DEL)
                .post(mapOf("id" to fullName))
        }
    }

    /** Sets the text body of this model to the specified Markdown string. Only valid for text posts and comments. */
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

    /** Enables or disables sending direct replies to this model to the user's inbox */
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

    /**
     * Distinguish a comment or submission author with a sigil. Logged in user must have the priveleges to use the
     * supplied [DistinguishedStatus], i.e. moderator priveleges for [DistinguishedStatus.MODERATOR] and admin
     * priveleges for [DistinguishedStatus.ADMIN] and so on.
     *
     * @param sticky Flag for comments, which will stick the distingushed comment to the top of all comments threads.
     * If a comment is marked sticky, it will override any other stickied comment for that post (as only one comment may
     * be stickied at a time.) Only top-level comments may be stickied. Requires moderator privileges. Can only be used
     * with [DistinguishedStatus.MODERATOR] or [DistinguishedStatus.ADMIN].
     *
     * See [Reddit API documentation](https://www.reddit.com/dev/api/#POST_api_distinguish)
     */
    @EndpointImplementation(Endpoint.POST_DISTINGUISH)
    fun distinguish(how: DistinguishedStatus, sticky: Boolean) {
        val howOption = when(how) {
            DistinguishedStatus.NORMAL -> "no"
            DistinguishedStatus.MODERATOR -> "yes"
            DistinguishedStatus.ADMIN -> "admin"
            DistinguishedStatus.SPECIAL -> "special"
            DistinguishedStatus.GOLD -> throw IllegalArgumentException("Cannot manually distinguish a contribution with a gold distinguish status")
        }
        if (sticky && this is SubmissionReference)
            throw IllegalArgumentException("Flag 'sticky' can only be set for comments, not submissions")
        if (sticky && how == DistinguishedStatus.NORMAL)
            throw IllegalArgumentException("Cannot use flag 'sticky' with DistinguishedStatus.NORMAL")

        reddit.request {
            it.endpoint(Endpoint.POST_DISTINGUISH)
                .post(mapOf(
                    "api_type" to "json",
                    "how" to howOption,
                    "id" to fullName,
                    "sticky" to sticky.toString()
                ))
        }
    }

    /**
     * Remove the contribution as a subreddit moderator. Requires mod priveleges on the subreddit of the contribution.
     *
     * @param spam Whether spam is the reason for the removal. Trains the subreddit spamfilter to be critical of
     * similar contributions in the future
     *
     * See [What is the difference between spam and remove buttons on reported posts?](https://www.reddit.com/r/modhelp/comments/3vp76c/whats_the_difference_between_spam_and_remove_on/)
     */
    @EndpointImplementation(Endpoint.POST_REMOVE)
    @JvmOverloads fun remove(spam: Boolean = false) {
        reddit.request {
            it.endpoint(Endpoint.POST_REMOVE)
                .post(mapOf(
                    "id" to fullName,
                    "spam" to spam.toString()
                ))
        }
    }

    /**
     * Approve the contribution as a subreddit moderator. Requires mod priveleges on the subreddit of the contribution.
     */
    @EndpointImplementation(Endpoint.POST_APPROVE)
    fun approve() {
        reddit.request {
            it.endpoint(Endpoint.POST_APPROVE)
                .post(mapOf(
                    "id" to fullName
                ))
        }
    }
}
