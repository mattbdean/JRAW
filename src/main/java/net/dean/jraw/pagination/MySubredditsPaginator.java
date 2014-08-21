package net.dean.jraw.pagination;

import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.models.core.Subreddit;

public class MySubredditsPaginator extends AbstractPaginator<Subreddit> {
	private final Where where;

	protected MySubredditsPaginator(Builder b) {
		super(b);
		this.where = b.where;
	}

	@Override
	protected String getBaseUri() {
		return String.format("/subreddits/mine/%s.json", where.name().toLowerCase());
	}


	@Override
	@EndpointImplementation(uris = {
			"/subreddits/mine/contributor",
			"/subreddits/mine/moderator",
			"/subreddits/mine/subscriber",
			"/subreddits/mine/where",
	})
	protected Listing<Subreddit> getListing(boolean forwards) throws NetworkException {
		// Just call super so that we can add the @EndpointImplementation annotation
		return super.getListing(forwards);
	}

	public static class Builder extends AbstractPaginator.Builder<Subreddit> {
		private final Where where;

		/**
		 * Instantiates a new Builder
		 *
		 * @param account The LoggedInAccount to use to find subreddits with
		 * @param where   The subreddits you want this paginator to iterate over
		 */
		public Builder(LoggedInAccount account, Where where) {
			super(account.getCreator(), Subreddit.class);
			this.where = where;
		}

		@Override
		public MySubredditsPaginator build() {
			return new MySubredditsPaginator(this);
		}
	}

	public static enum Where {
		/** Subreddits you are subscribed to  */
		SUBSCRIBER,
		/** Subreddits that you contribute to */
		CONTRIBUTOR,
		/** Subreddits that you moderate */
		MODERATOR
	}
}
