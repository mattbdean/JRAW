package net.dean.jraw.paginators;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAccessible;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.http.RestRequest;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Thing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Represents the basic concept of a paginator
 *
 * @param <T> The type that the listings will contain
 */
public abstract class Paginator<T extends Thing> implements Iterator<Listing<T>>,
        NetworkAccessible<RedditResponse, RedditClient> {

    /** The default limit of Things to return */
    public static final int DEFAULT_LIMIT = 25;
    /**
     * The recommended maximum limit of Things to return. No client-side code is in place to ensure that the limit is
     * not set over this number, but the Reddit API might error out if a limit is set higher than this.
     */
    public static final int RECOMMENDED_MAX_LIMIT = 100;
    /** The default sorting */
    public static final Sorting DEFAULT_SORTING = Sorting.HOT;
    /** The default time period */
    public static final TimePeriod DEFAULT_TIME_PERIOD = TimePeriod.DAY;

    /** The client that created this */
    protected final RedditClient reddit;
    protected final Class<T> thingType;

    protected Sorting sorting;
    protected TimePeriod timePeriod;
    protected int limit;
    private boolean includeLimit;
    /** Current listing. Will get the next listing based on the current listing's "after" value */
    protected Listing<T> current;
    private int pageNumber;

    private boolean started;
    private boolean changed;

    /**
     * Instantiates a new Paginator
     *
     * @param reddit The RedditClient that will be used to send HTTP requests
     * @param thingType The type of Thing that this Paginator will return
     */
    public Paginator(RedditClient reddit, Class<T> thingType) {
        this.reddit = reddit;
        this.thingType = thingType;
        this.sorting = DEFAULT_SORTING;
        this.timePeriod = DEFAULT_TIME_PERIOD;
        this.limit = DEFAULT_LIMIT;
        this.changed = false;
        this.started = false;
        this.includeLimit = false;
    }

    /**
     * Gets a listing of things
     *
     * @param forwards If true, this method will return the next listing. If false, it will return the first listing.
     * @return A new listing
     * @throws NetworkException If the request was not successful
     * @throws IllegalStateException If a setter method (such as {@link #setLimit(int)} was called after the first
     *                               listing was requested and {@link #reset()} was not called.
     */
    protected Listing<T> getListing(boolean forwards) throws NetworkException, IllegalStateException {
        if (started && changed) {
            throw new IllegalStateException("Cannot change parameters without calling reset()");
        }

        String path = getBaseUri();

        Map<String, String> args = new HashMap<>();
        if (includeLimit)
            args.put("limit", String.valueOf(limit));
        if (current != null)
            if (forwards && current.getAfter() != null)
                args.put("after", current.getAfter());

        if (timePeriod != null && (sorting == Sorting.CONTROVERSIAL || sorting == Sorting.TOP)) {
            // Time period only applies to controversial and top listings
            args.put("t", timePeriod.name().toLowerCase());
        }

        Map<String, String> extraArgs = getExtraQueryArgs();
        if (extraArgs != null) {
            args.putAll(extraArgs);
        }

        RestRequest request = getHttpClient().request()
                .path(path)
                .query(args)
                .build();
        Listing<T> listing = parseListing(getHttpClient().execute(request));
        this.current = listing;
        pageNumber++;

        if (!started) {
            started = true;
        }

        return listing;
    }

    /**
     * Responsible for turning a RedditResponse into a Thing
     * @param response The response
     * @return A new Listing from the given response
     */
    protected Listing<T> parseListing(RedditResponse response) {
        return response.asListing(thingType);
    }

    /**
     * Creates a list of listings whose size is less than or equal to the given number of pages. The amount of time this
     * method takes to return will grow linearly based on the maximum number of pages, and is therefore doubly important
     * this this method be executed on another thread.
     *
     * @param maxPages The maximum amount of pages to retrieve
     * @return A list of listings
     * @throws NetworkException
     */
    public final List<Listing<T>> accumulate(int maxPages) throws NetworkException {
        List<Listing<T>> listings = new ArrayList<>();
        if (maxPages <= 0) {
            throw new IllegalArgumentException("Pages must be greater than 0");
        }

        try {
            while (hasNext() && getPageIndex() < maxPages) {
                listings.add(next());
            }
        } catch (IllegalStateException e) {
            // Most likely cause will be a NetworkException because next() throws a NetworkException as a cause of an
            // IllegalStateException
            if (e.getCause().getClass().equals(NetworkException.class)) {
                throw (NetworkException) e.getCause();
            } else {
                throw e;
            }
        }

        return listings;
    }

    @Override
    public boolean hasNext() {
        return (current != null && current.getAfter() != null) || !started;
    }

    /**
     * Gets the next listing.
     *
     * @return The next listing of submissions
     * @throws IllegalStateException If there was a problem getting the next listing
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
     * Generates the base URI. Parameters will be stacked after this URI to form a query. For example,
     * {@link net.dean.jraw.paginators.SubredditPaginator} will return something like "/r/pics/new.json"
     *
     * @return The base URI that will be used in queries
     */
    protected abstract String getBaseUri();

    /**
     * How the Reddit API will choose to return the listing. If the sorting is ${@link Sorting#TOP},
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
     * Sets the new sorting
     * @param sorting The new sorting
     */
    public void setSorting(Sorting sorting) {
        this.sorting = sorting;
        invalidate();
    }

    /**
     * Sets the new time period
     * @param timePeriod The new time period
     */
    public void setTimePeriod(TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
        invalidate();
    }

    /**
     * Sets the new limit
     * @param limit The new limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
        this.includeLimit = true;
        invalidate();
    }

    /**
     * Checks whether this Paginator has sent a request yet. Calling {@link #reset()} resets this.
     * @return True if this Paginator has sent a request yet, false if else
     */
    public boolean hasStarted() {
        return started;
    }

    /**
     * Generates extra arguments to be included in the query string.
     * @return A map of paginator-implementation-specific arguments
     */
    protected Map<String, String> getExtraQueryArgs() {
        return null;
    }

    /**
     * Resets the listing. Call this method after you call a setter method such as {@link #setTimePeriod(TimePeriod)}
     */
    public void reset() {
        current = null;
        started = false;
        changed = false;
        pageNumber = 0;
    }

    /**
     * Invalidates the current listing. This must be called in setter methods to notify {@link #getListing(boolean)} that
     * its parameters have changed.
     */
    protected void invalidate() {
        if (started)
            this.changed = true;
    }

    /**
     * Gets the last listing that was retrieved
     * @return The last listing retrieved, or null if this is a new Paginator or {@link #reset()} was just called
     */
    public Listing<T> getCurrentListing() {
        return current;
    }

    /**
     * Gets the page index, where 1 is the first page and 0 means that this is a new Paginator or {@link #reset()} was
     * just called.
     *
     * @return An integer representing the current page number
     */
    public int getPageIndex() {
        return pageNumber;
    }

    @Override
    public RedditClient getHttpClient() {
        return reddit;
    }

    @Override
    public final void remove() {
        throw new UnsupportedOperationException("Cannot modify listing data");
    }
}
