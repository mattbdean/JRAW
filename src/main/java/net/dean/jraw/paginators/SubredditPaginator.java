package net.dean.jraw.paginators;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.util.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to paginate through the front page or a subreddit with different time periods or sortings.
 *
 * <p>Please note that the Submissions that are returned by this Paginator will <em>always</em> have null comments.
 * The reason for this is that reddit does not include them in general pagination (which is what this Paginator does).
 * Only when that Submission is queried directly does reddit give you that post's comments.
 *
 * <p>To query a Submission directly, use {@link RedditClient#getSubmission(String)}.
 */
public class SubredditPaginator extends Paginator<Submission> {
    private String subreddit;
    private boolean obeyOver18 = true;

    /**
     * Instantiates a new SubredditPaginator that will iterate through submissions on the front page.
     * @param creator The RedditClient that will be used to send HTTP requests
     */
    public SubredditPaginator(RedditClient creator) {
        this(creator, null);
    }
    
    /**
     * Instantiates a new SubredditPaginator that will iterate through submissions of more than one subreddit.
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param subreddit The first subreddit to iterate through
     * @param moreSubreddits More subreddits to iterate through
     */
    public SubredditPaginator(RedditClient creator, String subreddit, String... moreSubreddits) {
        super(creator, Submission.class);
        setSubreddit(subreddit, moreSubreddits);
    }

    /**
     * Sets whether to ignore a user's over_18 reddit preference
     * @param obeyOver18 Whether to obey the preference
     */
    public void setObeyOver18(boolean obeyOver18){
        this.obeyOver18 = obeyOver18;
    }

    @Override
    protected Map<String, String> getExtraQueryArgs() {
        Map<String, String> args = new HashMap<>(super.getExtraQueryArgs());
        args.put("obey_over18", String.valueOf(obeyOver18));
        return args;
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
        String path = "/" + sorting.name().toLowerCase();
        return JrawUtils.getSubredditPath(subreddit, path);
    }

    /** Gets the subreddit this Paginator is currently browsing, or null for the front page. */
    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit, String... moreSubreddits) {
        if (subreddit != null && moreSubreddits != null) {
            StringBuilder subreddits = new StringBuilder(subreddit);
            for (String sub : moreSubreddits) {
                subreddits.append("+")
                        .append(sub);
            }
            this.subreddit = subreddits.toString();
        } else if (subreddit != null) {
            this.subreddit = subreddit;
        } else {
            this.subreddit = null;
        }
        invalidate();
    }
}
