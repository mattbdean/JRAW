package net.dean.jraw.fluent;

import net.dean.jraw.RedditClient;

/**
 * Provides a basic abstract implementation of {@link Reference}
 */
public abstract class AbstractReference implements Reference {
    protected final RedditClient reddit;

    protected AbstractReference(RedditClient reddit) {
        this.reddit = reddit;
    }

    @Override
    public RedditClient getRedditClient() {
        return reddit;
    }
}
