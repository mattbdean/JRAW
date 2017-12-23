package net.dean.jraw.models

internal enum class GeneralSort(override val requiresTimePeriod: Boolean = false) : Sorting {
    /** Used only internally as a default sort */
    NEW
}
