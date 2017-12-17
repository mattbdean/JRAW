package net.dean.jraw.pagination

import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.models.Sorting
import net.dean.jraw.models.TimePeriod

open class DefaultPaginator<T> private constructor(
    reddit: RedditClient,
    baseUrl: String,
    val sortingAlsoInPath: Boolean,
    val timePeriod: TimePeriod,
    val sorting: Sorting,
    limit: Int,
    clazz: Class<T>
) : Paginator<T>(reddit, baseUrl, limit, clazz) {

    override fun createNextRequest(): HttpRequest {
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
            .build()
    }

    open class Builder<T, S : Sorting>(
        reddit: RedditClient,
        baseUrl: String,

        /**
         * If true, the sorting will be included as a query parameter instead of a path parameter. Most endpoints
         * support (and require) specifying the sorting as a path parameter like this: `/r/pics/top?sort=top`. However,
         * other endpoints 404 when given a path like this, in which case the sorting will need to be specified via
         * query parameter only
         */
        protected var sortingAlsoInPath: Boolean = false,
        clazz: Class<T>
    ) : Paginator.Builder<T>(reddit, baseUrl, clazz) {
        protected var limit: Int = Paginator.DEFAULT_LIMIT
        protected var timePeriod: TimePeriod = Paginator.DEFAULT_TIME_PERIOD
        protected var sorting: S? = null

        fun limit(limit: Int): Builder<T, S> { this.limit = limit; return this }
        fun sorting(sorting: S): Builder<T, S> { this.sorting = sorting; return this }
        fun timePeriod(timePeriod: TimePeriod): Builder<T, S> { this.timePeriod = timePeriod; return this }

        override fun build(): DefaultPaginator<T> =
            DefaultPaginator(reddit, baseUrl, sortingAlsoInPath, timePeriod, sorting ?: Paginator.DEFAULT_SORTING, limit, clazz)

        companion object {
            inline fun <reified T, S : Sorting> create(reddit: RedditClient, baseUrl: String, sortingAlsoInPath: Boolean = false): Builder<T, S> {
                return Builder(reddit, baseUrl, sortingAlsoInPath, T::class.java)
            }
        }
    }
}
