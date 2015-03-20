package net.dean.jraw.paginators;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Thing;

import java.util.List;

public interface RedditIterable<T extends Thing> extends Iterable<Listing<T>> {
    /** Gets the next page */
    public Listing<T> next() throws NetworkException;

    /**
     * Gets the next page
     *
     * @param forceNetwork If true, then the request will be sent through the network, regardless of it a cached version
     *                     is already available. Useful for when you want to make sure you have the absolute latest
     *                     version of the model.
     * @return The next page
     * @throws IllegalStateException If any property was changed after the first listing was requested and
     *                               {@link #reset()} was not called.
     */
    public Listing<T> next(boolean forceNetwork) throws NetworkException;

    /** Checks if Reddit can provide a next page. */
    public boolean hasNext();

    /**
     * Creates a list of listings whose size is less than or equal to the given number of pages. The amount of time this
     * method takes to return will grow linearly based on the maximum number of pages, as there will be one request for
     * each new page.
     *
     * @param maxPages The maximum amount of pages to retrieve
     * @return A list of listings
     */
    public List<Listing<T>> accumulate(int maxPages) throws NetworkException;

    /**
     * Creates a list of Things whose size is less than or equal to
     * {@code maxPages * Math.min(limit, RECOMMENDED_MAX_LIMIT)}. The amount of time this method takes to return will
     * grow linearly based on the value of {@code maxPages}.
     *
     * @param maxPages The maximum amount of pages to retrive
     * @return A list of Things
     * @throws NetworkException If any request was not successful
     */
    public List<T> accumulateMerged(int maxPages) throws NetworkException;

    /** Checks if listings have been iterated yet. */
    public boolean hasStarted();

    /**
     * Resets the RedditIterable; it will begin at page one. Call this method after you change a property relevant to
     * listing retrieval in order to avoid an IllegalStateException from {@link #next(boolean)}
     */
    public void reset();

    /**
     * Gets the page index, where 1 is the first page and 0 means that a listing has not been requested yet or
     * {@link #reset()} was just called.
     *
     * @return An integer representing the current page number
     */
    public int getPageIndex();

    /**
     * Gets the last page that was retrieved
     * @return The last listing retrieved, or null if this is a new Paginator or {@link #reset()} was just called
     */
    public Listing<T> getCurrentListing();
}
