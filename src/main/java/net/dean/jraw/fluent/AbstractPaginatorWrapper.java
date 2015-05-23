package net.dean.jraw.fluent;

import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Thing;
import net.dean.jraw.paginators.Paginator;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.TimePeriod;

import java.util.List;

public abstract class AbstractPaginatorWrapper<W, T extends Thing> extends AbstractReference {
    protected Paginator<T> paginator;

    protected AbstractPaginatorWrapper(Paginator<T> paginator) {
        super(paginator.getRedditClient());
        this.paginator = paginator;
    }

    /**
     * Sets a limit on how many Submissions to return. If the value is less than one or greater than
     * {@link net.dean.jraw.paginators.Paginator#RECOMMENDED_MAX_LIMIT}, reddit will most likely ignore this.
     *
     * @return This paginator reference for call chaining
     */
    public W limit(int max) {
        paginator.setLimit(max);
        return (W) this;
    }

    /** Gets the next page from the subreddit. */
    @NetworkingCall
    public Listing<T> fetch() {
        return paginator.next();
    }

    /** Gets the next <em>n</em> pages from the subreddit */
    @NetworkingCall
    public List<Listing<T>> fetch(int pages) {
        return paginator.accumulate(pages);
    }

    /** Sets this Reference to retrieve the top posts from the given time period */
    public W top(TimePeriod timePeriod) { return sorting(Sorting.TOP, timePeriod); }
    /** Sets this Reference to retrieve the most commented on posts from the given time period */
    public W controversial(TimePeriod timePeriod) { return sorting(Sorting.CONTROVERSIAL, timePeriod); }
    /** Sets this Reference to retrieve rising posts */
    public W rising() { return sorting(Sorting.RISING, null); }
    /** Sets this Reference to retrieve the newest posts */
    public W newest() { return sorting(Sorting.NEW, null); }
    /** Sets this Reference to retrieve hot posts */
    public W hot() { return sorting(Sorting.HOT, null); }

    protected W sorting(Sorting s, TimePeriod t) {
        paginator.setSorting(s);
        if (s.requiresTimePeriod() && t == null)
            throw new IllegalArgumentException("A TimePeriod must be specified with sorting=" + s);

        paginator.setTimePeriod(t);
        return (W) this;
    }
}
