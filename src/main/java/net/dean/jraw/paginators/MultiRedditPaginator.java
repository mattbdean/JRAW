package net.dean.jraw.paginators;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.MultiReddit;
import net.dean.jraw.models.Submission;

/**
 * Iterates through the posts in a multireddit.
 */
public class MultiRedditPaginator extends Paginator<Submission> {
    private MultiReddit multiReddit;

    /**
     * Instantiates a new MultiRedditPaginator
     *
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param multi The multireddit to iterate
     */
    public MultiRedditPaginator(RedditClient creator, MultiReddit multi) {
        super(creator, Submission.class);
        this.multiReddit = multi;
    }

    @Override
    protected String getBaseUri() {
        return multiReddit.getPath();
    }

    /**
     * Gets the MultiReddit that this paginator is iterating through
     * @return The MultiReddit
     */
    public MultiReddit getMultiReddit() {
        return multiReddit;
    }

    /**
     * Sets the MultiReddit to iterate through
     * @param multiReddit The new MultiReddit
     */
    public void setMultiReddit(MultiReddit multiReddit) {
        this.multiReddit = multiReddit;
        invalidate();
    }
}
