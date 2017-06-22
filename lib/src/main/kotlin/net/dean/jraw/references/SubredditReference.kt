package net.dean.jraw.references

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.models.RootCommentNode
import net.dean.jraw.models.Submission
import net.dean.jraw.models.SubmissionKind
import net.dean.jraw.models.Subreddit
import net.dean.jraw.pagination.DefaultPaginator

/**
 * Allows the user to perform API actions against a subreddit
 *
 * @constructor Creates a new SubredditReference for the given subreddit. Do not include the "/r/" prefix (e.g. "pics")
 */
class SubredditReference internal constructor(reddit: RedditClient, subreddit: String) : AbstractReference<String>(reddit, subreddit) {

    /**
     * Returns a [Subreddit] instance for this reference
     */
    @EndpointImplementation(Endpoint.GET_SUBREDDIT_ABOUT)
    fun about(): Subreddit = reddit.request { it.path("/r/$subject/about") }.deserialize()

    /**
     * Creates a new [DefaultPaginator.Builder] to iterate over this subreddit's posts.
     */
    @EndpointImplementation(Endpoint.GET_HOT, Endpoint.GET_NEW, Endpoint.GET_RISING, Endpoint.GET_SORT)
    fun posts() = DefaultPaginator.Builder<Submission>(reddit, "/r/$subject")

    /**
     * Gets a random submission from this subreddit. Although it is not marked with [EndpointImplementation], this
     * method executes a network request.
     *
     * @see RedditClient.randomSubreddit
     */
    fun randomSubmission() = RootCommentNode(reddit.request { it.path("/r/$subject/random") }.json)

    /**
     * Submits content to this subreddit
     *
     * @param kind Is this a self post (text) or a link post?
     * @param content If `kind` is [SubmissionKind.SELF], the Markdown-formatted body, else a URL.
     * @param sendReplies If direct replies to the submission should be sent to the user's inbox
     */
    @EndpointImplementation(Endpoint.POST_SUBMIT)
    fun submit(kind: SubmissionKind, title: String, content: String, sendReplies: Boolean): String {
        val args = mutableMapOf(
            "api_type" to "json",
            "extension" to "json",
            "kind" to kind.name.toLowerCase(),
            "resubmit" to "false",
            "sendreplies" to sendReplies.toString(),
            "sr" to subject,
            "title" to title
        )

        args[if (kind == SubmissionKind.SELF) "text" else "url"] = content

        val json = reddit.request {
            it.endpoint(Endpoint.POST_SUBMIT)
                .post(args)
        }.json

        JrawUtils.handleApiErrors(json)

        return JrawUtils.navigateJson(json, "json", "data", "id").asText()
    }
}
