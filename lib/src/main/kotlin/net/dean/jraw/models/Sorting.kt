package net.dean.jraw.models

/**
 * Represents how the reddit API chooses what it returns in a Paginator
 *
 * @property requiresTimePeriod If this sorting method must also include a [TimePeriod]. For example, sorting by [TOP]
 * is ambiguous, reddit doesn't know what posts to include when sorting. Sorting by [TOP] of the past [TimePeriod.HOUR]
 * is not ambiguous.
 */
enum class Sorting constructor(val requiresTimePeriod: Boolean = false) {
    /** Popular posts, takes into account score, age, and other factors. */
    HOT,

    /** Posts that include gold */
    GILDED,

    /** New posts */
    NEW,

    /** Somewhat new posts that have a lot of karma relative to others posted at the same time */
    RISING,

    /** Posts that received a lot of downvotes */
    CONTROVERSIAL(true),

    /** Posts that received the most upvotes */
    TOP(true)
}
