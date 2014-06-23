package net.dean.jraw.pagination;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.endpointgen.EndpointImplementation;
import net.dean.jraw.models.Sorting;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;

/**
 * This class is used to paginate through the front page or a subreddit with different time periods or sortings.
 */
public class SimplePaginator extends AbstractPaginator<Submission> {
	private final String subreddit;

	/**
	 * Instantiates a new SimplePaginator that will be used to browse the front page
	 *
	 * @param creator The RedditClient that created this object
	 * @param sorting The sorting to use
	 * @param timePeriod The time period to use
	 * @return A new SimplePaginator
	 */
	public static SimplePaginator ofFrontPage(RedditClient creator, Sorting sorting, TimePeriod timePeriod) {
		return new SimplePaginator(creator, null, sorting, timePeriod);
	}

	/**
	 * Instantiates a new SimplePaginator that will be used to browse a subreddit
	 *
	 * @param creator The RedditClient that created this object
	 * @param subreddit The subreddit to browse
	 * @param sorting The sorting to use
	 * @param timePeriod The time period to use
	 * @return A new SimplePaginator
	 */
	public static SimplePaginator ofSubreddit(RedditClient creator, String subreddit, Sorting sorting, TimePeriod timePeriod) {
		return new SimplePaginator(creator, subreddit, sorting, timePeriod);
	}

	private SimplePaginator(RedditClient creator, String subreddit, Sorting sorting, TimePeriod timePeriod) {
		super(creator, Submission.class, sorting, timePeriod); // Will always be submissions
		this.subreddit = subreddit;
	}

	@Override
	@EndpointImplementation(uris = {"/controversial", "/hot", "/new", "/top", "/sort"})
	protected Listing<Submission> getListing(boolean forwards) throws NetworkException {
		// Just call super so that we can add the @EndpointImplementation annotation
		return super.getListing(forwards);
	}

	@Override
	protected String getBaseUri() {
		String path = "/" + sorting.name().toLowerCase() + ".json";
		// "/new.json"
		if (subreddit != null) {
			path = "/r/" + subreddit + path;
			// "/r/pics/new.json"
		}

		return path;
	}

	/**
	 * Gets the subreddit this Paginator is currently browsing
	 * @return The subreddit
	 */
	public String getSubreddit() {
		return subreddit;
	}
}
