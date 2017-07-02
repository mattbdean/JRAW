package net.dean.jraw.ratelimit

import java.util.concurrent.TimeUnit

/**
 * This class implements a RateLimiter with a flexible version of the
 * [leaky bucket algorithm](https://en.wikipedia.org/wiki/Leaky_bucket)
 *
 * A LeakyBucketRateLimiter starts with zero permits, gaining them over time based on its [refillStrategy]. The most
 * common refill strategy is [FixedIntervalRefillStrategy], which refills the bucket up to its capacity at (like the
 * name suggests) a fixed, configurable interval.
 *
 * This class allows for what is known as "bursting" via the [capacity] property. For example, consider an instance with
 * a capacity of 5 permits using a refill strategy that adds one permit to the bucket every second. If 5 seconds go by
 * and no permits are acquired, it is possible to then spend all 5 permits in less than one second.
 */
class LeakyBucketRateLimiter(
    /**
     * The maximum amount of permits to hold at one time. Setting this value to 1 guarantees that **all** permits are
     * distributed at the rate prescribed by [refillStrategy]. A value greater than 1 allows "bursting" of permit
     * acquisitions.
     */
    val capacity: Long,

    /** How the bucket will get refilled */
    val refillStrategy: RefillStrategy
) : RateLimiter {

    /** Creates an instance using a [FixedIntervalRefillStrategy]. */
    constructor(capacity: Long, permitsPerPeriod: Long, unit: TimeUnit) : this(
        capacity = capacity,
        refillStrategy = FixedIntervalRefillStrategy(permitsPerPeriod, unit)
    )

    private val initialPermits = 0L
    private var size = initialPermits

    init {
        if (capacity <= 0) throw IllegalArgumentException("expecting a permit capacity > 0")
        if (initialPermits > capacity) throw IllegalArgumentException("initialPermits cannot be higher than capacity")
    }

    override fun acquire(permits: Long) {
        while (true) {
            if (tryAcquire(permits)) break

            // Sleep for the smallest unit of time possible so we allow other threads to run
            suspend(1, TimeUnit.NANOSECONDS)
        }
    }

    override fun tryAcquire(permits: Long): Boolean {
        if (permits <= 0) throw IllegalArgumentException("permits must be above 0")
        if (permits > capacity) throw IllegalArgumentException("permits must be below capacity ($capacity)")

        // Get caught up on how many permits we actually have
        refill(refillStrategy.refill())

        if (permits <= size) {
            size -= permits
            return true
        }

        return false
    }

    override fun refill(permits: Long) {
        val newPermits = Math.min(capacity, Math.max(0, permits))
        size = Math.max(0, Math.min(size + newPermits, capacity))
    }

    /**
     * Adapted (read: copied) from Guava's Uninterruptibles.sleepUninterruptibly()
     */
    private fun suspend(sleepFor: Long, unit: TimeUnit) {
        var interrupted = false
        try {
            var remainingNanos = unit.toNanos(sleepFor)
            val end = System.nanoTime() + remainingNanos
            while (true) {
                try {
                    // TimeUnit.suspend() treats negative timeouts just like zero.
                    TimeUnit.NANOSECONDS.sleep(remainingNanos)
                    return
                } catch (e: InterruptedException) {
                    interrupted = true
                    remainingNanos = end - System.nanoTime()
                }

            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt()
            }
        }
    }
}

