package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.UserRecord;

/**
 * This class provides a way to iterate over the users that have been banned, marked as contributors, etc.
 */
public class UserRecordPaginator extends GenericUserRecordPaginator {
    private String subreddit;

    /**
     * Instantiates a new UserRecordPaginator
     *
     * @param creator The RedditClient that will be used to send requests
     * @param subreddit The subreddit to view the user records from. The logged in user must be a moderator of this
     *                  subreddit.
     * @param where What to iterate
     */
    public UserRecordPaginator(RedditClient creator, String subreddit, String where) {
        super(creator, where);
        this.subreddit = subreddit;
    }

    @Override
    @EndpointImplementation({
            Endpoints.ABOUT_BANNED,
            Endpoints.ABOUT_WIKIBANNED,
            Endpoints.ABOUT_CONTRIBUTORS,
            Endpoints.ABOUT_WIKICONTRIBUTORS,
            Endpoints.ABOUT_MODERATORS,
            Endpoints.ABOUT_WHERE
    })
    public Listing<UserRecord> next(boolean forceNetwork) {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.next(forceNetwork);
    }

    @Override
    protected String getUriPrefix() {
        return "/r/" + subreddit + "/about/";
    }

    @Override
    public String[] getWhereValues() {
        return new String[] {"banned", "wikibanned", "contributors", "wikicontributors", "moderators"};
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
        invalidate();
    }
}
