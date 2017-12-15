package net.dean.jraw.models

/** A list of periods that can be used when sorting by [Sorting.TOP] */
enum class TimePeriod {
    /** Highest of the past 60 minutes */
    HOUR,

    /** Highest of the past 24 hours*/
    DAY,

    /** Highest of the past week */
    WEEK,

    /** Highest of the past month */
    MONTH,

    /** Highest of the past year*/
    YEAR,

    /** Highest of all time */
    ALL
}
