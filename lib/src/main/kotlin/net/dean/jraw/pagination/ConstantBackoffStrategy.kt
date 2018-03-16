package net.dean.jraw.pagination

/**
 * A ConstantBackoffStrategy is probably a misnomer because this class never "backs off," but instead continues to make
 * requests at a constant rate regardless of how many new items were discovered.
 *
 * @param millis The amount of milliseconds to wait between each request. Defaults to 1000 (1 second). Note that
 * the RedditClient's rate limiting will overpower if it comes to that.
 */
class ConstantBackoffStrategy(val millis: Long = 1000L) : BackoffStrategy {
    override fun delayRequest(newItems: Int, totalItems: Int): Long = millis
}
