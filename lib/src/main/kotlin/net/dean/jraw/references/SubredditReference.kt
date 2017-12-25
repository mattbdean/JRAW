package net.dean.jraw.references

import com.squareup.moshi.Types
import net.dean.jraw.*
import net.dean.jraw.http.HttpResponse
import net.dean.jraw.models.*
import net.dean.jraw.models.internal.GenericJsonResponse
import net.dean.jraw.models.internal.SubmissionData
import net.dean.jraw.pagination.BarebonesPaginator
import net.dean.jraw.pagination.DefaultPaginator
import net.dean.jraw.pagination.SearchPaginator
import net.dean.jraw.tree.RootCommentNode
import java.net.URLEncoder

/**
 * Allows the user to perform API actions against a subreddit
 *
 * @constructor Creates a new SubredditReference for the given subreddit. Do not include the "/r/" prefix (e.g. "pics")
 *
 * @property subreddit The name of the subreddit without the "/r/" prefix.
 */
class SubredditReference internal constructor(reddit: RedditClient, val subreddit: String) : AbstractReference(reddit) {

    /**
     * Returns a [Subreddit] instance for this reference
     */
    @EndpointImplementation(Endpoint.GET_SUBREDDIT_ABOUT)
    fun about(): Subreddit = reddit.request { it.endpoint(Endpoint.GET_SUBREDDIT_ABOUT, subreddit) }.deserializeEnveloped()

    /**
     * Creates a new [DefaultPaginator.Builder] to iterate over this subreddit's posts. Not a blocking call.
     */
    @EndpointImplementation(
        Endpoint.GET_HOT, Endpoint.GET_NEW, Endpoint.GET_RISING, Endpoint.GET_SORT,
        type = MethodType.NON_BLOCKING_CALL
    )
    fun posts() = DefaultPaginator.Builder.create<Submission, SubredditSort>(reddit, "/r/$subreddit", sortingAlsoInPath = true)

    /**
     * Creates a BarebonesPaginator.Builder that will iterate over the latest comments from this subreddit when built.
     *
     * @see RedditClient.latestComments
     */
    fun comments(): BarebonesPaginator.Builder<Comment> = reddit.latestComments(subreddit)

    /**
     * Creates a BarebonesPaginator.Builder that will iterate over the gilded contributions in this subreddit when built.
     *
     * @see RedditClient.gildedContributions
     */
    fun gilded(): BarebonesPaginator.Builder<PublicContribution<*>> = reddit.gildedContributions(subreddit)

    /**
     * Creates a SearchPaginator.Builder to search for submissions in this subreddit.
     *
     * @see SearchPaginator.inSubreddits
     */
    fun search(): SearchPaginator.Builder = SearchPaginator.inSubreddits(reddit, subreddit)

    /**
     * Gets a random submission from this subreddit.
     *
     * @see RedditClient.randomSubreddit
     */
    @EndpointImplementation(Endpoint.GET_RANDOM)
    fun randomSubmission(): RootCommentNode {
        val data: SubmissionData = reddit.request { it.endpoint(Endpoint.GET_RANDOM, subreddit) }.deserialize()
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
            "sr" to subreddit,
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
            it.endpoint(Endpoint.GET_SUBMIT_TEXT, subreddit)
        }.deserialize<Map<String, String>>().getOrElse("submit_text") {
            throw IllegalArgumentException("Unexpected response: no `submit_text` key")
        }
    }

    /** Alias to `setSubscribed(true)` */
    fun subscribe() = setSubscribed(true)

    /** Alias to `setSubscribed(false)` */
    fun unsubscribe() = setSubscribed(false)

    /** Subscribes or unsubscribes to this subreddit. Requires an authenticated user. */
    @EndpointImplementation(Endpoint.POST_SUBSCRIBE)
    fun setSubscribed(subscribe: Boolean) {
        val body = mutableMapOf(
            "sr_name" to subreddit,
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

    /**
     * Lists all possible flairs for users. Requires an authenticated user. Will return nothing if flair is disabled on
     * the subreddit, the user cannot set their own flair, or they are not a moderator that can set flair.
     *
     * @see FlairReference.updateToTemplate
     */
    @EndpointImplementation(Endpoint.GET_USER_FLAIR)
    fun userFlairOptions(): List<Flair> = requestFlair("user")

    /**
     * Lists all possible flairs for submissions to this subreddit. Requires an authenticated user. Will return nothing
     * if the user cannot set their own link flair and they are not a moderator that can set flair.
     *
     * @see FlairReference.updateToTemplate
     */
    @EndpointImplementation(Endpoint.GET_LINK_FLAIR)
    fun linkFlairOptions(): List<Flair> = requestFlair("link")

    private fun requestFlair(type: String): List<Flair> {
        return reddit.request {
            it.path("/r/${JrawUtils.urlEncode(subreddit)}/api/${type}_flair")
        }.deserializeWith(JrawUtils.moshi.adapter(listOfFlairsType))
    }

    /**
     * Returns a UserFlairReference for a user.
     *
     * Equivalent to `redditClient.user(name).flairOn(subreddit)`.
     */
    fun otherUserFlair(name: String) = reddit.user(name).flairOn(subreddit)

    /**
     * Returns a UserFlairReference for the authenticated user.
     *
     * Equivalent to `redditClient.me().flairOn(subreddit)`.
     */
    fun selfUserFlair() = reddit.me().flairOn(subreddit)

    /**
     * Returns a SubmissionFlairReference for the given submission id (without the kind prefix).
     */
    fun submissionFlair(id: String) = SubmissionFlairReference(reddit, subreddit, id)

    /**
     * Returns the moderator-created rules for the subreddit. Users can report things in this subreddit using the String
     * specified in [SubredditRule.getViolationReason]
     */
    @EndpointImplementation(Endpoint.GET_SUBREDDIT_ABOUT_RULES)
    fun rules(): Ruleset {
        return reddit.request {
            it.endpoint(Endpoint.GET_SUBREDDIT_ABOUT_RULES, subreddit)
        }.deserialize()
    }

    /** Returns a reference to the subreddit's wiki */
    fun wiki() = WikiReference(reddit, subreddit)

    /** Fetches the stylesheet of a subreddit. Returned value is CSS. */
    @EndpointImplementation(Endpoint.GET_STYLESHEET)
    fun stylesheet(): String {
        return reddit.request {
            it.endpoint(Endpoint.GET_STYLESHEET, subreddit)
        }.body
    }

    /** Updates the stylesheet of a subreddit. */
    @EndpointImplementation(Endpoint.POST_SUBREDDIT_STYLESHEET)
    fun updateStylesheet(stylesheet: String, reason: String): HttpResponse {
        return reddit.request {
            it.endpoint(Endpoint.POST_SUBREDDIT_STYLESHEET, subreddit)
                .post(mapOf(
                    "api_type" to "json",
                    "op" to "save",
                    "reason" to reason,
                    "stylesheet_contents" to stylesheet
                ))
        }
    }

    /** Returns a listing of currently set flairs on the subreddit (both custom and template-based ones)
     *  Requires mod priveleges on the subreddit.
     */
    @EndpointImplementation(Endpoint.GET_FLAIRLIST)
    fun flairList() = BarebonesPaginator.Builder.create<SimpleFlairInfo>(reddit, "/r/$subreddit/api/flairlist")

    /** Updates users flairs on the subreddit in bulk (up to 100 rows, the rest are ignored by Reddit).
     *  Requires mod priveleges on the subreddit.
     */
    @EndpointImplementation(Endpoint.POST_FLAIRCSV)
    fun patchFlairList(patch: List<SimpleFlairInfo>) {
        reddit.request {
            it.path("/r/$subreddit/api/flaircsv")
                .post(mapOf(
                    "flair_csv" to (patch.joinToString(separator = URLEncoder.encode("\n", "UTF-8")) { it.toCsvLine() })
                ))
        }
    }

    /** */
    companion object {
        private val listOfFlairsType = Types.newParameterizedType(List::class.java, Flair::class.java)
    }
}
