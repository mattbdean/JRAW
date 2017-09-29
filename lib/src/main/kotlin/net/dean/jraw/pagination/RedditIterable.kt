package net.dean.jraw.pagination

import net.dean.jraw.models.Listing

/**
 * A standard interface for interacting with paginated data provided by the reddit API.
 */
interface RedditIterable<T> : Iterable<Listing<T>> {
    /** The most recently fetched Listing, or null if no work has been done yet. */
    val current: Listing<T>?

    /** The current page number. 0 = not started, 1 = first page, etc. */
    val pageNumber: Int

    /** Gets the next page */
    fun next(): Listing<T>

    /** Resets [current] and [pageNumber] so iteration can start at the first page again */
    fun restart(): Unit

    /** Returns true if iteration has not been started */
    fun hasStarted(): Boolean

    /**
     * Creates a List of Listings whose size is less than or equal to `maxPages`.
     *
     * The amount of time this method takes to return will grow linearly based on the maximum number of pages, as there
     * will be one request for each new page.
     *
     * @param maxPages The maximum amount of pages to retrieve
     */
    fun accumulate(maxPages: Int): List<Listing<T>>

    /** Does the same thing as [accumulate], but merges all Listing children into one List */
    fun accumulateMerged(maxPages: Int): List<T>
}
