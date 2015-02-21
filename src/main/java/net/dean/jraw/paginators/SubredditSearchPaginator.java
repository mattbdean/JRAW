package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Subreddit;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a way to search for subreddits by both name and description
 */
public class SubredditSearchPaginator extends Paginator<Subreddit> {
    private String query;

    /**
     * Instantiates a new Paginator
     *
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param query   What to search for
     */
    public SubredditSearchPaginator(RedditClient creator, String query) {
        super(creator, Subreddit.class);
        this.query = query;
    }

    @Override
    protected String getBaseUri() {
        return "/subreddits/search";
    }

    @Override
    @EndpointImplementation(Endpoints.SUBREDDITS_SEARCH)
    public Listing<Subreddit> next(boolean forceNetwork) throws IllegalStateException {
        return super.next(forceNetwork);
    }

    @Override
    protected Map<String, String> getExtraQueryArgs() {
        Map<String, String> args = new HashMap<>(super.getExtraQueryArgs());
        args.put("q", query);
        return args;
    }
}
