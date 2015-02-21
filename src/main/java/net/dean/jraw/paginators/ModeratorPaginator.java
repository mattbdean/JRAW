package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.PublicContribution;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a way to iterate over submissions and comments relevant to moderators, such as those reported
 * and marked as spam.
 */
public class ModeratorPaginator extends GenericPaginator<PublicContribution> {
    private String subreddit;
    private boolean includeSubmissions;
    private boolean includeComments;

    /**
     * Instantiates a new ModeratorPaginator
     * @param creator The RedditClient used to send requests
     * @param subreddit The subreddit to view. Must be a moderator.
     * @param where What to paginate
     */
    public ModeratorPaginator(RedditClient creator, String subreddit, String where) {
        super(creator, PublicContribution.class, where);
        this.subreddit = subreddit;
    }

    /**
     * Checks whether this ModeratorPaginator is including submissions. Will only take effect if the Where given supports
     * filtering.
     *
     * @return If this ModeratorPaginator is including submissions
     * @see ModeratorPaginator.Where#supportsFilter()
     */
    public boolean isIncludingSubmissions() {
        return includeSubmissions;
    }

    /**
     * Sets whether this ModeratorPaginator will return Listings that contain submissions. Will only take effect if the
     * Where given supports filtering.
     *
     * @param includeSubmissions If this ModeratorPaginator is including submissions
     * @see ModeratorPaginator.Where#supportsFilter()
     */
    public void setIncludeSubmissions(boolean includeSubmissions) {
        this.includeSubmissions = includeSubmissions;
        invalidate();
    }

    /**
     * Checks whether this ModeratorPaginator is including comments. Will only take effect if the Where given supports
     * filtering.
     *
     * @return If this ModeratorPaginator is including comments
     * @see ModeratorPaginator.Where#supportsFilter()
     */
    public boolean isIncludingComments() {
        return includeComments;
    }

    /**
     * Sets whether this ModeratorPaginator will return Listings that contain comments. Will only take effect if the
     * Where given supports filtering.
     *
     * @param includeComments If this ModeratorPaginator is including comments
     * @see ModeratorPaginator.Where#supportsFilter()
     */
    public void setIncludeComments(boolean includeComments) {
        this.includeComments = includeComments;
        invalidate();
    }

    @Override
    protected String getUriPrefix() {
        return String.format("/r/%s/about/", subreddit);
    }

    @Override
    public String[] getWhereValues() {
        return new String[] {"reports", "spam", "modqueue", "unmoderated", "edited"};
    }

    @Override
    @EndpointImplementation({
            Endpoints.ABOUT_LOCATION,
            Endpoints.ABOUT_REPORTS,
            Endpoints.ABOUT_SPAM,
            Endpoints.ABOUT_MODQUEUE,
            Endpoints.ABOUT_UNMODERATED,
            Endpoints.ABOUT_EDITED
    })
    public Listing<PublicContribution> next(boolean forceNetwork) {
        // Just call super so that we can add the @EndpointImplementation annotation
        return super.next(forceNetwork);
    }

    @Override
    protected Map<String, String> getExtraQueryArgs() {
        Map<String, String> args = new HashMap<>(1);

        Where where = Where.valueOf(this.where.toUpperCase());
        if (!(!includeComments && !includeSubmissions) && !(includeComments && includeSubmissions) && where.supportsFilter()) {
            // Including only comments or only submissions AND filtering is available
            args.put("only", includeSubmissions ? "links" : "comments");
        }

        return args;
    }

    protected static enum Where {
        /** Submissions that have been reported. Supports filtering. */
        REPORTS(true),
        /** Submissions that have been marked as spam. Supports filtering. */
        SPAM(true),
        /**
         * Submissions requiring moderator review, such as reported submissoins and submissions caught in the spam
         * filter. Supports filtering.
         */
        MODQUEUE(true),
        /** Submissions that have yet to be approved or removed by a moderator. Does not support filtering. */
        UNMODERATED(false),
        /** Things that have recently been edited. Supports filtering. */
        EDITED(true);

        private boolean supportsFilter;
        private Where(boolean supportsFilter) {
            this.supportsFilter = supportsFilter;
        }

        /**
         * Whether or not this endpoint can filter the type of thing it returns
         * @return If this Where supports filtering
         */
        public boolean supportsFilter() {
            return supportsFilter;
        }
    }
}
