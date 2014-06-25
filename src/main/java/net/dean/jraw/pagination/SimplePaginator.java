package net.dean.jraw.pagination;

import net.dean.jraw.RedditClient;
import net.dean.jraw.endpointgen.EndpointImplementation;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;

/**
 * This class is used to paginate through the front page or a subreddit with different time periods or sortings.
 */
public class SimplePaginator extends AbstractPaginator<Submission> {
	private final String subreddit;

	protected SimplePaginator(Builder b) {
		super(b);
		this.subreddit = b.subreddit;
	}

	@Override
	@EndpointImplementation(uris = {"/controversial", "/hot", "/new", "/top", "/sort"})
	protected Listing<Submission> getListing(boolean forwards) throws NetworkException {
		// Just call super so that we can add the @EndpointImplementation annotation
		return super.getListing(forwards);
	}

	@Override
	protected String getBaseUri() {
		String path = "/" + sorting.name().toLowerCase() + ".json";
		// "/new.json"
		if (subreddit != null) {
			path = "/r/" + subreddit + path;
			// "/r/pics/new.json"
		}

		return path;
	}

	/**
	 * Gets the subreddit this Paginator is currently browsing
	 * @return The subreddit
	 */
	public String getSubreddit() {
		return subreddit;
	}

	public static class Builder extends AbstractPaginator.Builder<Submission> {
		private String subreddit;

		/**
		 * Instantiates a new Builder
		 * @param reddit The RedditClient to send requests with
		 */
		public Builder(RedditClient reddit) {
			super(reddit, Submission.class);
		}

		/**
		 * Sets the subreddit to browse. Defaults to the front page (null)
		 * @param sr The subreddit to browse
		 * @return This Builder
		 */
		public Builder subreddit(String sr) {
			this.subreddit = sr;
			return this;
		}

		@Override
		public SimplePaginator build() {
			return new SimplePaginator(this);
		}
	}
}
