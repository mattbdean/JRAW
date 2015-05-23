package net.dean.jraw.fluent;

import net.dean.jraw.managers.WikiManager;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.paginators.SubredditPaginator;

/**
 * Provides a reference to a subreddit.
 */
public final class SubredditReference extends PaginatorWrapper<Submission, SubredditReference> {
    private String subreddit;
    private WikiManager wikiManager;

    /** Creates a SubredditReference to the front page */
    static SubredditReference frontPage(WikiManager wikiManager) {
        return new SubredditReference(new SubredditPaginator(wikiManager.getRedditClient()), wikiManager);
    }

    /** Creates a SubreditReference to a specific subreddit */
    static SubredditReference subreddit(WikiManager wikiManager, String subreddit) {
        return new SubredditReference(new SubredditPaginator(wikiManager.getRedditClient(), subreddit), wikiManager);
    }

    private SubredditReference(SubredditPaginator paginator, WikiManager wikiManager) {
        super(paginator);
        this.subreddit = paginator.getSubreddit();
        this.wikiManager = wikiManager;
    }

    /** Retrieves the Subreddit's information */
    @NetworkingCall
    public Subreddit info() {
        if (subreddit == null)
            throw new IllegalArgumentException("Must be a Reference to a subreddit, not the front page");

        return reddit.getSubreddit(subreddit);
    }

    /** Retrieves a reference to this subreddit's wiki */
    public WikiReference wiki() {
        return new WikiReference(wikiManager, subreddit);
    }

}
