package net.dean.jraw.pagination

import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.models.Sorting
import net.dean.jraw.models.TimePeriod
import net.dean.jraw.models.UniquelyIdentifiable
import net.dean.jraw.pagination.DefaultPaginator.Builder

/**
 * This Paginator is for paginated API endpoints that support not only limits, but sortings and time periods
 * (e.g. /top).
 */
open class DefaultPaginator<T : UniquelyIdentifiable> protected constructor(
    reddit: RedditClient,
    baseUrl: String,

    /** See [Builder.sortingAlsoInPath] */
    private val sortingAlsoInPath: Boolean,

    /** See [Builder.timePeriod] */
    val timePeriod: TimePeriod,

    /** See [Builder.sorting] */
    val sorting: Sorting,
    limit: Int,
    clazz: Class<T>
) : Paginator<T>(reddit, baseUrl, limit, clazz) {

    override fun createNextRequest(): HttpRequest.Builder {
        val sortingString = sorting.name.toLowerCase()
        val args: MutableMap<String, String> = mutableMapOf(
            "limit" to limit.toString(radix = 10),
            "sort" to sortingString
        )

        if (sorting.requiresTimePeriod)
            args.put("t", timePeriod.name.toLowerCase())

        if (current?.nextName != null)
            args.put("after", current!!.nextName!!)

        val path = if (sortingAlsoInPath) "$baseUrl/$sortingString" else baseUrl

        return reddit.requestStub()
            .path(path)
            .query(args)
    }

    /** Builder pattern for this class */
    open class Builder<T : UniquelyIdentifiable, S : Sorting>(
        reddit: RedditClient,
        baseUrl: String,

        /**
         * If true, the sorting will be included as a query parameter instead of a path parameter. Most endpoints
         * support (and require) specifying the sorting as a path parameter like this: `/r/pics/top?sort=top`. However,
         * other endpoints 404 when given a path like this, in which case the sorting will need to be specified via
         * query parameter only
         */
        private var sortingAlsoInPath: Boolean = false,
        clazz: Class<T>
    ) : Paginator.Builder<T>(reddit, baseUrl, clazz) {
        private var limit: Int = Paginator.DEFAULT_LIMIT
        private var timePeriod: TimePeriod = Paginator.DEFAULT_TIME_PERIOD
        private var sorting: S? = null

        /** Sets the limit */
        fun limit(limit: Int): Builder<T, S> { this.limit = limit; return this }
        /** Sets the sorting */
        fun sorting(sorting: S): Builder<T, S> { this.sorting = sorting; return this }
        /** Sets the time period */
        fun timePeriod(timePeriod: TimePeriod): Builder<T, S> { this.timePeriod = timePeriod; return this }

        override fun build(): DefaultPaginator<T> =
            DefaultPaginator(reddit, baseUrl, sortingAlsoInPath, timePeriod, sorting ?: Paginator.DEFAULT_SORTING, limit, clazz)

        /** */
        companion object {
            /** Convenience factory function using reified generics */
            inline fun <reified T : UniquelyIdentifiable, S : Sorting> create(
                reddit: RedditClient,
                baseUrl: String,
                sortingAlsoInPath: Boolean = false
            ): Builder<T, S> {
                return Builder(reddit, baseUrl, sortingAlsoInPath, T::class.java)
            }
        }
    }
}
