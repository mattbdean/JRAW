package net.dean.jraw.pagination;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;

/**
 * This class is used to paginate through user posts or comments via /user/&lt;username&gt;/&lt;where&gt;.json
 */
public class UserPaginatorSubmission extends AbstractPaginator<Submission> {
	private final String username;
	private final Where where;

	protected UserPaginatorSubmission(Builder b) {
		super(b);
		this.username = b.username;
		this.where = b.where;
	}

	@Override
	@EndpointImplementation(uris = {
			"/user/username/disliked",
			"/user/username/hidden",
			"/user/username/liked",
			"/user/username/saved",
			"/user/username/submitted"})
	protected Listing<Submission> getListing(boolean forwards) throws NetworkException {
		// Just call super so that we can add the @EndpointImplementation annotation
		return super.getListing(forwards);
	}

	@Override
	protected String getBaseUri() {
		return String.format("/user/%s/%s.json", username, where.name().toLowerCase());
	}

	public String getUsername() {
		return username;
	}

	public Where getWhere() {
		return where;
	}

	public static class Builder extends AbstractPaginator.Builder<Submission> {
		private String username;
		private Where where;

		/**
		 * Instantiates a new Builder
		 * @param reddit The RedditClient to help send requests
		 */
		public Builder(RedditClient reddit) {
			super(reddit, Submission.class);
		}


		public Builder username(String user) {
			this.username = user;
			return this;
		}

		public Builder where(Where w) {
			this.where = w;
			return this;
		}

		@Override
		public UserPaginatorSubmission build() {
			return new UserPaginatorSubmission(this);
		}
	}
}
