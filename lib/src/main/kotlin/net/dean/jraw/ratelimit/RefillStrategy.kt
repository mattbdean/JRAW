package net.dean.jraw.ratelimit

import java.util.concurrent.TimeUnit

/**
 * Defines a method for refilling permits on a RateLimiter
 */
interface RefillStrategy {
    /** Calculates how many permits to give the RateLimiter based on how long it has been since the last refill */
    fun refill(): Long

    /**
     * Calculates the amount of time until the next time the RateLimiter should have permits added to it
     *
     * @param unit The returned value will be in this time unit
     */
    fun timeUntilNextRefill(unit: TimeUnit): Long
}

