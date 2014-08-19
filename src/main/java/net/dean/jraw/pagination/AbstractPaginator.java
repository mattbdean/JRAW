package net.dean.jraw.pagination;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.HttpVerb;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestRequest;
import net.dean.jraw.models.Sorting;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Thing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents the basic concept of a paginator
 *
 * @param <T> The type that the listing will contain
 */
public abstract class AbstractPaginator<T extends Thing> implements Iterator<Listing<T>> {
	/** The client that created this */
	protected final RedditClient creator;
	protected final Sorting sorting;
	protected final TimePeriod timePeriod;
	protected final int limit;
	/** Current listing. Will get the next listing based on the current listing's "after" value */
	protected Listing<T> current;
	private Class<T> thingType;

	protected AbstractPaginator(Builder<T> b) {
		this.creator = b.creator;
		this.thingType = b.thingType;
		this.sorting = b.sorting;
		this.timePeriod = b.timePeriod;
		this.limit = b.limit;
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
		if (current != null)
			if (forwards && current.getAfter() != null)
				args.put("after", current.getAfter());

		if (timePeriod != null && (sorting == Sorting.CONTROVERSIAL || sorting == Sorting.TOP)) {
			// Time period only applies to controversial and top listings
			args.put("t", timePeriod.name().toLowerCase());
		}

		Listing<T> listing = creator.execute(new RestRequest(HttpVerb.GET, path, args)).asListing(thingType);
		this.current = listing;

		return listing;
	}

	@Override
	public boolean hasNext() {
		return current == null || current.getAfter() != null;
	}

	/**
	 * Gets the next listing.
	 *
	 * @return The next listing of submissions
	 * @throws IllegalStateException If there was a problem sending the HTTP request
	 */
	@Override
	public Listing<T> next() {
		try {
			return getListing(true);
		} catch (NetworkException e) {
			throw new IllegalStateException("Could not get the next listing", e);
		}
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
	 * This class provides a way to easily create Paginator objects with default values for some parameters
	 * @param <T> The type of Thing that will be returned by the created paginator
	 */
	public abstract static class Builder<T> {
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

		private final Class<T> thingType;
		private final RedditClient creator;
		private int limit = DEFAULT_LIMIT;
		private Sorting sorting = DEFAULT_SORTING;
		private TimePeriod timePeriod = DEFAULT_TIME_PERIOD;

		/**
		 * Instantiates a new Builder
		 * @param reddit The RedditClient to send requests with
		 * @param thingType The type of object to return in the built paginator
		 */
		public Builder(RedditClient reddit, Class<T> thingType) {
			this.creator = reddit;
			this.thingType = thingType;
		}

		/**
		 * Sets the amount of posts returned by the Reddit API. Must be at least 1 and no greater than {@value #LIMIT_MAX}
		 *
		 * @param limit The new limit
		 */
		public Builder limit(int limit) {
			if (limit > LIMIT_MAX) {
				throw new IllegalArgumentException(String.format("Limit cannot be over %s (was %s)", LIMIT_MAX, limit));
			} else if (limit < 1) {
				throw new IllegalArgumentException(String.format("Limit cannot be less than %s (was %s)", LIMIT_MIN, limit));
			}
			this.limit = limit;
			return this;
		}

		/**
		 * Sets the sorting to use for the new paginator
		 * @param s The sorting to use
		 * @return This Builder
		 */
		public Builder sorting(Sorting s) {
			this.sorting = s;
			return this;
		}

		/**
		 * Sets the time period to use for the new paginator
		 * @param tp The time period to use
		 * @return This Builder
		 */
		public Builder timePeriod(TimePeriod tp) {
			this.timePeriod = tp;
			return this;
		}

		/**
		 * Transforms this Builder into a Paginator
		 * @return A paginator with the same parameters as this Builder
		 */
		public abstract AbstractPaginator build();
	}
}
