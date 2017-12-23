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

