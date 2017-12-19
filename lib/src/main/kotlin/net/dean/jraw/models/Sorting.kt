package net.dean.jraw.models

/**
 * Represents how the reddit API chooses what it returns in a Paginator
 *
 * @property requiresTimePeriod If this sorting method must also include a [TimePeriod]. For example, sorting by [TOP]
 * is ambiguous, reddit doesn't know what posts to include when sorting. Sorting by [TOP] of the past [TimePeriod.HOUR]
 * is not ambiguous.
 */
interface Sorting {
    val requiresTimePeriod: Boolean
    val name: String
}

internal enum class GeneralSort(override val requiresTimePeriod: Boolean = false) : Sorting {
    /** Used only internally as a default sort */
    NEW
}

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
