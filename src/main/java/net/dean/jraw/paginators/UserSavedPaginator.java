package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.Listing;

/**
 * This class is used to paginate through user saved posts or comments via {@code /user/{username}/saved.json}
 *
 * This class adds the subreddit and category functionality missing in the UserContributionPaginator
 */
public class UserSavedPaginator extends GenericPaginator<Contribution> {
    private String username;
    private String subreddit;
    private String category;

    /**
     * Instantiates a new UserSavedPaginator
     *
     * @param creator  The RedditClient that will be used to send HTTP requests
     * @param where    The criteria in which to return Subreddits
     * @param username The user to view
     */
    public UserSavedPaginator(RedditClient creator, String where, String username) {
        super(creator, Contribution.class, where);
        this.username = username;
    }

    @Override
    @EndpointImplementation({
            Endpoints.USER_USERNAME_WHERE,
            Endpoints.USER_USERNAME_SAVED
    })
    public Listing<Contribution> next(boolean forceNetwork) {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.next(forceNetwork);
    }

    /**
     * Sets the subreddit to limit results to.
     * May return no results if the user has no saved submissions from that subreddit
     *
     * @param subreddit
     */
    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    /**
     * Sets the category to limit results to.
     *
     * This category should be a user-created category (see /api/saved_categories), and will return
     * no results if the category doesn't exist
     *
     * @param category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getUriPrefix() {
        return "/user/" + username ;
    }

    @Override
    public String getUriPostfix(){
        return createSavedEnding();
    }

    public String createSavedEnding() {
        String ending = "";
        if (category != null && !category.isEmpty()) {
            ending = "/" + category;
        }
        if (subreddit != null && !subreddit.isEmpty()) {
            ending = ending + "?sr=" + subreddit;
        }
        return ending;
    }

    @Override
    public String[] getWhereValues() {
        return new String[] {"saved"};
    }
    /**
     * Gets the name whose submitted links you are iterating over
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }
}
