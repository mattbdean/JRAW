package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Subreddit;

/**
 * This paginator will iterate through either the newest or the most popular subreddits
 */
public class AllSubredditsPaginator extends GenericPaginator<Subreddit> {

    /**
     * Instantiates a new AllSubredditsPaginator
     *
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param where The criteria in which to return Subreddits
     */
    public AllSubredditsPaginator(RedditClient creator, String where) {
        super(creator, Subreddit.class, where);
    }

    @Override
    @EndpointImplementation({
            Endpoints.SUBREDDITS_POPULAR,
            Endpoints.SUBREDDITS_NEW,
            Endpoints.SUBREDDITS_WHERE
    })
    public Listing<Subreddit> getListing(boolean forwards) throws NetworkException {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.getListing(forwards, where.equals("new"));
    }

    @Override
    public String getUriPrefix() {
        return "/subreddits/";
    }

    @Override
    public String[] getWhereValues() {
        return new String[] {"popular", "new"};
    }
}
