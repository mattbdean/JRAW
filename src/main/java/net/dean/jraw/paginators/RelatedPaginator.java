package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

/**
 * Iterates through submissions related to a given one
 */
public class RelatedPaginator extends Paginator<Submission> {
    private String article;

    public RelatedPaginator(RedditClient reddit, Submission article) {
        super(reddit, Submission.class);
        this.article = article.getId();
    }

    @Override
    protected String getBaseUri() {
        return "/related/" + article;
    }

    @Override
    @EndpointImplementation(Endpoints.RELATED_ARTICLE)
    public Listing<Submission> next(boolean forceNetwork) throws NetworkException, IllegalStateException {
        return super.next(forceNetwork);
    }

    @Override
    protected Listing<Submission> parseListing(RestResponse response) {
        // The response is an two-length array of listings. The first listing contains only one element (the original
        // submission) and the second listing contains the related articles.
        return new Listing<>(response.getJson().get(1).get("data"), Submission.class);
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
        invalidate();
    }
}
