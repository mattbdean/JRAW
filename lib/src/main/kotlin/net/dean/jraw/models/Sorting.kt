package net.dean.jraw.models

/**
 * Represents how the reddit API chooses what it returns in a Paginator
 */
enum class Sorting constructor(val requiresTimePeriod: Boolean = false) {
    HOT,
    NEW,
    RISING,
    CONTROVERSIAL(true),
    TOP(true)
}
