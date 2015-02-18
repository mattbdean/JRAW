package net.dean.jraw.managers;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.*;

/**
 * This class serves as the base class for all "manager" classes, which have control over a certain section of the API,
 * such as {@link MultiRedditManager multireddits}, {@link WikiManager wikis}, or {@link InboxManager the inbox}.
 */
public abstract class AbstractManager {
    protected final RedditClient reddit;

    /**
     * Instantiates a new AbstractManager
     * @param reddit The RedditClient to use
     */
    protected AbstractManager(RedditClient reddit) {
        this.reddit = reddit;
    }

    /**
     * Executes a generic POST request that returns a {@link RestResponse}. Used primarily for convenience since most
     * endpoints that modify something are POST requests, and also for automatic error handling.
     *
     * @param r The request to execute
     * @return A representation of the response by the Reddit API
     * @throws NetworkException If the request was not successful
     *                          HTTP request.
     */
    protected RestResponse genericPost(HttpRequest r) throws NetworkException, ApiException {
        if (!r.getMethod().equals("POST")) {
            throw new IllegalArgumentException("Request is not POST");
        }

        RestResponse response = reddit.execute(r);
        if (response.hasErrors()) {
            throw response.getError();
        }

        return response;
    }
}
