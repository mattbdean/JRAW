package net.dean.jraw.pagination

import net.dean.jraw.models.Listing
import net.dean.jraw.models.Thing

/**
 * A standard interface for interacting with paginated data provided by the reddit API.
 */
interface RedditIterable<T : Thing> : Iterable<Listing<T>> {
    /** The most recently fetched Listing, or null if no work has been done yet. */
    val current: Listing<T>?

    /** The current page number. 0 = not started, 1 = first page, etc. */
    val pageNumber: Int

    /** Gets the next page */
    fun next(): Listing<T>

    /** Resets [current] and [pageNumber] so iteration can start at the first page again */
    fun restart(): Unit
}
