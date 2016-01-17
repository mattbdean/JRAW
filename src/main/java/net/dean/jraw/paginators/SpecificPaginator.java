package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.util.JrawUtils;


/**
 * This class provides a way to iterate through a list of Submissions by their names.
 */
public class SpecificPaginator extends Paginator<Submission> {
    private String[] submissions;
    private String compiledFullnames;

    /**
     * Instantiates a new SpecificPaginator
     *
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param submissionFullNames A list of fullnames of Submissions
     */
    public SpecificPaginator(RedditClient creator, String... submissionFullNames) {
        super(creator, Submission.class);
        setSubmissions(submissionFullNames);
    }

    /**
     * Sets the new Submissions to iterate
     * @param submissionFullNames A list of fullnames of Submissions
     */
    public void setSubmissions(String... submissionFullNames) {
        this.submissions = submissionFullNames;
        this.compiledFullnames = JrawUtils.join(submissionFullNames);
        invalidate();
    }

    @Override
    protected String getBaseUri() {
        return "/by_id/" + compiledFullnames;
    }

    /**
     * Gets a list of fullnames of the submissions this paginator is iterating
     * @return A list of fullnames
     */
    public String[] getSubmissions() {
        String[] localCopy = new String[submissions.length];
        System.arraycopy(submissions, 0, localCopy, 0, submissions.length);

        return localCopy;
    }

    @Override
    @EndpointImplementation(Endpoints.BY_ID_NAMES)
    public Listing<Submission> next(boolean forceNetwork) {
        // Just call super so we can add the @EndpointImplementation annotation
        return super.next(forceNetwork);
    }
}
