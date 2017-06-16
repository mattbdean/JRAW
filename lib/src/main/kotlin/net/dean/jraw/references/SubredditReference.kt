package net.dean.jraw.references

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.RedditClient
import net.dean.jraw.models.RootCommentNode
import net.dean.jraw.models.Submission
import net.dean.jraw.models.Subreddit
import net.dean.jraw.pagination.Paginator

/**
 * Allows the user to perform API actions against a subreddit
 *
 * @constructor Creates a new SubredditReference for the given subreddit. Do not include the "/r/" prefix (e.g. "pics")
 */
class SubredditReference internal constructor(reddit: RedditClient, subreddit: String) : AbstractReference<String>(reddit, subreddit) {

    /**
     * Returns a [Subreddit] instance for this reference
     */
    @EndpointImplementation(arrayOf(Endpoint.GET_SUBREDDIT_ABOUT))
    fun about(): Subreddit = reddit.request { it.path("/r/$subject/about") }.deserialize()

    /**
     * Creates a new [Paginator.Builder] to iterate over this subreddit's posts.
     */
    @EndpointImplementation(arrayOf(Endpoint.GET_HOT, Endpoint.GET_NEW, Endpoint.GET_RISING, Endpoint.GET_SORT))
    fun posts() = Paginator.Builder<Submission>(reddit, "/r/$subject")

    /**
     * Gets a random submission from this subreddit. Although it is not marked with [EndpointImplementation], this
     * method executes a network request.
     *
     * @see RedditClient.randomSubreddit
     */
    fun randomSubmission() = RootCommentNode(reddit.request { it.path("/r/$subject/random") }.json)
}
