package net.dean.jraw.pagination

import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.models.RedditObject

/**
 * This class, like its name suggests, supports fewer query modifiers compared to other Paginators. Only the limit can
 * be set.
 */
open class BarebonesPaginator<T : RedditObject> private constructor(
    reddit: RedditClient,
    baseUrl: String,
    limit: Int
) : Paginator<T, Paginator.Builder<T>>(reddit, baseUrl, limit) {

    override fun createNextRequest(): HttpRequest {
        val args = mutableMapOf("limit" to limit.toString())
        if (current?.after != null)
            args.put("after", current!!.after!!)

        return reddit.requestStub()
            .path(baseUrl)
            .query(args)
            .build()
    }

    override fun newBuilder() = Builder<T>(reddit, baseUrl)
        .limit(limit)

    class Builder<T : RedditObject>(reddit: RedditClient, baseUrl: String) :
        Paginator.Builder<T>(reddit, baseUrl) {

        private var limit: Int = Paginator.DEFAULT_LIMIT
        fun limit(limit: Int): Builder<T> { this.limit = limit; return this }

        override fun build(): BarebonesPaginator<T> =
            BarebonesPaginator(reddit, baseUrl, limit)
    }
}
