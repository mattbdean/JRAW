package net.dean.jraw.pagination

import net.dean.jraw.Experimental
import net.dean.jraw.models.UniquelyIdentifiable

/**
 * A Stream is special Iterator subclass that polls from a given source (usually a [Paginator]), and yields new data as
 * each item becomes available.
 *
 * Consider [next] to be a blocking operation if this Stream is given a data source that interacts with the network.
 */
@Experimental
class Stream<out T : UniquelyIdentifiable> @JvmOverloads constructor(
    private val dataSource: RedditIterable<T>,
    private val backoff: BackoffStrategy = ConstantBackoffStrategy(),
    historySize: Int = 500
) : Iterator<T> {

    /** Keeps track of the uniqueIds we've seen recently */
    private val history = RotatingSearchList<String>(historySize)
    private var currentIterator: Iterator<T>? = null
    private var resumeTimeMillis = -1L

    override fun hasNext(): Boolean = true

    override fun next(): T {
        val it = currentIterator
        if (it != null && it.hasNext()) {
            return it.next()
        }

        val new = requestNew()
        currentIterator = new
        return new.next()
    }

    private fun requestNew(): Iterator<T> {
        var newDataIterator: Iterator<T>? = null

        while (newDataIterator == null) {
            // Make sure to honor the backoff strategy
            if (resumeTimeMillis > System.currentTimeMillis()) {
                Thread.sleep(resumeTimeMillis - System.currentTimeMillis())
            }

            dataSource.restart()

            val (new, old) = dataSource.next().partition { history.contains(it.uniqueId) }
            old.forEach { history.add(it.uniqueId) }

            // Calculate at which time to poll for new data
            val backoffMillis = backoff.delayRequest(old.size, new.size + old.size)
            require(backoffMillis >= 0) { "delayRequest must return a non-negative integer, was $backoffMillis" }
            resumeTimeMillis = System.currentTimeMillis() + backoff.delayRequest(old.size, new.size + old.size)

            // Yield in reverse order so that if more than one unseen item is present the older items are yielded first
            if (old.isNotEmpty())
                newDataIterator = old.asReversed().iterator()
        }

        return newDataIterator
    }
}
