package net.dean.jraw.models

enum class SubredditSort(override val requiresTimePeriod: Boolean = false) : Sorting {
    /** Popular posts, takes into account score, age, and other factors. */
    HOT,

    /** New posts */
    NEW,

    /** Somewhat new posts that have a lot of karma relative to others posted at the same time */
    RISING,

    /** Posts that received a lot of both upvotes and downvotes */
    CONTROVERSIAL(true),

    /** Posts that received the biggest score (upvotes minus downvotes) */
    TOP(true);
}
