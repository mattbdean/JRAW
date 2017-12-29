package net.dean.jraw.models

/**
 * Represents how the reddit API chooses what it returns in a Paginator
 */
interface Sorting {

    /**
     * If this sorting method must also include a [TimePeriod]. For example, sorting by [SubredditSort.TOP]
     * is ambiguous, reddit doesn't know what posts to include when sorting. Sorting by [SubredditSort.TOP] of the past [TimePeriod.HOUR]
     * is not ambiguous.
     */
    val requiresTimePeriod: Boolean

    val name: String
}

