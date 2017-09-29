package net.dean.jraw.ratelimit

/**
 * A time adapter using [System.nanoTime]
 */
class SystemTimeAdapter : TimeAdapter {
    override fun nanoTime(): Long = System.nanoTime()
}
