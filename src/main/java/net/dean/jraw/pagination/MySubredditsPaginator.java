package net.dean.jraw.pagination;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Subreddit;

/**
 * This paginator provides a way to iterate through the logged-in user's subscribed subreddits
 */
public class MySubredditsPaginator extends GenericPaginator<Subreddit, MySubredditsPaginator.Where, MySubredditsPaginator> {

    private MySubredditsPaginator(GenericPaginator.Builder<Subreddit, Where, MySubredditsPaginator> b) {
        super(b);
    }

    @Override
    public String getUriPrefix() {
        return "/subreddits/mine/";
    }

    @Override
    @EndpointImplementation(uris = {
            "/subreddits/mine/contributor",
            "/subreddits/mine/moderator",
            "/subreddits/mine/subscriber",
            "/subreddits/mine/{where}",
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

    public static class Builder extends GenericPaginator.Builder<Subreddit, Where, MySubredditsPaginator> {

        /**
         * Instantiates a new Builder
         *
         * @param account The (logged in) account you wish to view the subreddits of
         * @param where   The enum that will be appended to the
         */
        public Builder(LoggedInAccount account, Where where) {
            super(account.getCreator(), Subreddit.class, where);
        }

        @Override
        public MySubredditsPaginator build() {
            return new MySubredditsPaginator(this);
        }
    }
}
