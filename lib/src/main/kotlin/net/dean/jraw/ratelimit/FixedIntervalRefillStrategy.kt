package net.dean.jraw.ratelimit

import java.util.concurrent.TimeUnit

/**
 * Refills a permit bucket at a fixed, configurable interval
 */
class FixedIntervalRefillStrategy internal constructor(
    /** How many permits to add to the bucket every time one `unit` goes by */
    val permitsPerPeriod: Long,
    unit: TimeUnit,
    private val timeAdapter: TimeAdapter
) : RefillStrategy {
    /**
     * @param permitsPerPeriod The amount of permits issued per one unit of time
     * @param unit The unit of time used by [permitsPerPeriod]
     */
    constructor(permitsPerPeriod: Long, unit: TimeUnit) : this(permitsPerPeriod, unit, SystemTimeAdapter())

    private val durationNanos = unit.toNanos(permitsPerPeriod)
    private var lastRefillTime: Long
    private var nextRefillTime: Long

    private val lock = Any()

    init {
        val now = timeAdapter.nanoTime()
        lastRefillTime = now
        nextRefillTime = now + durationNanos
    }

    override fun refill(): Long = synchronized(lock) {
        val now = timeAdapter.nanoTime()
        if (now < nextRefillTime) return 0

        // Calculate how many time periods have elapsed
        val numPeriods = Math.max(0, (now - lastRefillTime) / durationNanos)

        // Move up the last refill time by that many periods
        lastRefillTime += numPeriods * durationNanos

        // Update the next refill time to 1 time period in the future
        nextRefillTime = lastRefillTime + durationNanos

        return numPeriods * permitsPerPeriod
    }

    override fun timeUntilNextRefill(unit: TimeUnit): Long {
        val now = timeAdapter.nanoTime()
        return unit.convert(Math.max(0, nextRefillTime - now), TimeUnit.NANOSECONDS)
    }
}
