package net.dean.jraw.pagination

import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.models.RedditObject
import net.dean.jraw.models.Sorting
import net.dean.jraw.models.TimePeriod

open class DefaultPaginator<T : RedditObject> private constructor(
    reddit: RedditClient,
    baseUrl: String,
    val sortingAlsoInPath: Boolean,
    val timePeriod: TimePeriod,
    val sorting: Sorting,
    limit: Int
) : Paginator<T, Paginator.Builder<T>>(reddit, baseUrl, limit) {

    override fun createNextRequest(): HttpRequest {
        val sortingString = sorting.name.toLowerCase()
        val args: MutableMap<String, String> = mutableMapOf(
            "limit" to limit.toString(radix = 10),
            "sort" to sortingString
        )

        if (sorting.requiresTimePeriod)
            args.put("t", timePeriod.name.toLowerCase())

        if (current?.after != null)
            args.put("after", current!!.after!!)

        val path = if (sortingAlsoInPath) "$baseUrl/$sortingString" else baseUrl

        return reddit.requestStub()
            .path(path)
            .query(args)
            .build()
    }

    override fun newBuilder() = Builder<T>(reddit, baseUrl)
        .sorting(sorting)
        .timePeriod(timePeriod)
        .limit(limit)

    open class Builder<T : RedditObject>(
        reddit: RedditClient, baseUrl: String,

        /**
         * If true, the sorting will be included as a query parameter instead of a path parameter. Most endpoints
         * support (and require) specifying the sorting as a path parameter like this: `/r/pics/top?sort=top`. However,
         * other endpoints 404 when given a path like this, in which case the sorting will need to be specified via
         * query parameter only
         */
        protected var sortingAlsoInPath: Boolean = false
    ) : Paginator.Builder<T>(reddit, baseUrl) {
        protected var limit: Int = Paginator.DEFAULT_LIMIT
        protected var timePeriod: TimePeriod = Paginator.DEFAULT_TIME_PERIOD
        protected var sorting = Paginator.DEFAULT_SORTING

        fun limit(limit: Int): Builder<T> { this.limit = limit; return this }
        fun sorting(sorting: Sorting): Builder<T> { this.sorting = sorting; return this }
        fun timePeriod(timePeriod: TimePeriod): Builder<T> { this.timePeriod = timePeriod; return this }

        override fun build(): DefaultPaginator<T> =
            DefaultPaginator(reddit, baseUrl, sortingAlsoInPath, timePeriod, sorting, limit)
    }
}
