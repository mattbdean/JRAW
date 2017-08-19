package net.dean.jraw.references

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.RedditClient
import net.dean.jraw.models.CommentTreeSettings
import net.dean.jraw.models.KindConstants
import net.dean.jraw.models.RootCommentNode
import net.dean.jraw.models.Submission

/**
 * A Reference to a link or text submitted to a subreddit, like [this one](https://www.reddit.com/comments/6afe8u).
 */
class SubmissionReference internal constructor(reddit: RedditClient, id: String) :
    PublicContributionReference(reddit, id, KindConstants.SUBMISSION) {

    /**
     * Makes a request to retrieve all comments from this submission with the default settings.
     *
     * This method is equivalent to
     *
     * ```kotlin
     * comments(CommentsRequest())
     * ```
     */
    fun comments(): RootCommentNode = comments(CommentsRequest())

    /**
     * Makes a request to retrieve comments from this submission with the given settings.
     */
    @EndpointImplementation(Endpoint.GET_COMMENTS_ARTICLE)
    fun comments(spec: CommentsRequest): RootCommentNode {
        val query = mapOf(
            "comment" to spec.focus,
            "context" to spec.context?.toString(),
            "depth" to spec.depth?.toString(),
            "limit" to spec.limit?.toString(),
            "sort" to spec.sort.name.toLowerCase(),
            "sr_detail" to "false"
        )
            .filterValues { it != null }
            .mapValues { it.value ?: throw IllegalStateException("should not have been thrown") }

        val settings = CommentTreeSettings(
            submissionId = subject,
            sort = spec.sort
        )

        return RootCommentNode(reddit.request { it.path("/comments/$subject").query(query) }.json, settings)
    }

    /**
     * Gets a [Submission] instance for this reference.
     *
     * Equivalent to
     *
     * ```kotlin
     * comments().submission
     * ```
     */
    fun inspect(): Submission = comments().subject

    /** Equivalent to `setHidden(true)` */
    fun hide() = setHidden(true)

    /** Equivalent to `setHidden(false)` */
    fun unhide() = setHidden(false)

    /** Hides or unhides this submission */
    @EndpointImplementation(Endpoint.POST_HIDE, Endpoint.POST_UNHIDE)
    fun setHidden(hidden: Boolean) {
        // Just make sure it doesn't throw an exception
        reddit.request {
            it.endpoint(if (hidden) Endpoint.POST_HIDE else Endpoint.POST_UNHIDE)
                // 'id' requires a full name
                .post(mapOf("id" to KindConstants.SUBMISSION + "_" + subject))
        }
    }
}
