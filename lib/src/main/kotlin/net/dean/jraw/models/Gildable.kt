package net.dean.jraw.models

/**
 * Common interface for models that can receive reddit Gold
 */
interface Gildable {
    /** If the currently-logged-in-user can give reddit Gold to this model */
    val canGild: Boolean

    /** How many times this model has received reddit Gold */
    val gilded: Short
}
