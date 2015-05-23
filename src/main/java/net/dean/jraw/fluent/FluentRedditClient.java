package net.dean.jraw.fluent;

import net.dean.jraw.RedditClient;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.managers.InboxManager;
import net.dean.jraw.managers.MultiRedditManager;
import net.dean.jraw.managers.WikiManager;

/**
 * This class wraps a {@link RedditClient} to provide a simpler, more logical API than the one RedditClient currently
 * provides. This class also aims to ease the pain at handling the different managers one would normally employ when
 * using a RedditClient.
 *
 * <p>The sole purpose of this class is to instantiate {@link Reference} objects. No logic is done here as it is handled
 * by the aforementioned References.
 */
public class FluentRedditClient {
    private RedditClient reddit;
    private InboxManager inbox;
    private AccountManager account;
    private MultiRedditManager multis;
    private WikiManager wiki;

    /**
     * Instantiates a new FluentRedditClient
     * @param reddit The RedditClient that will be used to make all requests to the API. <strong>Must already be
     *               authenticated</strong>.
     */
    public FluentRedditClient(RedditClient reddit) {
        if (!reddit.isAuthenticated())
            throw new IllegalArgumentException("The RedditClient must already be authenticated");
        this.reddit = reddit;
        this.inbox = new InboxManager(reddit);
        this.account = new AccountManager(reddit);
        this.multis = new MultiRedditManager(reddit);
        this.wiki = new WikiManager(reddit);
    }

    /** Requests a reference to the front page */
    public SubredditReference frontPage() {
        return SubredditReference.frontPage(wiki);
    }

    /** Requests a reference to a specific subreddit */
    public SubredditReference subreddit(String subreddit) {
        return SubredditReference.subreddit(wiki, subreddit);
    }
}
