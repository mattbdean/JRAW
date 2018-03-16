package net.dean.jraw.pagination

import net.dean.jraw.Experimental

@Experimental
interface BackoffStrategy {
    /**
     * Returns the amount of milliseconds until the next request should be made.
     *
     * @param newItems The number of new items present in the existing response
     * @param totalItems The total number of items received in the response
     */
    fun delayRequest(newItems: Int, totalItems: Int): Long
}
