package net.dean.jraw.pagination;

import net.dean.jraw.http.NetworkException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.endpointgen.EndpointImplementation;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;

/**
 * This class is used to paginate through user posts or comments via /user/&lt;username&gt;/&lt;where&gt;.json
 */
public class UserPaginatorSubmission extends AbstractPaginator<Submission> {
	private final String username;
	private final Where where;

	public UserPaginatorSubmission(RedditClient creator, String username, Where where) {
		super(creator, Submission.class);

		if (!where.hasSubmissions() || where.hasComments()) {
			throw new IllegalArgumentException("Where must only contain submissions");
		}

		this.where = where;
		this.username = username;
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
}
