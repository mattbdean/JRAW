package net.dean.jraw.references

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.MethodType
import net.dean.jraw.RedditClient
import net.dean.jraw.pagination.ModLogPaginator

/**
 * Provides access to moderation tools and logs for a specific subreddit. The authenticated user must be a moderator of
 * the subreddit for this reference to be of any use
 */
class ModerationReference internal constructor(reddit: RedditClient, val subreddit: String) : AbstractReference(reddit) {
    /**
     * Provides access to the moderation log of this subreddit
     */
    @EndpointImplementation(Endpoint.GET_ABOUT_LOG, type = MethodType.NON_BLOCKING_CALL)
    fun log() = ModLogPaginator.Builder(reddit, subreddit)
}
