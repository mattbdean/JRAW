package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
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
     * @param multi   The multireddit to iterate
     */
    public MultiRedditPaginator(RedditClient creator, MultiReddit multi) {
        super(creator, Submission.class);
        this.multiReddit = multi;
    }

    @Override
    @EndpointImplementation({
            Endpoints.CONTROVERSIAL,
            Endpoints.HOT,
            Endpoints.NEW,
            Endpoints.TOP,
            Endpoints.SORT
    })
    public Listing<Submission> next(boolean forceNetwork) {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.next(forceNetwork);
    }


    @Override
    protected String getBaseUri() {
        String sort = sorting == null ? "" : sorting.name().toLowerCase();
        return multiReddit.getPath() + sort;
    }

    /**
     * Gets the MultiReddit that this paginator is iterating through
     *
     * @return The MultiReddit
     */
    public MultiReddit getMultiReddit() {
        return multiReddit;
    }

    /**
     * Sets the MultiReddit to iterate through
     *
     * @param multiReddit The new MultiReddit
     */
    public void setMultiReddit(MultiReddit multiReddit) {
        this.multiReddit = multiReddit;
        invalidate();
    }
}
