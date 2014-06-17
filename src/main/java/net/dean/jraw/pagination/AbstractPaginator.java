package net.dean.jraw.pagination;

import net.dean.jraw.*;
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
	/** Default number of things returned by the Reddit API */
	public static final int LIMIT_DEFAULT = 25;

	protected Sorting sorting;
	protected TimePeriod timePeriod;
	/** From 1 to {@value #LIMIT_MAX} */
	protected int limit;
	/** Current listing. Will get the next listing based on this one's "after" value */
	protected Listing<T> current;

	/** The client that created this */
	protected RedditClient creator;

	private Class<T> thingType;

	/**
	 * Instantiates a new AbstractPaginator
	 *
	 * @param creator The client that created this
	 * @param thingType The type of thing that will be created
	 */
	protected AbstractPaginator(RedditClient creator, Class<T> thingType) {
		this.creator = creator;
		this.thingType = thingType;

		// Reddit API default settings
		this.limit = LIMIT_DEFAULT;
		this.sorting = Sorting.HOT;
		this.timePeriod = TimePeriod.DAY;
	}

	/**
	 * Gets a listing of things
	 *
	 * @param forwards If true, this method will return the next listing. If false, it will return the first listing.
	 * @return
	 * @throws net.dean.jraw.NetworkException
	 */
	protected Listing<T> getListing(boolean forwards) throws NetworkException {
		String path = getBaseUri();

		Map<String, String> args = new HashMap<>();
		args.put("limit", Integer.toString(limit));
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
	 * Gets the next listing. If a setter method is called (that is not ${@link #setLimit(int)}), then this method is
	 * going to return the same thing that ${@link #first()} would.
	 *
	 * @return The next listing of submissions
	 * @throws NetworkException
	 */
	public Listing<T> next() throws NetworkException {
		return getListing(true);
	}

	/**
	 * Gets the first listing
	 *
	 * @return The first listing
	 * @throws NetworkException
	 */
	public Listing<T> first() throws NetworkException {
		return getListing(false);
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
		invalidate();
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
		invalidate();
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
	 * Sets the amount of posts returned by the Reddit API. Must be at least 1 and no greater than {@value #LIMIT_MAX}
	 *
	 * @param limit The new limit
	 */
	public void setLimit(int limit) {
		if (limit > LIMIT_MAX) {
			throw new IllegalArgumentException("Limit cannot be over " + LIMIT_MAX);
		}
		this.limit = limit;
	}

	/**
	 * Notifies {@link #getListing(boolean)} that some piece of data (such as sorting or time period) has changed, so we
	 * need to start over
	 */
	protected void invalidate() {
		if (current != null)
			current = null;
	}
}
