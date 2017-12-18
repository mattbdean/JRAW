package net.dean.jraw.models

/**
 * Represents how the reddit API chooses what it returns in a Paginator
 */
interface Sorting {
    val requiresTimePeriod: Boolean
    val name: String
}

enum class GeneralSort(override val requiresTimePeriod: Boolean = false) : Sorting {
    NEW
}

enum class SubredditSort(override val requiresTimePeriod: Boolean = false) : Sorting {
    HOT,
    NEW,
    RISING,
    CONTROVERSIAL(true),
    TOP(true);
}

enum class UserHistorySort(override val requiresTimePeriod: Boolean = false) : Sorting {
    HOT,
    NEW,
    CONTROVERSIAL(true),
    TOP(true);
}

enum class SearchSort(override val requiresTimePeriod: Boolean = true) : Sorting {
    RELEVANCE, HOT, TOP, NEW, COMMENTS;
}
