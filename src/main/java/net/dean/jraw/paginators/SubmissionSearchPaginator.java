package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

import java.util.Map;

/**
 * This class allows access to <a href="https://www.reddit.com/search">Reddit's search functionality</a>. This class
 * uses a special enum for sorting the values retrieved called {@link SearchSort}. To set this new sorting, use
 * {@link #setSearchSorting(SearchSort)}.
 */
public class SubmissionSearchPaginator extends Paginator<Submission> {
    public static final SearchSort DEFAULT_SORTING = SearchSort.RELEVANCE;
    private SearchSort sorting;
    private String subreddit;
    private String query;

    /**
     * Instantiates a new Paginator
     *
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param query   What to search for
     */
    public SubmissionSearchPaginator(RedditClient creator, String query) {
        super(creator, Submission.class);
        this.query = query;
        this.sorting = DEFAULT_SORTING;
    }

    @Override
    @EndpointImplementation(Endpoints.SEARCH)
    public Listing<Submission> next(boolean forceNetwork) {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.next(forceNetwork);
    }

    @Override
    protected String getBaseUri() {
        String base = "/search";
        if (subreddit != null) {
            base = "/r/" + subreddit + base;
        }
        return base;
    }

    @Override
    protected Map<String, String> getExtraQueryArgs() {
        return JrawUtils.mapOf(
                "q", query,
                "restrict_sr", subreddit == null ? "off" : "on",
                "sort", sorting.name().toLowerCase()
        );
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
        invalidate();
    }

    /**
     * Gets the query this SearchPaginator is searching for.
     * @return The query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Sets the query and invalidates the paginator.
     * @param query The new query
     */
    public void setQuery(String query) {
        this.query = query;
        invalidate();
    }

    @Override
    public void setSorting(Sorting sorting) {
        throw new UnsupportedOperationException("Use setSearchSorting(SearchSort)");
    }

    /**
     * Sets the new sorting and invalidates the paginator
     * @param sorting The new sorting
     */
    public void setSearchSorting(SearchSort sorting) {
        this.sorting = sorting;
        invalidate();
    }

    /**
     * Gets the current sorting
     * @return The current sorting
     */
    public SearchSort getSearchSorting() {
        return sorting;
    }

    /**
     * How the search results can be sorted
     */
    public static enum SearchSort {
        NEW,
        HOT,
        TOP,
        RELEVANCE,
        COMMENTS
    }
}
