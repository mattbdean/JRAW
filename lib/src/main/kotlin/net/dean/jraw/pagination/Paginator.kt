package net.dean.jraw.pagination

import com.squareup.moshi.Types
import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.databind.Enveloped
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.models.Listing
import net.dean.jraw.models.Sorting
import net.dean.jraw.models.TimePeriod

abstract class Paginator<T, out B : Paginator.Builder<T>> protected constructor(
    val reddit: RedditClient,
    baseUrl: String,
    val limit: Int,
    protected val clazz: Class<T>
) : RedditIterable<T> {
    // Internal, modifiable properties
    private var _current: Listing<T>? = null
    private var _pageNumber = 0

    // Make sure we don't have a trailing slash
    val baseUrl = if (baseUrl.trim().endsWith("/")) baseUrl.trim().substring(0, baseUrl.trim().length - 2) else baseUrl

    // Publicly available property is simply an unmodifiable alias to the private properties
    override val current: Listing<T>?
        get() = _current
    override val pageNumber: Int
        get() = _pageNumber

    override fun next(): Listing<T> {
        val adapter = JrawUtils.moshi.adapter<Listing<T>>(Types.newParameterizedType(Listing::class.java, clazz), Enveloped::class.java)
        _current = reddit.request(createNextRequest()).deserializeWith(adapter)
        _pageNumber++

        return _current!!
    }

    override fun restart() {
        this._current = null
        this._pageNumber = 0
    }

    override fun iterator(): Iterator<Listing<T>> = object: Iterator<Listing<T>> {
        override fun hasNext() = !hasStarted() || (_current != null && _current!!.nextName != null)
        override fun next() = this@Paginator.next()
    }

    override fun hasStarted(): Boolean = _current != null && _pageNumber > 0

    override fun accumulate(maxPages: Int): List<Listing<T>> {
        val lists = mutableListOf<Listing<T>>()

        var i = 0
        val it = iterator()
        while (++i <= maxPages && it.hasNext())
            lists.add(it.next())
        return lists
    }

    override fun accumulateMerged(maxPages: Int): List<T> = accumulate(maxPages).flatten()

    /** Constructs a new [Builder] with the current pagination settings */
    abstract fun newBuilder(): B

    /** Creates an HTTP request to fetch the next page of data, if any. */
    abstract protected fun createNextRequest(): HttpRequest

    /**
     * Base for all Paginator.Builder subclasses
     */
    abstract class Builder<T>(
        val reddit: RedditClient,
        val baseUrl: String,
        protected val clazz: Class<T>
    ) {
        abstract fun build(): Paginator<T, Builder<T>>
    }

    companion object {
        /**
         * The recommended maximum limit of Things to return. No client-side code is in place to ensure that the limit is
         * not set over this number, but the Reddit API will only return this many amount of objects.
         */
        const val RECOMMENDED_MAX_LIMIT = 100

        /** reddit returns 25 items when no limit parameter is passed */
        const val DEFAULT_LIMIT = 25

        @JvmField val DEFAULT_SORTING = Sorting.NEW
        @JvmField val DEFAULT_TIME_PERIOD = TimePeriod.DAY
    }
}
