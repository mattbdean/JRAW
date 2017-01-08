package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

/**
 * This class is used to paginate through links of a certain domain.
 * <p>
 * <p>Please note that the Submissions that are returned by this Paginator will <em>always</em> have null comments.
 * The reason for this is that reddit does not include them in general pagination (which is what this Paginator does).
 * Only when that Submission is queried directly does reddit give you that post's comments.
 * <p>
 * <p>To query a Submission directly, use {@link RedditClient#getSubmission(String)}.
 */
public class DomainPaginator extends Paginator<Submission> {
    private String domain;

    /**
     * Instantiates a new SubredditPaginator that will iterate through submissions on the front page.
     *
     * @param creator The RedditClient that will be used to send HTTP requests
     */
    public DomainPaginator(RedditClient creator) {
        this(creator, null);
    }

    /**
     * Instantiates a new SubredditPaginator that will iterate through submissions of more than one subreddit.
     *
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param domain  The domain to iterate through
     */
    public DomainPaginator(RedditClient creator, String domain) {
        super(creator, Submission.class);
        setDomain(domain);
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
        return "/domain/" + domain + "/" + sorting.name().toLowerCase();
    }

    /**
     * Gets the domain this Paginator is currently browsing.
     */
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
        invalidate();
    }
}
