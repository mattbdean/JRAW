package net.dean.jraw.pagination

import net.dean.jraw.RedditClient
import net.dean.jraw.models.Listing
import net.dean.jraw.models.Sorting
import net.dean.jraw.models.Thing
import net.dean.jraw.models.TimePeriod
import net.dean.jraw.references.AbstractReference

class Paginator<T : Thing> private constructor(
    reddit: RedditClient,
    baseUrl: String,
    val timePeriod: TimePeriod,
    val sorting: Sorting,
    val limit: Int
) : AbstractReference<String>(reddit, baseUrl), Iterable<Listing<T>> {

    private var _current: Listing<T>? = null
    private var _pageNumber = 0

    /** The most recently fetched Listing, or null if no work has been done yet. */
    val current: Listing<T>?
        get() = _current

    /** The current page number. 0 = not started, 1 = first page, etc. */
    val pageNumber: Int
        get() = _pageNumber

    fun next(): Listing<T> {
        val args: MutableMap<String, String> = mutableMapOf(
            "limit" to limit.toString(radix = 10)
        )

        if (sorting.requiresTimePeriod)
            args.put("t", timePeriod.name.toLowerCase())

        if (_current != null && _current!!.after != null)
            args.put("after", _current!!.after!!)


        val response = reddit.request {
            it.path("$subject/${sorting.name.toLowerCase()}")
                .query(args)
        }

        _current = response.deserialize()
        _pageNumber++

        return _current!!
    }

    /** Resets [current] and [pageNumber] so iteration can start at the first page again */
    fun restart() {
        this._current = null
        this._pageNumber = 0
    }

    /**
     * Constructs a new [Builder] with the current pagination settings
     */
    fun newBuilder() = Builder<T>(reddit, subject)
        .sorting(sorting)
        .timePeriod(timePeriod)
        .limit(limit)

    override fun iterator(): Iterator<Listing<T>> = object: Iterator<Listing<T>> {
        override fun hasNext() = _current != null && _current!!.after != null
        override fun next() = this@Paginator.next()
    }

    class Builder<T : Thing> internal constructor(val reddit: RedditClient, val baseUrl: String) {
        private var timePeriod: TimePeriod = DEFAULT_TIME_PERIOD
        private var sorting = DEFAULT_SORTING
        private var limit = DEFAULT_LIMIT // reddit returns 25 items when no limit parameter is passed

        fun sorting(sorting: Sorting): Builder<T> { this.sorting = sorting; return this }
        fun timePeriod(timePeriod: TimePeriod): Builder<T> { this.timePeriod = timePeriod; return this }
        fun limit(limit: Int): Builder<T> { this.limit = limit; return this }

        fun build() = Paginator<T>(reddit, baseUrl, timePeriod, sorting, limit)
    }

    companion object {
        /**
         * The recommended maximum limit of Things to return. No client-side code is in place to ensure that the limit is
         * not set over this number, but the Reddit API will only return this many amount of objects.
         */
        const val RECOMMENDED_MAX_LIMIT = 100
        const val DEFAULT_LIMIT = 25
        @JvmField val DEFAULT_SORTING = Sorting.NEW
        @JvmField val DEFAULT_TIME_PERIOD = TimePeriod.DAY
    }
}
