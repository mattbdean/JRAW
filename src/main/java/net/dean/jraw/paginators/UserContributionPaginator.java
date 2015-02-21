package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.Listing;

/**
 * This class is used to paginate through user posts or comments via {@code /user/{username}/{where}.json}
 */
public class UserContributionPaginator extends GenericPaginator<Contribution> {
    private String username;

    /**
     * Instantiates a new UserPaginatorSubmission
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param where The criteria in which to return Subreddits
     * @param username The user to view
     */
    public UserContributionPaginator(RedditClient creator, String where, String username) {
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
    public Listing<Contribution> next(boolean forceNetwork) {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.next(forceNetwork);
    }

    @Override
    public String getUriPrefix() {
        return "/user/" + username;
    }

    @Override
    public String[] getWhereValues() {
        return new String[] {"overview", "gilded", "submitted", "liked", "disliked", "hidden", "saved", "comments"};
    }

    /**
     * Gets the name whose submitted links you are iterating over
     * @return The username
     */
    public String getUsername() {
        return username;
    }
}
