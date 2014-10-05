package net.dean.jraw.pagination;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.Listing;

/**
 * This class is used to paginate through user posts or comments via {@code /user/{username}/{where}.json}
 */
public class UserContributionPaginator extends GenericPaginator<Contribution, UserContributionPaginator.Where> {
    private String username;

    /**
     * Instantiates a new UserPaginatorSubmission
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param where The criteria in which to return Subreddits
     * @param username The user to view
     */
    public UserContributionPaginator(RedditClient creator, Where where, String username) {
        super(creator, Contribution.class, where);
        this.username = username;
    }

    @Override
    @EndpointImplementation({
            Endpoints.USER_USERNAME_WHERE,
            Endpoints.USER_USERNAME_OVERVIEW,
            Endpoints.USER_USERNAME_SUBMITTED,
            Endpoints.USER_USERNAME_COMMENTS,
            Endpoints.USER_USERNAME_LIKED,
            Endpoints.USER_USERNAME_DISLIKED,
            Endpoints.USER_USERNAME_HIDDEN,
            Endpoints.USER_USERNAME_SAVED,
            Endpoints.USER_USERNAME_GILDED
    })
    protected Listing<Contribution> getListing(boolean forwards) throws NetworkException {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.getListing(forwards);
    }

    @Override
    public String getUriPrefix() {
        return "/user/" + username;
    }

    /**
     * Gets the name whose submitted links you are iterating over
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Used by UserPaginatorSubmission to fill in the "where" in {@code /user/{username}/{where}}
     */
    public static enum Where {
        // Both submissions and comments
        /** Represents the user overview. Contains both submissions and comments */
        OVERVIEW,
        /** Represents the user's gilded submissions and comments */
        GILDED,

        // Only submissions
        /** Represents the user's submitted links */
        SUBMITTED,
        /** Represents the user's liked (upvoted) submissions */
        LIKED,
        /** Represents the user's disliked (downvoted) submissions */
        DISLIKED,
        /** Represents the user's hidden submissions */
        HIDDEN,
        /** Represents the user's saved submissions */
        SAVED,

        // Only comments
        /** Represents the user's comments */
        COMMENTS
    }
}
