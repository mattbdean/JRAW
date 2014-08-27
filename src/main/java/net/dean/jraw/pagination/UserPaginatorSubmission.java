package net.dean.jraw.pagination;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;

/**
 * This class is used to paginate through user posts or comments via {@code /user/{username}/{where}.json}
 */
public class UserPaginatorSubmission extends GenericPaginator<Submission, UserPaginatorSubmission.Where, UserPaginatorSubmission> {
    private final String username;

    protected UserPaginatorSubmission(Builder b) {
        super(b);
        this.username = b.username;
    }

    @Override
    @EndpointImplementation(uris = {
            "/user/{username}/disliked",
            "/user/{username}/hidden",
            "/user/{username}/liked",
            "/user/{username}/saved",
            "/user/{username}/submitted"
    })
    protected Listing<Submission> getListing(boolean forwards) throws NetworkException {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.getListing(forwards);
    }

    @Override
    public String getUriPrefix() {
        return "/user/" + username;
    }

    public String getUsername() {
        return username;
    }

    public static class Builder extends GenericPaginator.Builder<Submission, Where, UserPaginatorSubmission> {
        private String username;

        /**
         * Instantiates a new Builder
         * @param reddit The RedditClient to help send requests
         */
        public Builder(RedditClient reddit, Where where) {
            super(reddit, Submission.class, where);
        }


        public Builder username(String user) {
            this.username = user;
            return this;
        }

        @Override
        public UserPaginatorSubmission build() {
            return new UserPaginatorSubmission(this);
        }
    }

    /**
     * Used by UserPaginatorSubmission to fill in the "where" in {@code /user/{username}/{where}}
     */
    public static enum Where {
        // Both submissions and comments
        /** Represents the user overview. Contains both submissions and comments */
        OVERVIEW(true, true),
        /** Represents the user's gilded submissions and comments */
        GILDED(true, true),

        // Only submissions
        /** Represents the user's submitted links */
        SUBMITTED(true, false),
        /** Represents the user's liked (upvoted) submissions */
        LIKED(true, false),
        /** Represents the user's disliked (downvoted) submissions */
        DISLIKED(true, false),
        /** Represents the user's hidden submissions */
        HIDDEN(true, false),
        /** Represents the user's saved submissions */
        SAVED(true, false),

        // Only comments
        /** Represents the user's comments */
        COMMENTS(false, true);

        private boolean hasSubmissions;
        private boolean hasComments;

        private Where(boolean hasSubmissions, boolean hasComments) {
            this.hasSubmissions = hasSubmissions;
            this.hasComments = hasComments;
        }

        /**
         * Whether this sorting could contain submissions in it
         * @return If this Where contains submissions
         */
        public boolean hasSubmissions() {
            return hasSubmissions;
        }

        /**
         * Whether this sorting could contain comments in it
         * @return If this Where contains submissions
         */
        public boolean hasComments() {
            return hasComments;
        }
    }
}
