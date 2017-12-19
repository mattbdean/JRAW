package net.dean.jraw.ratelimit

/**
 * Abstraction for getting the time in nanoseconds the JVM has been up and running.
 *
 * This abstraction exists almost entirely for mocking purposes and non-test environments should undoubtedly use
 * [SystemTimeAdapter].
 */
interface TimeAdapter {
    /** Returns the amount of nanoseconds this program has been running */
    fun nanoTime(): Long
}
