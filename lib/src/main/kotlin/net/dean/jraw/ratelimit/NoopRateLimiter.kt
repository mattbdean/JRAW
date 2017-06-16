package net.dean.jraw.ratelimit

/**
 * A very simple RateLimiter implementation that doesn't do anything (no-operation) and grants permits whenever asked.
 *
 * Providing an instance of this class to another class that utilizes a RateLimiter (e.g. RedditClient) essentially
 * removes any rate limiting.
 */
class NoopRateLimiter : RateLimiter {
    // do nothing
    override fun acquire(permits: Long) {}
    override fun tryAcquire(permits: Long) = true
    override fun refill(permits: Long) {}
}
