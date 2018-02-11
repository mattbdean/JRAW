package net.dean.jraw.ratelimit

import java.util.concurrent.TimeUnit

/**
 * The goal of this class is to fill in some boilerplate when it comes to creating RateLimiter implementations. This
 * class overrides the [acquire] method with a generic implementation.
 */
abstract class AbstractRateLimiter : RateLimiter {
    override fun acquire(permits: Long) {
        while (true) {
            if (tryAcquire(permits)) break

            // Sleep for the smallest unit of time possible so we allow other threads to run
            suspend(1, TimeUnit.NANOSECONDS)
        }
    }

    /** Adapted (read: copied) from Guava's Uninterruptibles.sleepUninterruptibly() */
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
