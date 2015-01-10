package net.dean.jraw.managers;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.*;

/**
 * This class serves as the base class for all "manager" classes, which have control over a certain section of the API,
 * such as multireddits, wikis, and messages
 */
public abstract class AbstractManager implements HttpClient<RedditResponse>,
        NetworkAccessible<RedditResponse, RedditClient> {
    protected final RedditClient reddit;

    /**
     * Instantiates a new AbstractManager
     * @param reddit The RedditClient to use
     */
    protected AbstractManager(RedditClient reddit) {
        this.reddit = reddit;
    }

    @Override
    public final RedditResponse execute(RestRequest r) throws NetworkException {
        if (r.needsAuth() && !reddit.isLoggedIn()) {
            throw new IllegalStateException("This request requires an authenticated user");
        }

        return reddit.execute(r);
    }

    @Override
    public final RestRequest.Builder request() {
        RestRequest.Builder b = getHttpClient().request();
        b.needsAuth(true); // Assuming needs authentication by default
        return b;
    }

    @Override
    public final RedditClient getHttpClient() {
        return reddit;
    }

    /**
     * Executes a generic POST request that returns a RedditResponse. Used primarily for convenience and standardization
     * of the messages of RedditExceptions that are thrown.
     *
     * @param r The request to execute
     * @return A representation of the response by the Reddit API
     * @throws NetworkException If the request was not successful
     *                          HTTP request.
     */
    protected RedditResponse genericPost(RestRequest r) throws NetworkException,
            ApiException {
        if (!r.getMethod().equals("POST")) {
            throw new IllegalArgumentException("Request is not POST");
        }

        RedditResponse response = execute(r);
        if (response.hasErrors()) {
            throw response.getErrors()[0];
        }

        return response;
    }
}
