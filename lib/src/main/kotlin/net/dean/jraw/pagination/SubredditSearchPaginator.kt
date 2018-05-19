package net.dean.jraw.pagination

import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.models.Subreddit
import net.dean.jraw.models.SubredditSearchSort
import net.dean.jraw.models.TimePeriod

/**
 * This specific paginator is dedicated to searching for subreddits. To create an instance, use
 * [RedditClient.searchSubreddits]
 */
class SubredditSearchPaginator private constructor(b: Builder) : DefaultPaginator<Subreddit>(
    reddit = b.reddit,
    sortingAlsoInPath = false,
    baseUrl = b.baseUrl,
    limit = b.limit,
    clazz = Subreddit::class.java,
    timePeriod = TimePeriod.ALL, // unused
    sorting = b.sorting
) {
    val query = b.query

    override fun createNextRequest(): HttpRequest.Builder {
        // Pretty much everything can be reused from DefaultPaginator
        val base = super.createNextRequest().build()

        // Add the 'q' query parameter
        return base.newBuilder()
            .configureUrl { it.setQueryParameter("q", query) }
    }

    /** Builder pattern implementation */
    class Builder(reddit: RedditClient, baseUrl: String = "/subreddits/search") : Paginator.Builder<Subreddit>(reddit, baseUrl, Subreddit::class.java) {
        internal var limit: Int = Paginator.DEFAULT_LIMIT
        internal var query: String = ""
        internal var sorting: SubredditSearchSort = SubredditSearchSort.RELEVANCE

        /**
         * Sets how many items to return in each page. The default is 25, and the maximum reddit allows is 100. If not
         * set, the built paginator will use the default value.
         *
         * @see Paginator.DEFAULT_LIMIT
         * @see Paginator.RECOMMENDED_MAX_LIMIT
         */
        fun limit(limit: Int): Builder { this.limit = limit; return this }

        /**
         * A term or terms to search for. Subreddits with this term in its name or description will be more likely to
         * appear in the results.
         */
        fun query(query: String): Builder { this.query = query; return this }

        /** How to sort the results. Defaults to [SubredditSearchSort.RELEVANCE]. */
        fun sorting(sorting: SubredditSearchSort): Builder { this.sorting = sorting; return this }

        override fun build(): SubredditSearchPaginator = SubredditSearchPaginator(this)
    }
}
