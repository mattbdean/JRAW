package net.dean.jraw.pagination

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.models.SearchSort
import net.dean.jraw.models.Submission
import net.dean.jraw.models.TimePeriod

/**
 * This class allows access to [Reddit search functionality](https://www.reddit.com/search).
 * Construct the [SearchPaginator] using [SearchPaginator.Builder] constructor or provided methods in its companion object.
 */
class SearchPaginator private constructor(
    reddit: RedditClient,
    baseUrl: String,
    val timePeriod: TimePeriod,
    val sorting: SearchSort,
    limit: Int,
    val syntax: QuerySyntax,
    val query: String
) : Paginator<Submission>(reddit, baseUrl, limit, Submission::class.java) {

    @EndpointImplementation(Endpoint.GET_SEARCH)
    override fun createNextRequest(): HttpRequest.Builder {
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

    /**
     * The syntax language of the provided [query]. Reddit's official documentation on different syntaxes is very sparse,
     * the latest version of the official search documentation doesn't mention the word 'syntax' at all, and the
     * API documentation just lists the available choices without any explanation.
     *
     * See
     * - [reddit search on Reddit Wiki](https://www.reddit.com/wiki/search)
     * - [reddit search API documentation](https://www.reddit.com/dev/api#GET_search)
     *
     * The last revision of the reddit search wiki page to describe [CLOUDSEARCH] can be found
     * [here](https://www.reddit.com/wiki/search?v=1d92a164-31a3-11e7-a834-0aab9d436d2a) which was posted around the time
     * [reddit announced](https://www.reddit.com/r/changelog/comments/694o34/reddit_search_performance_improvements/)
     * moving from "from the old Amazon CloudSearch domain to a new Amazon CloudSearch domain".
     */
    enum class QuerySyntax {
        /**
         * The defaul reddit search syntax, also named as "field search" which supports using field queries
         *
         * See [Field search](https://www.reddit.com/wiki/search#wiki_field_search)
         * */
        LUCENE,

        /**
         * Compound query language designed by Amazon for developers
         *
         * See
         * - [Constructing Compound Queries](http://docs.aws.amazon.com/cloudsearch/latest/developerguide/searching-compound-queries.html)
         * - [relevant Reddit wiki revision](https://www.reddit.com/wiki/search?v=1d92a164-31a3-11e7-a834-0aab9d436d2a)
         * */
        CLOUDSEARCH,

        /**
         * Plain text search - no field support, no compund queries. The search engine is looking for posts containing
         * the [query] text in their titles and self-post texts.
         */
        PLAIN
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
