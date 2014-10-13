package net.dean.jraw.http;

import net.dean.jraw.RedditClient;

/**
 * This class serves as the base class for all "manager" classes, which have control over a certain section of the API,
 * such as multireddits, wikis, or messages
 */
public abstract class AbstractManager implements NetworkAccessible<RedditResponse, RedditClient> {
    protected final RedditClient reddit;

    protected AbstractManager(RedditClient reddit) {
        this.reddit = reddit;
    }

    @Override
    public RedditClient getCreator() {
        return reddit;
    }

    @Override
    public final RedditResponse execute(RestRequest r) throws NetworkException {
        if (requiresAuthentication() && !reddit.isLoggedIn()) {
            throw new IllegalStateException("This manager requires an authenticated user");
        }

        return reddit.execute(r);
    }


    /**
     * Checks if the RedditClient needs to be logged in in order to complete requests successfully
     * @return If there needs to be an authenticated user
     */
    protected abstract boolean requiresAuthentication();
}
