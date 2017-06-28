package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.ratelimit.FixedIntervalRefillStrategy
import net.dean.jraw.ratelimit.LeakyBucketRateLimiter
import net.dean.jraw.ratelimit.RateLimiter
import net.dean.jraw.ratelimit.TimeAdapter
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class RateLimiterTest : Spek({
    var rate: RateLimiter by Delegates.notNull()
    val time = MockTimeAdapter()

    // Have up to 10 permits
    val capacity = 10L

    // We're allotted 1 permit every second. Do not change from 1. Tests depend that time period = frequency to make the
    // math simpler
    val permitsPerPeriod = 1L
    val timeUnit = TimeUnit.SECONDS

    beforeEachTest {
        val refillStrategy = FixedIntervalRefillStrategy(permitsPerPeriod, timeUnit, time)
        rate = LeakyBucketRateLimiter(capacity, refillStrategy)
        time.now = 0L
    }

    describe("tryAcquire") {
        it("should start with no permits") {
            rate.tryAcquire().should.be.`false`
        }

        it("should only return true when it has enough permits") {
            rate.refill(capacity)

            // Try to consume one token, we have at least [capacity] permits left
            rate.tryAcquire(1).should.be.`true`
            // Try to consume the rest of the permits
            rate.tryAcquire(capacity - 1).should.be.`true`
            // No more permits, can't acquire one until the next time period
            rate.tryAcquire().should.be.`false`

            // Advance one time unit, should have refilled by now
            time.advance(permitsPerPeriod, TimeUnit.SECONDS)
            rate.tryAcquire(permitsPerPeriod).should.be.`true`
            // No more permits
            rate.tryAcquire().should.be.`false`
        }

        it("should not fill over capacity") {
            // Advance time some crazy amount so we be sure that it won't overflow
            time.advance(1, TimeUnit.DAYS)
            rate.tryAcquire(capacity).should.be.`true`
            rate.tryAcquire(1).should.be.`false`
        }
    }
})

class MockTimeAdapter : TimeAdapter {
    var now: Long = 0

    fun advance(amount: Long, unit: TimeUnit) {
        now += TimeUnit.NANOSECONDS.convert(amount, unit)
    }

    override fun nanoTime() = now
}
