package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Subreddit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This Paginator provides a way to iterate through the logged-in user's subreddits they interact with, whether that be
 * through being a contributor of, a moderator of, or are subscribed to
 */
public class UserSubredditsPaginator extends GenericPaginator<Subreddit> {

    /**
     * Instantiates a new MySubredditsPaginator
     *
     * @param client The RedditClient that will be used to send HTTP requests
     * @param where The criteria in which to return Subreddits
     */
    public UserSubredditsPaginator(RedditClient client, String where) {
        super(client, Subreddit.class, where);
    }

    @Override
    public String getUriPrefix() {
        return "/subreddits/mine/";
    }

    @Override
    public String[] getWhereValues() {
        return new String[] {"subscriber", "contributor", "moderator"};
    }

    @Override
    @EndpointImplementation({
            Endpoints.SUBREDDITS_MINE_CONTRIBUTOR,
            Endpoints.SUBREDDITS_MINE_MODERATOR,
            Endpoints.SUBREDDITS_MINE_SUBSCRIBER,
            Endpoints.SUBREDDITS_MINE_WHERE
    })
    public Listing<Subreddit> next(boolean forceNetwork) {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.next(forceNetwork);
    }

    /**
     * Creates a flattened list that contains all the subreddits associated with the user
     * @return A flattened list of subreddits
     */
    public final List<Subreddit> accumulateMergedAll() throws NetworkException {
        // reset so that we start from the beginning
        reset();
        setLimit(RECOMMENDED_MAX_LIMIT); // Make as few networking calls as possible
        ArrayList<Subreddit> flattened = new ArrayList<>();

        while (hasNext()){
            flattened.addAll(next(true));
        }

        setLimit(DEFAULT_LIMIT); // Return to default limit
        return flattened;
    }

    /**
     * Sorts the return value of {@link #accumulateMergedAll()}. Subreddits are sorted by their display name ("pics",
     * "funny", etc.)
     * @return A sorted list of all the subreddits the authenticated user is subscribed to
     */
    public final List<Subreddit> accumulateMergedAllSorted() throws NetworkException {
        List<Subreddit> list = accumulateMergedAll();
        Collections.sort(list);

        return list;
    }
}
