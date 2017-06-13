package net.dean.jraw.references

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.RedditClient
import net.dean.jraw.models.ThingType
import net.dean.jraw.models.VoteDirection

/**
 * Base class for References that can voted upon and saved.
 *
 * Provides methods for [upvoting][upvote], [downvoting][downvote], and [removing the current vote][unvote], and also
 * one method for [manually setting the vote direction by enum value][setVote].
 */
abstract class PublicContributionReference internal constructor(reddit: RedditClient, id: String, val type: ThingType) :
    AbstractReference<String>(reddit, id) {

    /** Equivalent to `setVote(VoteDirection.UP)` */
    fun upvote() { setVote(VoteDirection.UP) }

    /** Equivalent to `setVote(VoteDirection.DOWN)` */
    fun downvote() { setVote(VoteDirection.DOWN) }

    /** Equivalent to `setVote(VoteDirection.NONE)` */
    fun unvote() { setVote(VoteDirection.NONE) }

    /**
     * Votes on a model on behalf of the user.
     *
     * From the docs:
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
                "id" to type.prefix + '_' + subject
            ))
        }
    }
}
