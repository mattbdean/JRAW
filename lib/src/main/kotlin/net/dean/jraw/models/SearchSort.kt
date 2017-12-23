package net.dean.jraw.models

enum class SearchSort(override val requiresTimePeriod: Boolean = true) : Sorting {
    /** Posts that match the search query the most */
    RELEVANCE,

    /** Popular posts, takes into account score, age, and other factors. */
    HOT,

    /** Posts that received the biggest score (upvotes minus downvotes) */
    TOP,

    /** New posts */
    NEW,

    /** Posts that received the most comments */
    COMMENTS;
}
