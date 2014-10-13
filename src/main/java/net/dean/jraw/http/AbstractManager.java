package net.dean.jraw.http;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.LoggedInAccount;

/**
 * This class serves as the base class for all "manager" classes, which have control over a certain section of the API,
 * such as multireddits, wikis, or messages
 */
public abstract class AbstractManager implements NetworkAccessible<RedditResponse, RedditClient> {
    protected final LoggedInAccount account;
    protected final RedditClient client;

    protected AbstractManager(LoggedInAccount account) {
        this.account = account;
        this.client = account.getCreator();
    }

    @Override
    public RedditClient getCreator() {
        return client;
    }
}
