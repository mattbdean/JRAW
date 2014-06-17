package net.dean.jraw;

import net.dean.jraw.endpointgen.EndpointImplementation;
import net.dean.jraw.models.Sorting;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to paginate through the front page or a subreddit with different time periods or sortings.
 */
public class Paginator {
	private static final int LIMIT_MAX = 100;
	private static final int LIMIT_DEFAULT = 25;

	private String subreddit;
	private RedditClient creator;
	private Listing<Submission> current;
	private Sorting sorting;
	private TimePeriod timePeriod;
	private int limit;

	static Paginator ofFrontPage(RedditClient creator) {
		return new Paginator(creator, null);
	}

	static Paginator ofSubreddit(RedditClient creator, String subreddit) {
		return new Paginator(creator, subreddit);
	}

	private Paginator(RedditClient creator, String subreddit) {
		this.subreddit = subreddit;
		this.creator = creator;
		this.limit = LIMIT_DEFAULT;

		// Reddit API default settings
		this.sorting = Sorting.HOT;
		this.timePeriod = TimePeriod.DAY;
	}

	/**
	 * Gets a listing of submissions from a given subreddit or the front page.
	 *
	 * @param forwards If true, this method will return the next listing. If false, it will return the first listing.
	 * @return
	 * @throws NetworkException
	 */
	@EndpointImplementation(uris = { "/controversial", "/hot", "/new", "/top", "/sort"})
	private Listing<Submission> getListing(boolean forwards) throws NetworkException {
		String path = "/" + sorting.name().toLowerCase() + ".json";
		// "/new.json"
		if (subreddit != null) {
			path = "/r/" + subreddit + path;
			// "/r/pics/new.json"
		}

        Map<String, String> args = new HashMap<>();
		args.put("limit", Integer.toString(limit));
		if (current != null) {
			if (forwards && current.getAfter() != null)
					args.put("after", current.getBefore());
		}

		if (timePeriod != null && (sorting == Sorting.CONTROVERSIAL || sorting == Sorting.TOP)) {
			// Time period only applies to controversial and top listings
			args.put("t", timePeriod.name().toLowerCase());
		}

		Listing<Submission> submissionListing = creator.execute(new RestRequest(HttpVerb.GET, path, args)).asListing(Submission.class);
		this.current = submissionListing;

		return submissionListing;
	}

	/**
	 * Gets the next listing. If a setter method is called (that is not ${@link #setLimit(int)}), then this method is
	 * going to return the same thing that ${@link #first()} would.
	 *
	 * @return The next listing of submissions
	 * @throws NetworkException
	 */
	public Listing<Submission> next() throws NetworkException {
		return getListing(true);
	}

	/**
	 * Gets the first listing
	 *
	 * @return The first listing
	 * @throws NetworkException
	 */
	public Listing<Submission> first() throws NetworkException {
		return getListing(false);
	}

	public String getSubreddit() {
		return subreddit;
	}

	public void setSubreddit(String subreddit) {
		this.subreddit = subreddit;
	}

	/**
	 * How the Reddit API will choose to return the listing. If the sorting is ${@link net.dean.jraw.models.Sorting#TOP},
	 * then the time period will default to the last requested time period. If there was none, Reddit will use DAY.
	 *
	 */
	public Sorting getSorting() {
		return sorting;
	}

	/**
	 * Sets the new sorting and invalidates the current listing
	 *
	 * @param sorting The new sorting
	 */
	public void setSorting(Sorting sorting) {
		this.sorting = sorting;
		this.current = null;
	}

	/**
	 * The time period to get the submissions from
	 *
	 * @return The time period
	 */
	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	/**
	 * Sets the new time period and invalidates the current time period
	 *
	 * @param timePeriod The new time period
	 */
	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
		this.current = null;
	}

	/**
	 * Gets the maximum amount of submissions the listing will return. Default is {@value #LIMIT_DEFAULT}, maximum is
	 * {@value #LIMIT_MAX}.
	 *
	 * @return The maximum amount submissions returned in each call
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * Sets the limit
	 *
	 * @param limit The new limit
	 */
	public void setLimit(int limit) {
		if (limit > LIMIT_MAX) {
			throw new IllegalArgumentException("Limit cannot be over " + LIMIT_MAX);
		}
		this.limit = limit;
	}
}
