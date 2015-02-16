package net.dean.jraw.paginators;

import net.dean.jraw.RedditClient;

import java.util.*;

/**
 * Provides a way to iterate through multiple subreddits without using multireddits. See
 * <a href="https://www.reddit.com/r/programming+java">here</a> for a demonstration.
 */
public class CompoundSubredditPaginator extends SubredditPaginator {
    private List<String> subreddits;

    /**
     * Instantiates a new CompoundSubredditPaginator
     *
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param subreddits The subreddits to iterate over. Must contain at least on element.
     */
    public CompoundSubredditPaginator(RedditClient creator, List<String> subreddits) {
        super(creator);
        setSubreddits(subreddits);
        this.subreddits = Collections.unmodifiableList(subreddits);
    }

    @Override
    protected String getBaseUri() {
        StringBuilder path = new StringBuilder();

        path.append("/r/");

        // sub1+sub2+sub3
        path.append(subreddits.get(0));
        for (ListIterator<String> it = subreddits.listIterator(1); it.hasNext(); ) {
            path.append("+")
                .append(it.next());
        }

        // /top.json
        path.append("/")
            .append(sorting.name().toLowerCase());

        return path.toString();
    }

    /**
     * Gets the subreddits that are being iterated over
     * @return The subreddits that are being iterated over
     */
    public List<String> getSubreddits() {
        return new ArrayList<>(subreddits);
    }

    /**
     * Sets the new subreddits to iterate over and invalidates the paginator
     * @param subreddits The subreddits to iterate over. Must contain at least on element.
     */
    public void setSubreddits(List<String> subreddits) {
        if (subreddits.size() == 0) {
            throw new IllegalArgumentException("Must have at least one subreddit");
        }
        this.subreddits = Collections.unmodifiableList(subreddits);
        invalidate();
    }

    @Override
    public void setSubreddit(String subreddit) {
        setSubreddits(Arrays.asList(subreddit));
    }
}
