package net.dean.jraw.references

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.RedditClient
import net.dean.jraw.models.RootCommentNode
import net.dean.jraw.models.Submission
import net.dean.jraw.models.Subreddit

/**
 * Allows the user to perform API actions against a subreddit
 *
 * @constructor Creates a new SubredditReference for the given subreddit. Do not include the "/r/" prefix (e.g. "pics")
 */
class SubredditReference(reddit: RedditClient, subreddit: String) : AbstractReference<String>(reddit, subreddit) {

    /**
     * Returns a [Subreddit] instance for this subreddit
     */
    @EndpointImplementation(Endpoint.GET_SUBREDDIT_ABOUT)
    fun about(): Subreddit = reddit.request { it.path("/r/$subject/about") }.deserialize()

    fun posts() = PaginatorReference<Submission>(reddit, "/r/$subject")

    /**
     * Gets a random submission from this subreddit. Although it is not marked with [EndpointImplementation], this
     * method executes a network request.
     *
     * @see RedditClient.randomSubreddit
     */
    fun randomSubmission() = RootCommentNode(reddit.request { it.path("/r/$subject/random") }.json)
}
