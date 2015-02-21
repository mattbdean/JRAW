package net.dean.jraw.paginators;


import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.LiveUpdate;

/**
 * Provides a way to iterate through the updates in a live thread. See <a href="https://www.reddit.com/live">here</a>
 * for more.
 */
public class LiveThreadPaginator extends Paginator<LiveUpdate> {
    private final String threadId;

    /**
     * Instantiates a new LiveThreadPaginator
     *
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param threadId The live thread's ID
     */
    public LiveThreadPaginator(RedditClient creator, String threadId) {
        super(creator, LiveUpdate.class);
        this.threadId = threadId;
    }

    @Override
    @EndpointImplementation(Endpoints.LIVE_THREAD)
    public Listing<LiveUpdate> next(boolean forceNetwork) {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.next(forceNetwork);
    }

    @Override
    protected String getBaseUri() {
        return String.format("/live/" + threadId);
    }
}
