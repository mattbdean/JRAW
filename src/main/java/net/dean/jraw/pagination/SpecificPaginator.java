package net.dean.jraw.pagination;

import net.dean.jraw.RedditClient;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;


/**
 * This class provides a way to iterate through a list of Submissions by their names.
 */
public class SpecificPaginator extends Paginator<Submission> {
    private final String submissionListing;
    private SpecificPaginator(Builder b) {
        super(b);
        this.submissionListing = b.submissionListing;
    }

    @Override
    protected String getBaseUri() {
        return "/by_id/" + submissionListing + ".json";
    }

    @Override
    @EndpointImplementation(uris = "GET /by_id/{names}")
    protected Listing<Submission> getListing(boolean forwards) throws NetworkException {
        // Just call super so we can add the @EndpointImplementation annotation
        return super.getListing(forwards);
    }

    public static class Builder extends Paginator.Builder<Submission> {
        private String submissionListing;

        /**
         * Instantiates a new Builder
         * @param reddit The RedditClient that will be used to send HTTP requests
         * @param submissions A list of Submission objects
         */
        public Builder(RedditClient reddit, Submission... submissions) {
            this(reddit, compile(submissions));
        }

        /**
         * Instantiates a new Builder
         * @param reddit The RedditClient that will be used to send HTTP requests
         * @param submissionFullNames A list of fullnames of submissions
         */
        public Builder(RedditClient reddit, String... submissionFullNames) {
            super(reddit, Submission.class);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < submissionFullNames.length; i++) {
                sb.append(submissionFullNames[i]);
                if (i + 1 != submissionFullNames.length) {
                    sb.append(",");
                }
            }
            this.submissionListing = sb.toString();
        }

        /**
         * Utility method to compile all of the submission's full names into a String array
         * @param submissions The Submission's full names to collect
         * @return A String array that consists of all the submissions' full names
         */
        private static String[] compile(Submission... submissions) {
            String[] fullNames = new String[submissions.length];
            for (int i = 0; i < fullNames.length; i++) {
                fullNames[i] = submissions[i].getFullName();
            }

            return fullNames;
        }

        @Override
        public SpecificPaginator build() {
            return new SpecificPaginator(this);
        }
    }
}
