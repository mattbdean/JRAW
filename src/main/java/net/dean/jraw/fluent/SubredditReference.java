package net.dean.jraw.fluent;

import net.dean.jraw.ApiException;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.paginators.SubredditPaginator;

import java.net.URL;

/**
 * Provides a reference to a subreddit.
 */
public final class SubredditReference extends PaginatorWrapper<Submission, SubredditReference> {
    private final String subreddit;
    private final ManagerAggregation managers;
    private transient Subreddit cached;

    /** Creates a SubredditReference to the front page */
    static SubredditReference frontPage(ManagerAggregation managers) {
        return new SubredditReference(new SubredditPaginator(managers.reddit()), managers);
    }

    /** Creates a SubreditReference to a specific subreddit */
    static SubredditReference subreddit(ManagerAggregation managers, String subreddit) {
        return new SubredditReference(new SubredditPaginator(managers.reddit(), subreddit), managers);
    }

    private SubredditReference(SubredditPaginator paginator, ManagerAggregation managers) {
        super(paginator);
        this.subreddit = paginator.getSubreddit();
        this.managers = managers;
    }

    private void checkNotFrontPage() {
        if (subreddit == null)
            throw new IllegalArgumentException("A reference to a subreddit (not the front page) is required to do this");
    }

    /** Retrieves the Subreddit's information */
    @NetworkingCall
    public Subreddit info() {
        return info(false);
    }

    @NetworkingCall
    private Subreddit info(boolean cachedIfAvailable) {
        checkNotFrontPage();
        if (cachedIfAvailable && cached != null)
            return cached;

        cached = reddit.getSubreddit(subreddit);
        return cached;
    }

    /** Creates a submission. Returns the newly created submission. */
    @NetworkingCall
    public Submission submit(URL url, String title) throws ApiException {
        checkNotFrontPage();
        return managers.account().submit(new AccountManager.SubmissionBuilder(url, subreddit, title));
    }

    /** Creates a self-post submission. Returns the newly created submission. */
    @NetworkingCall
    public Submission submit(String selfText, String title) throws ApiException {
        checkNotFrontPage();
        return managers.account().submit(new AccountManager.SubmissionBuilder(selfText, subreddit, title));
    }

    /**
     * Subscribes to a subreddit.
     *
     * @throws NetworkException If the request was not successful
     */
    @NetworkingCall
    public void subscribe() throws NetworkException {
        checkNotFrontPage();
        managers.account().subscribe(info(true));
    }

    /**
     * Unsubscribes from a subreddit
     *
     * @throws NetworkException If the request was not successful
     */
    @NetworkingCall
    public void unsubscribe() {
        if (subreddit == null)
            throw new IllegalArgumentException("Cannot unsubscribe from the front page");
        managers.account().unsubscribe(info(true));
    }

    /** Gets the stylesheet for this subreddit. This reference must not be to the front page. */
    @NetworkingCall
    public String css() {
        checkNotFrontPage();
        return managers.reddit().getStylesheet(subreddit);
    }

    /** Gets a reference to this subreddit's flair. This reference must not be to the front page. */
    public FlairReference flair() {
        checkNotFrontPage();
        return new FlairReference(managers, subreddit);
    }

    /** Retrieves a reference to this subreddit's wiki */
    public WikiReference wiki() {
        return new WikiReference(managers.wiki(), subreddit);
    }
}
