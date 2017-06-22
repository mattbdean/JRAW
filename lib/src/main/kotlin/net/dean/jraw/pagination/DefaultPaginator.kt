package net.dean.jraw.pagination

import net.dean.jraw.RedditClient
import net.dean.jraw.models.Sorting
import net.dean.jraw.models.Thing
import net.dean.jraw.models.TimePeriod

open class DefaultPaginator<T : Thing> private constructor(
    reddit: RedditClient,
    baseUrl: String,
    sortingAsPathParameter: Boolean,
    timePeriod: TimePeriod,
    sorting: Sorting,
    limit: Int
) : Paginator<T, Paginator.Builder<T>>(reddit, baseUrl, sortingAsPathParameter, timePeriod, sorting, limit) {

    override fun newBuilder() = Builder<T>(reddit, baseUrl, sortingAsPathParam)
        .sorting(sorting)
        .timePeriod(timePeriod)
        .limit(limit)

    class Builder<T : Thing>(reddit: RedditClient, baseUrl: String, sortingAsPathParameter: Boolean = false) :
        Paginator.Builder<T>(reddit, baseUrl, sortingAsPathParameter) {

        override fun build(): Paginator<T, Paginator.Builder<T>> =
            DefaultPaginator(reddit, baseUrl, sortingAsPathParam, timePeriod, sorting, limit)
    }
}
