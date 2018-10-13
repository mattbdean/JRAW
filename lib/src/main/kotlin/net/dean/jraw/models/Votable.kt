package net.dean.jraw.models

/**
 * Common interface for models that can be upvoted or downvoted
 */
interface Votable {
    /** How the logged-in user voted on the model */
    val vote: VoteDirection

    /** Upvotes minus downvotes */
    val score: Int

    /** Upvotes & downvotes */
    val ups: Int
    val downs: Int
}
