package net.dean.jraw.pagination;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.HttpVerb;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestRequest;
import net.dean.jraw.models.Sorting;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Thing;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the basic concept of a paginator
 *
 * @param <T> The type that the listing will contain
 */
public abstract class AbstractPaginator<T extends Thing> {
	/** Maximum number of things returned by the Reddit API */
	public static final int LIMIT_MAX = 100;
	/** Minimum number of thing returned by the Reddit API */
	public static final int LIMIT_MIN = 1;
	/** Default number of things returned by the Reddit API */
	public static final int DEFAULT_LIMIT = 25;
	/** Default sorting for new Paginators */
	public static final Sorting DEFAULT_SORTING = Sorting.HOT;
	/** Default time period for new Paginators*/
	public static final TimePeriod DEFAULT_TIME_PERIOD = TimePeriod.DAY;

	protected final Sorting sorting;
	protected final TimePeriod timePeriod;
	/** From 1 to {@value #LIMIT_MAX} */
	protected int limit;
	/** Current listing. Will get the next listing based on the current listing's "after" value */
	protected Listing<T> current;

	/** The client that created this */
	protected final RedditClient creator;

	private Class<T> thingType;

	/**
	 * Instantiates a new AbstractPaginator using the default limit, sorting, and time period
	 * @param creator The client that created this
	 * @param thingType The type of thing that will be created
	 */
	protected AbstractPaginator(RedditClient creator, Class<T> thingType) {
		this(creator, thingType, DEFAULT_SORTING, DEFAULT_TIME_PERIOD);
	}

	/**
	 * Instantiates a new AbstractPaginator using the default limit
	 * @param creator The client that created this
	 * @param thingType The type of thing that will be created
	 * @param sorting The sorting that will be used
	 * @param timePeriod The time period that will be used
	 */
	protected AbstractPaginator(RedditClient creator, Class<T> thingType, Sorting sorting, TimePeriod timePeriod) {
		this(creator, thingType, sorting, timePeriod, DEFAULT_LIMIT);
	}

	/**
	 * Instantiates a new AbstractPaginator
	 *
	 * @param creator The client that created this
	 * @param thingType The type of thing that will be created
	 * @param sorting The sorting that will be used
	 * @param timePeriod The time period that will be used
	 * @param limit The maximum amount of things to return
	 */
	protected AbstractPaginator(RedditClient creator, Class<T> thingType, Sorting sorting, TimePeriod timePeriod, int limit) {
		this.creator = creator;
		this.thingType = thingType;
		this.limit = limit;
		this.sorting = sorting;
		this.timePeriod = timePeriod;
	}

	/**
	 * Gets a listing of things
	 *
	 * @param forwards If true, this method will return the next listing. If false, it will return the first listing.
	 * @return A new listing
	 * @throws NetworkException If there was a problem sending the HTTP request
	 */
	protected Listing<T> getListing(boolean forwards) throws NetworkException {
		String path = getBaseUri();

		Map<String, String> args = new HashMap<>();
		args.put("limit", String.valueOf(limit));
		if (current != null) {
			if (forwards && current.getAfter() != null)
				args.put("after", current.getAfter());
		}

		if (timePeriod != null && (sorting == Sorting.CONTROVERSIAL || sorting == Sorting.TOP)) {
			// Time period only applies to controversial and top listings
			args.put("t", timePeriod.name().toLowerCase());
		}

		Listing<T> listing = creator.execute(new RestRequest(HttpVerb.GET, path, args)).asListing(thingType);
		this.current = listing;

		return listing;
	}

	/**
	 * Gets the next listing. If a setter method is called (that is not ${@link #setLimit(int)}), then this
	 *
	 * @return The next listing of submissions
	 * @throws NetworkException If there was a problem sending the HTTP request
	 */
	public Listing<T> next() throws NetworkException {
		return getListing(true);
	}

	/**
	 * Generates the base URI. Parameters will be stacked after this URI to form a query. For example, SimplePaginator
	 * will return something like "/r/pics/new.json"
	 *
	 * @return The base URI that will be used in queries
	 */
	protected abstract String getBaseUri();

	/**
	 * How the Reddit API will choose to return the listing. If the sorting is ${@link net.dean.jraw.models.Sorting#TOP},
	 * then the time period will default to the last requested time period. If there was none, Reddit will use DAY.
	 *
	 * @return The current sorting
	 */
	public Sorting getSorting() {
		return sorting;
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
	 * Gets the maximum amount of submissions the listing will return. Default is {@value #DEFAULT_LIMIT}, maximum is
	 * {@value #LIMIT_MAX}.
	 *
	 * @return The maximum amount submissions returned in each call
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * Sets the amount of posts returned by the Reddit API. Must be at least 1 and no greater than {@value #LIMIT_MAX}
	 *
	 * @param limit The new limit
	 */
	public void setLimit(int limit) {
		if (limit > LIMIT_MAX) {
			throw new IllegalArgumentException(String.format("Limit cannot be over %s (was %s)", LIMIT_MAX, limit));
		} else if (limit < 1) {
			throw new IllegalArgumentException(String.format("Limit cannot be less than %s (was %s)", LIMIT_MIN, limit));
		}
		this.limit = limit;
	}
}
