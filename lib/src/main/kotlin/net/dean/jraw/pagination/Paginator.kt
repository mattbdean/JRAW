package net.dean.jraw.pagination

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.databind.Enveloped
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.models.GeneralSort
import net.dean.jraw.models.Listing
import net.dean.jraw.models.Sorting
import net.dean.jraw.models.TimePeriod

/**
 * A Paginator is used to iterate over API endpoints that return a Listing. Paginator uses the builder pattern and once
 * built, its settings cannot be modified.
 */
abstract class Paginator<T> protected constructor(
    /** The client to use to request data */
    val reddit: RedditClient,

    /**
     * The path relative to reddit.com at which the Listing endpoint is located. Trailing slashes will be removed
     * automatically.
     */
    baseUrl: String,

    /**
     * How many items to return at once
     *
     * @see RECOMMENDED_MAX_LIMIT
     */
    val limit: Int,

    /** The type of data that is contained in each Listing */
    clazz: Class<T>
) : RedditIterable<T> {
    // Internal, modifiable properties
    private var _current: Listing<T>? = null
    private var _pageNumber = 0
    private val adapter: JsonAdapter<Listing<T>>

    /** The path relative to reddit.com at which the Listing endpoint is located. */
    val baseUrl: String

    init {
        val type = Types.newParameterizedType(Listing::class.java, clazz)
        adapter = JrawUtils.moshi.adapter(type, Enveloped::class.java)

        // Make sure we don't have a trailing slash
        this.baseUrl = baseUrl.removeSuffix("/")
    }

    // Publicly available property is simply an unmodifiable alias to the private properties

    /** Returns a reference to the last requested Listing, or null if there is none */
    override val current: Listing<T>?
        get() = _current

    /** Returns the current page number. If no requests have been sent, this value is 1. */
    override val pageNumber: Int
        get() = _pageNumber

    override fun next(): Listing<T> {
        _current = reddit.request(createNextRequest()).deserializeWith(adapter)
        _pageNumber++

        return _current!!
    }

    override fun restart() {
        this._current = null
        this._pageNumber = 0
    }

    /** */
    override fun iterator(): Iterator<Listing<T>> = object: Iterator<Listing<T>> {
        override fun hasNext() = !hasStarted() || (_current != null && _current!!.nextName != null)
        override fun next() = this@Paginator.next()
    }

    override fun hasStarted(): Boolean = _current != null && _pageNumber > 0

    override fun accumulate(maxPages: Int): List<Listing<T>> {
        val lists = mutableListOf<Listing<T>>()

        if (maxPages < -1)
            throw IllegalArgumentException("Expecting maxPages to be -1 or greater")

        var i = 0
        val it = iterator()
        while ((maxPages == -1 || ++i <= maxPages) && it.hasNext())
            lists.add(it.next())
        return lists
    }

    override fun accumulateMerged(maxPages: Int): List<T> = accumulate(maxPages).flatten()

    /** Creates an HTTP request to fetch the next page of data, if any. */
    abstract protected fun createNextRequest(): HttpRequest

    /**
     * Base for all Paginator.Builder subclasses
     */
    abstract class Builder<T>(
        /** The RedditClient used to send requests */
        val reddit: RedditClient,

        /**
         * The path relative to reddit.com at which the Listing endpoint is located. Trailing slashes will be removed
         * automatically.
         */
        val baseUrl: String,

        /** The type of data that is contained in each Listing */
        protected val clazz: Class<T>
    ) {
        /** Creates a new Paginator */
        abstract fun build(): Paginator<T>
    }

    /** */
    companion object {
        /**
         * The recommended maximum limit of Things to return. No client-side code is in place to ensure that the limit is
         * not set over this number, but the Reddit API will only return this many amount of objects.
         */
        const val RECOMMENDED_MAX_LIMIT = 100

        /** reddit returns 25 items when no limit parameter is passed */
        const val DEFAULT_LIMIT = 25

        /** The sorting reddit uses when none is specified */
        @JvmField val DEFAULT_SORTING: Sorting = GeneralSort.NEW

        /** The time period reddit uses when none is specified */
        @JvmField val DEFAULT_TIME_PERIOD = TimePeriod.DAY
    }
}
