package net.dean.jraw.pagination

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.models.SearchSort
import net.dean.jraw.models.Submission
import net.dean.jraw.models.TimePeriod

/**
 * This class allows access to <a href="https://www.reddit.com/search">Reddit's search functionality</a>.
 * Construct the [SearchPaginator] using [SearchPaginator.Builder] constructor or provided methods in its companion object.
 */
open class SearchPaginator private constructor(
    reddit: RedditClient,
    baseUrl: String,
    val timePeriod: TimePeriod,
    val sorting: SearchSort,
    limit: Int,
    val syntax: QuerySyntax,
    val query: String
) : Paginator<Submission>(reddit, baseUrl, limit, Submission::class.java) {

    @EndpointImplementation(Endpoint.GET_SEARCH)
    override fun createNextRequest(): HttpRequest {
        val args: MutableMap<String, String> = mutableMapOf(
            "limit" to limit.toString(radix = 10),
            "q" to query,
            "restrict_sr" to (baseUrl.length > "/search".length).toString(),
            "sort" to sorting.name.toLowerCase(),
            "t" to timePeriod.name.toLowerCase(),
            // exclude users and subreddits from the search results
            "type" to "link",
            "syntax" to syntax.name.toLowerCase()
        )

        if (current?.nextName != null)
            args.put("after", current!!.nextName!!)

        return reddit.requestStub()
            .path(baseUrl)
            .query(args)
            .build()
    }

    open class Builder(
        reddit: RedditClient,
        baseUrl: String
    ) : Paginator.Builder<Submission>(reddit, baseUrl, Submission::class.java) {

        protected var limit: Int = Paginator.DEFAULT_LIMIT
        protected var timePeriod = TimePeriod.ALL
        protected var sorting = SearchSort.RELEVANCE
        protected var syntax = QuerySyntax.LUCENE
        protected var query = ""

        fun limit(limit: Int): Builder { this.limit = limit; return this }
        fun sorting(sorting: SearchSort): Builder { this.sorting = sorting; return this }
        fun timePeriod(timePeriod: TimePeriod): Builder { this.timePeriod = timePeriod; return this }
        fun syntax(syntax: QuerySyntax): Builder { this.syntax = syntax; return this }
        fun query(query: String): Builder { this.query = query; return this }

        override fun build(): SearchPaginator = SearchPaginator(reddit, baseUrl, timePeriod, sorting, limit, syntax, query)
    }

    enum class QuerySyntax {
        CLOUDSEARCH, LUCENE, PLAIN
    }

    companion object {
        private val baseUrlSuffix = "/search"

        fun inSubreddits(reddit: RedditClient, vararg subreddits: String): Builder {
            if (subreddits.isEmpty())
                return everywhere(reddit)

            val prefix = subreddits.joinToString(prefix = "/r/", separator = "+")
            return Builder(reddit, prefix + baseUrlSuffix)
        }

        fun everywhere(reddit: RedditClient): Builder = Builder(reddit, baseUrlSuffix)
    }
}
