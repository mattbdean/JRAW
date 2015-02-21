package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

/**
 * This class is used to paginate through the front page or a subreddit with different time periods or sortings.
 */
public class SubredditPaginator extends Paginator<Submission> {
    private String subreddit;

    /**
     * Instantiates a new SubredditPaginator that will iterate through submissions on the front page.
     * @param creator The RedditClient that will be used to send HTTP requests
     */
    public SubredditPaginator(RedditClient creator) {
        super(creator, Submission.class);
    }
    
    /**
     * Instantiates a new SubredditPaginator that will iterate through submissions in a subreddit.
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param subreddit The subreddit to paginate through
     */
    public SubredditPaginator(RedditClient creator, String subreddit) {
        super(creator, Submission.class);
        this.subreddit = subreddit;
    }

    @Override
    @EndpointImplementation({
            Endpoints.CONTROVERSIAL,
            Endpoints.HOT,
            Endpoints.NEW,
            Endpoints.TOP,
            Endpoints.SORT
    })
    public Listing<Submission> next(boolean forceNetwork) {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.next(forceNetwork);
    }

    @Override
    protected String getBaseUri() {
        String path = "/" + sorting.name().toLowerCase();
        return JrawUtils.getSubredditPath(subreddit, path);
    }

    /**
     * Gets the subreddit this Paginator is currently browsing
     * @return The subreddit
     */
    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
        invalidate();
    }
}
