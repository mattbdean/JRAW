package net.dean.jraw.models

/**
 * Common interface for models that can be upvoted or downvoted
 */
interface Votable {
    /** True if the user upvoted, false if downvoted, and null for no vote */
    val likes: Boolean?

    /** Upvotes minus downvotes */
    val score: Int
}
