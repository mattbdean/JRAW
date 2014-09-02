package net.dean.jraw.pagination;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Subreddit;

/**
 * This paginator provides a way to iterate through the logged-in user's subscribed subreddits
 */
public class MySubredditsPaginator extends GenericPaginator<Subreddit, MySubredditsPaginator.Where> {

    /**
     * Instantiates a new MySubredditsPaginator
     *
     * @param account The LoggedInAccount whose RedditClient will be used to send HTTP requests
     * @param where The criteria in which to return Subreddits
     */
    public MySubredditsPaginator(LoggedInAccount account, Where where) {
        super(account.getCreator(), Subreddit.class, where);
    }

    @Override
    public String getUriPrefix() {
        return "/subreddits/mine/";
    }

    @Override
    @EndpointImplementation({
            Endpoints.SUBREDDITS_MINE_CONTRIBUTOR,
            Endpoints.SUBREDDITS_MINE_MODERATOR,
            Endpoints.SUBREDDITS_MINE_SUBSCRIBER,
            Endpoints.SUBREDDITS_MINE_WHERE
    })
    protected Listing<Subreddit> getListing(boolean forwards) throws NetworkException {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.getListing(forwards);
    }

    public static enum Where {
        /** Subreddits you are subscribed to  */
        SUBSCRIBER,
        /** Subreddits that you contribute to */
        CONTRIBUTOR,
        /** Subreddits that you moderate */
        MODERATOR
    }
}
