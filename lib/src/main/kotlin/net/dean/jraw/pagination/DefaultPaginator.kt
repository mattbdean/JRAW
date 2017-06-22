package net.dean.jraw.pagination

import net.dean.jraw.RedditClient
import net.dean.jraw.models.Sorting
import net.dean.jraw.models.Thing
import net.dean.jraw.models.TimePeriod

open class DefaultPaginator<T : Thing> private constructor(
    reddit: RedditClient,
    baseUrl: String,
    timePeriod: TimePeriod,
    sorting: Sorting,
    limit: Int
) : Paginator<T, Paginator.Builder<T>>(reddit, baseUrl, timePeriod, sorting, limit) {

    override fun newBuilder() = Builder<T>(reddit, baseUrl)
        .sorting(sorting)
        .timePeriod(timePeriod)
        .limit(limit)

    class Builder<T : Thing>(reddit: RedditClient, baseUrl: String) : Paginator.Builder<T>(reddit, baseUrl) {
        override fun build(): Paginator<T, Paginator.Builder<T>> =
            DefaultPaginator(reddit, baseUrl, timePeriod, sorting, limit)
    }
}
