package net.dean.jraw.fluent;

import net.dean.jraw.managers.WikiManager;
import net.dean.jraw.models.WikiPage;
import net.dean.jraw.models.WikiPageSettings;

import java.util.List;

/**
 * Provides a reference to either reddit's main wiki or a specific subreddit's wiki.
 */
public final class WikiReference extends AbstractReference {
    private WikiManager manager;
    private String subreddit;

    WikiReference(WikiManager manager) {
        this(manager, null);
    }

    WikiReference(WikiManager manager, String subreddit) {
        super(manager.getRedditClient());
        this.manager = manager;
        this.subreddit = subreddit;
    }

    /**
     * Gets a WikiPage by name. To get a list of all wiki page names, use {@link #pages()}.
     * @param page The name of the wiki page
     */
    @NetworkingCall
    public WikiPage get(String page) {
        if (subreddit == null)
            return manager.get(page);
        return manager.get(subreddit, page);
    }

    /**
     * Gets a list of pages in this wiki. The contents of the list will in format such that the page can be accessed at
     * {@code https://www.reddit.com/r/$subreddit/wiki/$name}.
     */
    @NetworkingCall
    public List<String> pages() {
        return subreddit == null ? manager.getPages() : manager.getPages(subreddit);
    }

    /** Gets the settings for a given wiki page. */
    @NetworkingCall
    public WikiPageSettings settings(String page) {
        return subreddit == null ? manager.getSettings(page) : manager.getSettings(subreddit, page);
    }
}
