package net.dean.jraw.models

enum class UserHistorySort(override val requiresTimePeriod: Boolean = false) : Sorting {
    /** Popular posts and comments, takes into account score, age, and other factors. */
    HOT,

    /** New posts and comments */
    NEW,

    /** Posts and comments that received a lot of both upvotes and downvotes */
    CONTROVERSIAL(true),

    /** Posts and comments that received the biggest score (upvotes minus downvotes) */
    TOP(true);
}
