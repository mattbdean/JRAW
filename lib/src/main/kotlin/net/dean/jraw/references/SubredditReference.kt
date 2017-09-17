package net.dean.jraw.references

import net.dean.jraw.*
import net.dean.jraw.models.Submission
import net.dean.jraw.models.SubmissionKind
import net.dean.jraw.models.Subreddit
import net.dean.jraw.models.internal.GenericJsonResponse
import net.dean.jraw.models.internal.SubmissionData
import net.dean.jraw.pagination.DefaultPaginator
import net.dean.jraw.tree.RootCommentNode

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
    fun about(): Subreddit = reddit.request { it.path("/r/$subject/about") }.deserializeEnveloped()

    /**
     * Creates a new [DefaultPaginator.Builder] to iterate over this subreddit's posts. Not a blocking call.
     */
    @EndpointImplementation(
        Endpoint.GET_HOT, Endpoint.GET_NEW, Endpoint.GET_RISING, Endpoint.GET_SORT,
        type = MethodType.NON_BLOCKING_CALL
    )
    fun posts() = DefaultPaginator.Builder.create<Submission>(reddit, "/r/$subject", sortingAlsoInPath = true)

    /**
     * Gets a random submission from this subreddit. Although it is not marked with [EndpointImplementation], this
     * method executes a network request.
     *
     * @see RedditClient.randomSubreddit
     */
    fun randomSubmission(): RootCommentNode {
        val data: SubmissionData = reddit.request { it.path("/r/${JrawUtils.urlEncode(subject)}/random") }.deserialize()
        return RootCommentNode(data.submissions[0], data.comments, settings = null)
    }

    /**
     * Submits content to this subreddit
     *
     * @param kind Is this a self post (text) or a link post?
     * @param content If `kind` is [SubmissionKind.SELF], the Markdown-formatted body, else a URL.
     * @param sendReplies If direct replies to the submission should be sent to the user's inbox
     */
    @EndpointImplementation(Endpoint.POST_SUBMIT)
    fun submit(kind: SubmissionKind, title: String, content: String, sendReplies: Boolean): SubmissionReference {
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

        val res = reddit.request {
            it.endpoint(Endpoint.POST_SUBMIT)
                .post(args)
        }.deserialize<GenericJsonResponse>()

        val id = res.json?.data?.get("id") as? String ?:
            throw IllegalArgumentException("ID not found")

        return SubmissionReference(reddit, id)
    }

    /**
     * Gets the text meant to be displayed on the submission form.
     */
    @EndpointImplementation(Endpoint.GET_SUBMIT_TEXT)
    fun submitText(): String {
        return reddit.request {
            it.path("/r/{subreddit}/api/submit_text", subject)
        }.deserialize<Map<String, String>>().getOrElse("submit_text") {
            throw IllegalArgumentException("Unexpected response: no `submit_text` key")
        }
    }

    /** Alias to `setSubscribed(true)` */
    fun subscribe() = setSubscribed(true)

    /** Alias to `setSubscribed(false)` */
    fun unsubscribe() = setSubscribed(false)

    @EndpointImplementation(Endpoint.POST_SUBSCRIBE)
    fun setSubscribed(subscribe: Boolean) {
        val body = mutableMapOf(
            "sr_name" to subject,
            "action" to if (subscribe) "sub" else "unsub"
        )

        if (subscribe)
            // "prevent automatically subscribing the user to the current set of defaults when they take their first
            // subscription action"
            body["skip_initial_defaults"] = "true"

        // Response is an empty JSON object
        reddit.request {
            it.endpoint(Endpoint.POST_SUBSCRIBE).post(body)
        }
    }
}
