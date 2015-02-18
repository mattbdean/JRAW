package net.dean.jraw.paginators;

import com.squareup.okhttp.CacheControl;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.*;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Thing;

import java.util.*;

/**
 * Represents the basic concept of a paginator
 *
 * @param <T> The type that the listings will contain
 */
public abstract class Paginator<T extends Thing> implements Iterator<Listing<T>> {

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
     * Synonymous to {@link #next()}, but preferred since a NetworkException must be explicitly handled if one is,
     * instead of trying to handle an IllegalStateException whose cause is a NetworkException.
     *
     * @return The next page
     * @throws NetworkException If the request was not successful
     */
    public Listing<T> getNext() throws NetworkException {
        return getListing(true);
    }

    /**
     * Gets the next page
     * @param forwards If true, this method will return the next listing. If false, it will return the page.
     * @return The next page
     * @throws NetworkException If the request was not successful
     */
    public Listing<T> getListing(boolean forwards) throws NetworkException {
        return getListing(forwards, false);
    }

    /**
     * Gets the next page
     *
     * @param forwards If true, this method will return the next listing. If false, it will return the page.
     * @param forceNetwork If true, then the request will be sent through the network, regardless of it a cached version
     *                     is already available. Useful for when you want to make sure you have the absolute latest
     *                     version of the model.
     * @return The next page
     * @throws NetworkException If the request was not successful
     * @throws IllegalStateException If a setter method (such as {@link #setLimit(int)} was called after the first
     *                               listing was requested and {@link #reset()} was not called.
     */
    public Listing<T> getListing(boolean forwards, boolean forceNetwork) throws NetworkException, IllegalStateException {
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

        boolean sortingUsed = timePeriod != null;
        if (sortingUsed && (sorting == Sorting.CONTROVERSIAL || sorting == Sorting.TOP)) {
            // Time period only applies to controversial and top listings
            args.put("t", timePeriod.name().toLowerCase());
        }

        Map<String, String> extraArgs = getExtraQueryArgs();
        if (extraArgs != null && extraArgs.size() > 0) {
            args.putAll(extraArgs);
        }

        HttpRequest request = reddit.request()
                .path(path)
                .query(args)
                // Force a network response if sorting by new or explicitly declared
                .cacheControl(forceNetwork || (sortingUsed && sorting == Sorting.NEW) ? CacheControl.FORCE_NETWORK : null)
                .build();


        Listing<T> listing = parseListing(reddit.execute(request));
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
    protected Listing<T> parseListing(RestResponse response) {
        return response.asListing(thingType);
    }

    /**
     * Creates a list of listings whose size is less than or equal to the given number of pages. The amount of time this
     * method takes to return will grow linearly based on the maximum number of pages, and is therefore doubly important
     * this this method be executed on another thread.
     *
     * @param maxPages The maximum amount of pages to retrieve
     * @return A list of listings
     * @throws NetworkException If any request was not successful
     */
    public final List<Listing<T>> accumulate(int maxPages) throws NetworkException {
        if (maxPages <= 0) {
            throw new IllegalArgumentException("maxPages must be greater than 0");
        }

        List<Listing<T>> listings = new ArrayList<>(maxPages);
        while (hasNext() && getPageIndex() < maxPages) {
            listings.add(getListing(true));
        }

        return listings;
    }

    /**
     * Creates a list of Things whose size is less than or equal to
     * {@code maxPages * Math.min(limit, RECOMMENDED_MAX_LIMIT)}. The amount of time this method takes to return will
     * grow linearly based on the value of {@code maxPages}.
     *
     * @param maxPages The maximum amount of pages to retrive
     * @return A list of Things
     * @throws NetworkException If any request was not successful
     */
    public final List<T> accumulateMerged(int maxPages) throws NetworkException {
        List<Listing<T>> listings = accumulate(maxPages);
        List<T> flattened = new ArrayList<>();

        for (Listing<T> listing : listings) {
            flattened.addAll(listing);
        }

        return flattened;
    }

    @Override
    public boolean hasNext() {
        return (current != null && current.getAfter() != null) || !started;
    }

    @Override
    public Listing<T> next() {
        try {
            return getNext();
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
     * @return A non-null map of paginator-implementation-specific arguments
     */
    protected Map<String, String> getExtraQueryArgs() {
        return new HashMap<>();
    }

    /**
     * Resets the Paginator; it will begin at page one. Call this method after you call a setter method such as
     * {@link #setTimePeriod(TimePeriod)} to avoid an IllegalStateException from {@link #getListing(boolean, boolean)}
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
     * Gets the last page that was retrieved
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
    public final void remove() {
        throw new UnsupportedOperationException("Cannot modify listing data");
    }
}
