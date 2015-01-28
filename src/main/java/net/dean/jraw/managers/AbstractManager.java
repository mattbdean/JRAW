package net.dean.jraw.managers;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.*;

/**
 * This class serves as the base class for all "manager" classes, which have control over a certain section of the API,
 * such as multireddits, wikis, and messages
 */
public abstract class AbstractManager implements NetworkAccessible {
    protected final RedditClient reddit;

    /**
     * Instantiates a new AbstractManager
     * @param reddit The RedditClient to use
     */
    protected AbstractManager(RedditClient reddit) {
        this.reddit = reddit;
    }

    @Override
    public final HttpAdapter getHttpAdapter() {
        return reddit.getHttpAdapter();
    }

    /**
     * Executes a generic POST request that returns a RestResponse. Used primarily for convenience and standardization
     * of the messages of RedditExceptions that are thrown.
     *
     * @param r The request to execute
     * @return A representation of the response by the Reddit API
     * @throws NetworkException If the request was not successful
     *                          HTTP request.
     */
    protected RestResponse genericPost(RestRequest r) throws NetworkException,
            ApiException {
        if (!r.getMethod().equals("POST")) {
            throw new IllegalArgumentException("Request is not POST");
        }

        RestResponse response = reddit.execute(r);
        if (response.hasErrors()) {
            throw response.getErrors()[0];
        }

        return response;
    }
}
