package net.dean.jraw.ratelimit

/**
 * The purpose of a rate limiter is to control how often some block of code can execute.
 *
 * RateLimiters distribute permits at a particular rate. A permit can be acquired via [acquire] or [tryAcquire], the
 * difference being that [acquire] blocks the current thread while [tryAcquire] returns if a permit could be reserved.
 *
 * This interface is heavily based off of Guava's RateLimiter class.
 *
 * It is recommended to extend from [AbstractRateLimiter] instead of this interface directly.
 *
 * @see LeakyBucketRateLimiter
 */
interface RateLimiter {

    /**
     * Acquires a permit. If no permits are available, this method blocks until one is.
     */
    fun acquire(permits: Long = 1)

    /**
     * Tries to acquire a permit. If the amount of permits requested are available, true is returned and those permits
     * are allocated. If there aren't enough permits, false is returned.
     */
    fun tryAcquire(permits: Long = 1): Boolean

    /**
     * Manually adds some permits.
     */
    fun refill(permits: Long)
}
