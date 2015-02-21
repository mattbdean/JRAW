package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.ModAction;

/**
 * This class allows the iteration of the log of moderation actions on a particular subreddit
 */
public class ModLogPaginator extends Paginator<ModAction> {
    private String subreddit;

    /**
     * Instantiates a new Paginator
     *
     * @param reddit    The RedditClient that will be used to send HTTP requests
     * @param subreddit The subreddit to observe the moderation actions of
     */
    public ModLogPaginator(RedditClient reddit, String subreddit) {
        super(reddit, ModAction.class);
        this.subreddit = subreddit;
    }

    @Override
    @EndpointImplementation(Endpoints.ABOUT_LOG)
    public Listing<ModAction> next(boolean forceNetwork) throws IllegalStateException {
        return super.next(forceNetwork);
    }

    @Override
    protected String getBaseUri() {
        return "/r/" + subreddit + "/about/log";
    }
}
