package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

/**
 * Iterates through duplicates of a submission
 */
public class DuplicatesPaginator extends Paginator<Submission> {
    private String article;

    public DuplicatesPaginator(RedditClient reddit, Submission original) {
        super(reddit, Submission.class);
        this.article = original.getId();
    }

    @Override
    @EndpointImplementation(Endpoints.DUPLICATES_ARTICLE)
    public Listing<Submission> next(boolean forceNetwork) throws NetworkException, IllegalStateException {
        return super.next(forceNetwork);
    }

    @Override
    protected Listing<Submission> parseListing(RestResponse response) {
        // The response is an two-length array of listings. The first listing contains only one element (the original
        // submission) and the second listing contains the duplicates.
        return new Listing<>(response.getJson().get(1).get("data"), Submission.class);
    }

    @Override
    protected String getBaseUri() {
        return "/duplicates/" + article;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
        invalidate();
    }
}
