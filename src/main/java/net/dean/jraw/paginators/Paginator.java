package net.dean.jraw.paginators;

import com.squareup.okhttp.CacheControl;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.*;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Thing;

import java.util.*;

/**
 * Provides the ability to iterate through data provided by the Reddit API.
 *
 * @param <T> The type that the listings will contain
 */
public abstract class Paginator<T extends Thing> implements RedditIterable<T> {

    /** The default limit of Things to return */
    public static final int DEFAULT_LIMIT = 25;
    /**
     * The recommended maximum limit of Things to return. No client-side code is in place to ensure that the limit is
     * not set over this number, but the Reddit API will only return this many amount of objects.
     */
    public static final int RECOMMENDED_MAX_LIMIT = 100;
    /** The default sorting. Equal to {@link Sorting#HOT} */
    public static final Sorting DEFAULT_SORTING = Sorting.HOT;
    /** The default time period. Equal to {@link TimePeriod#DAY} */
    public static final TimePeriod DEFAULT_TIME_PERIOD = TimePeriod.DAY;

    protected final RedditClient reddit;
    protected final Class<T> thingType;

    protected Sorting sorting;
    protected TimePeriod timePeriod;
    protected int limit;
    private boolean includeLimit;
    /** Current listing. Will get the next listing based on the current listing's "after" value */
    protected Listing<T> current;
    private int pageNumber;
    private ListingIterator iterator;

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
        this.iterator = new ListingIterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Listing<T> next() throws NetworkException {
        return next(true);
    }

    @Override
    public Listing<T> next(boolean forceNetwork) throws NetworkException, IllegalStateException {
        if (started && changed) {
            throw new IllegalStateException("Cannot change parameters without calling reset()");
        }

        String path = getBaseUri();

        Map<String, String> args = new HashMap<>();
        if (includeLimit)
            args.put("limit", String.valueOf(limit));
        if (current != null && current.getAfter() != null)
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


        RestResponse response;
        response = reddit.execute(request);
        Listing<T> listing = parseListing(response);
        this.current = listing;
        pageNumber++;

        if (!started) {
            started = true;
        }

        return listing;
    }

    @Override
    public final List<Listing<T>> accumulate(int maxPages) throws NetworkException {
        if (maxPages <= 0) {
            throw new IllegalArgumentException("maxPages must be greater than 0");
        }

        List<Listing<T>> listings = new ArrayList<>(maxPages);
        while (iterator.hasNext() && getPageIndex() < maxPages) {
            listings.add(next(true));
        }

        return listings;
    }

    @Override
    public final List<T> accumulateMerged(int maxPages) throws NetworkException {
        List<Listing<T>> listings = accumulate(maxPages);
        List<T> flattened = new ArrayList<>();

        for (Listing<T> listing : listings) {
            flattened.addAll(listing);
        }

        return flattened;
    }

    @Override
    public Iterator<Listing<T>> iterator() {
        return iterator;
    }

    @Override
    public void reset() {
        current = null;
        started = false;
        changed = false;
        pageNumber = 0;
    }

    @Override
    public Listing<T> getCurrentListing() {
        return current;
    }

    @Override
    public int getPageIndex() {
        return pageNumber;
    }

    @Override
    public boolean hasStarted() {
        return started;
    }

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
     * Generates extra arguments to be included in the query string.
     * @return A non-null map of paginator-implementation-specific arguments
     */
    protected Map<String, String> getExtraQueryArgs() {
        return new HashMap<>();
    }

    /**
     * Generates the base URI. Parameters will be stacked after this URI to form a query. For example,
     * {@link net.dean.jraw.paginators.SubredditPaginator} will return something like "/r/pics/new.json"
     *
     * @return The base URI that will be used in queries
     */
    protected abstract String getBaseUri();

    /**
     * Invalidates the current listing. This must be called in setter methods to notify {@link #next(boolean)} that
     * its parameters have changed.
     */
    protected void invalidate() {
        if (started)
            this.changed = true;
    }

    /**
     * Responsible for turning a RedditResponse into a Thing
     * @param response The response
     * @return A new Listing from the given response
     */
    protected Listing<T> parseListing(RestResponse response) {
        return response.asListing(thingType);
    }

    private final class ListingIterator implements Iterator<Listing<T>> {
        @Override
        public final void remove() {
            throw new UnsupportedOperationException("Cannot modify listing data");
        }

        @Override
        public boolean hasNext() {
            return (current != null && current.getAfter() != null) || !started;
        }

        @Override
        public Listing<T> next() {
            return Paginator.this.next();
        }
    }
}
