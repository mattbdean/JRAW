package net.dean.jraw.pagination;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Subreddit;

/**
 * This paginator will iterate through either the newest or the most popular subreddits
 */
public class AllSubredditsPaginator extends GenericPaginator<Subreddit, AllSubredditsPaginator.Where> {


    /**
     * Instantiates a new AllSubredditsPaginator
     *
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param where The criteria in which to return Subreddits
     */
    public AllSubredditsPaginator(RedditClient creator, Where where) {
        super(creator, Subreddit.class, where);
    }

    @Override
    @EndpointImplementation({
            Endpoints.SUBREDDITS_POPULAR,
            Endpoints.SUBREDDITS_NEW,
            Endpoints.SUBREDDITS_WHERE
    })
    protected Listing<Subreddit> getListing(boolean forwards) throws NetworkException {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.getListing(forwards);
    }

    @Override
    public String getUriPrefix() {
        return "/subreddits/";
    }

    public static enum Where {
        POPULAR,
        NEW
    }
}
