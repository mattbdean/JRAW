package net.dean.jraw.pagination;

import net.dean.jraw.RedditClient;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;


public class SpecificPaginator extends AbstractPaginator<Submission> {
	private final String submissionListing;
	private SpecificPaginator(Builder b) {
		super(b);
		this.submissionListing = b.submissionListing;
	}

	@Override
	protected String getBaseUri() {
		return "/by_id/" + submissionListing + ".json";
	}

	@Override
	@EndpointImplementation(uris = "/by_id/names")
	protected Listing<Submission> getListing(boolean forwards) throws NetworkException {
		// Just call super so we can add the @EndpointImplementation annotation
		return super.getListing(forwards);
	}

	public static class Builder extends AbstractPaginator.Builder<Submission> {
		private String submissionListing;

		public Builder(RedditClient reddit, Submission... submissions) {
			this(reddit, compile(submissions));
		}

		public Builder(RedditClient reddit, String... submissionFullNames) {
			super(reddit, Submission.class);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < submissionFullNames.length; i++) {
				sb.append(submissionFullNames[i]);
				if (i + 1 != submissionFullNames.length) {
					sb.append(",");
				}
			}
			this.submissionListing = sb.toString();
		}

		/**
		 * Utility method to compile all of the submission's full names into a String array
		 * @param submissions The Submission's full names to collect
		 * @return A String array that consists of all the submissions' full names
		 */
		private static String[] compile(Submission... submissions) {
			String[] fullNames = new String[submissions.length];
			for (int i = 0; i < fullNames.length; i++) {
				fullNames[i] = submissions[i].getName();
			}

			return fullNames;
		}

		@Override
		public SpecificPaginator build() {
			return new SpecificPaginator(this);
		}
	}
}
