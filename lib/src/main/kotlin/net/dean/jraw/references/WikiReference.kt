package net.dean.jraw.references

import com.squareup.moshi.Types
import net.dean.jraw.*
import net.dean.jraw.models.Submission
import net.dean.jraw.models.WikiPage
import net.dean.jraw.models.WikiRevision
import net.dean.jraw.models.internal.RedditModelEnvelope
import net.dean.jraw.pagination.BarebonesPaginator

class WikiReference internal constructor(reddit: RedditClient, val subreddit: String) : AbstractReference(reddit) {
    /** Fetches the names of all accessible wiki pages. */
    @EndpointImplementation(Endpoint.GET_WIKI_PAGES)
    fun pages(): List<String> {
        val res = reddit.request { it.endpoint(Endpoint.GET_WIKI_PAGES, subreddit) }
        val adapter = JrawUtils.moshi.adapter<RedditModelEnvelope<List<String>>>(pagesType)
        return res.deserializeWith(adapter).data
    }

    /** Fetches information about a given wiki page */
    @EndpointImplementation(Endpoint.GET_WIKI_PAGE)
    fun page(name: String): WikiPage {
        return reddit.request {
            it.endpoint(Endpoint.GET_WIKI_PAGE, subreddit, name)
        }.deserializeEnveloped()
    }

    /** Updates a given wiki page */
    @EndpointImplementation(Endpoint.POST_WIKI_EDIT)
    fun update(page: String, content: String, reason: String = "") {
        reddit.request {
            it.endpoint(Endpoint.POST_WIKI_EDIT, subreddit)
                .post(mapOf(
                    "content" to content,
                    "page" to page,
                    "reason" to reason
                ))
        }
    }

    /** Returns a Paginator.Builder that iterates through all revisions of all wiki pages */
    @EndpointImplementation(Endpoint.GET_WIKI_REVISIONS, type = MethodType.NON_BLOCKING_CALL)
    fun revisions(): BarebonesPaginator.Builder<WikiRevision> =
        BarebonesPaginator.Builder.create(reddit, "/r/$subreddit/wiki/revisions")

    /** Returns a Paginator.Builder that iterates through all revisions of a specific wiki page */
    @EndpointImplementation(Endpoint.GET_WIKI_REVISIONS_PAGE, type = MethodType.NON_BLOCKING_CALL)
    fun revisionsFor(page: String): BarebonesPaginator.Builder<WikiRevision> =
        BarebonesPaginator.Builder.create(reddit, "/r/$subreddit/wiki/revisions/$page")

    /** Returns a Paginator.Builder that iterates through all Submissions that link to this specific wiki page */
    @EndpointImplementation(Endpoint.GET_WIKI_DISCUSSIONS_PAGE, type = MethodType.NON_BLOCKING_CALL)
    fun discussionsAbout(page: String): BarebonesPaginator.Builder<Submission> =
        BarebonesPaginator.Builder.create(reddit, "/r/$subreddit/wiki/discussions/$page")

    companion object {
        // RedditModelEnvelope<List<String>>
        private val pagesType = Types.newParameterizedType(RedditModelEnvelope::class.java,
            Types.newParameterizedType(List::class.java, String::class.java))
    }
}
