package net.dean.jraw.models;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.endpointgen.EndpointImplementation;
import net.dean.jraw.http.HttpVerb;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestRequest;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.models.core.Account;
import net.dean.jraw.models.core.Comment;
import net.dean.jraw.models.core.Submission;
import org.codehaus.jackson.JsonNode;

import java.net.URL;
import java.util.Map;

public class LoggedInAccount extends Account {
	private RedditClient creator;

	public LoggedInAccount(JsonNode data, RedditClient creator) {
		super(data);
		this.creator = creator;
	}

	public Submission submitContent(SubmissionBuilder b) throws NetworkException, ApiException {
		return submitContent(b, null, null);
	}

	/**
	 * Submits a link with a given captcha. Only really needed if the user has less than 10 link karma.
	 *
	 * @return A representation of the newly submitted Link
	 * @throws NetworkException If there was a problem sending the HTTP request
	 * @throws net.dean.jraw.ApiException If the API returned an error
	 */
	@EndpointImplementation(uris = "/api/submit")
	public Submission submitContent(SubmissionBuilder b, Captcha captcha, String captchaAttempt) throws NetworkException, ApiException {
		Map<String, String> args = JrawUtils.args(
				"api_type", "json",
				"extension", "json",
				"kind", b.type.name().toLowerCase(),
				"resubmit", b.resubmit,
				"save", b.saveAfter,
				"sendreplies", b.sendRepliesToInbox,
				"sr", b.subreddit,
				"then", "comments",
				"title", b.title
		);

		if (b.type == SubmissionType.LINK) {
			args.put("url", b.url.toExternalForm());
		} else if (b.type == SubmissionType.SELF) {
			args.put("text", b.selfText);
		} else {
			throw new IllegalArgumentException("Unknown SubmissionType: " + b.type);
		}

		if (captcha != null) {
			if (captchaAttempt == null) {
				throw new IllegalArgumentException("Captcha present but the attempt is not");
			}

			args.put("iden", captcha.getId());
			args.put("captcha", captchaAttempt);
		}

		RestResponse response = genericPost("/api/submit", args);
		String jsonUrl = response.getJson().get("json").get("data").get("url").getTextValue();

		return creator.execute(new RestRequest(HttpVerb.GET, jsonUrl)).as(Submission.class);
	}

	/**
	 * Votes on a submission. Please note that "API clients proxying a human's action one-for-one are OK, but bots
	 * deciding how to vote on content or amplifying a human's vote are not".
	 *
	 * @param s The submission to vote on
	 * @param voteDirection How to vote
	 * @throws NetworkException If there was a problem sending the HTTP request
	 * @throws ApiException If the API returned an error
	 */
	public void vote(Submission s, VoteDirection voteDirection) throws NetworkException, ApiException {
		vote(s.getName(), voteDirection);
	}

	/**
	 * Votes on a comment. Please note that "API clients proxying a human's action one-for-one are OK, but bots
	 * deciding how to vote on content or amplifying a human's vote are not".
	 *
	 * @param c The comment to vote on
	 * @param voteDirection How to vote
	 * @throws NetworkException If there was a problem sending the HTTP request
	 * @throws ApiException If the API returned an error
	 */
	public void vote(Comment c, VoteDirection voteDirection) throws NetworkException, ApiException {
		vote(c.getName(), voteDirection);
	}

	/**
	 * Votes on a thing. Please note that "API clients proxying a human's action one-for-one are OK, but bots
	 * deciding how to vote on content or amplifying a human's vote are not".
	 *
	 * @param fullName The submission or comment's full name to vote on
	 * @param voteDirection How to vote
	 * @throws NetworkException If there was a problem sending the HTTP request
	 * @throws ApiException If the API returned an error
	 */
	@EndpointImplementation(uris = "/api/vote")
	private void vote(String fullName, VoteDirection voteDirection) throws NetworkException, ApiException {
		genericPost("/api/vote", JrawUtils.args(
				"dir", voteDirection.getValue(),
				"id", fullName
		));
	}

	/**
	 * Saves or unsaves a submission.
	 *
	 * @param s The submission to save or unsave
	 * @return The JSON response from the API
	 * @throws NetworkException If there was a problem sending the HTTP request
	 * @throws ApiException If the API returned an error
	 */
	@EndpointImplementation(uris = {"/api/save", "/api/unsave"})
	public RestResponse setSaved(Submission s, boolean save) throws NetworkException, ApiException {
		// Send it to "/api/save" if save == true, "/api/unsave" if save == false
		return genericPost(String.format("/api/%ssave", save ? "" : "un"), JrawUtils.args(
				"id", s.getName()
		));
	}

	@EndpointImplementation(uris = "/api/sendreplies")
	public RestResponse setSendRepliesToInbox(Submission s, boolean send) throws NetworkException, ApiException {
		if (!s.getAuthor().equals(getName())) {
			throw new IllegalArgumentException(String.format("Logged in user (%s) did not post this submission (by %s)", getName(), s.getAuthor()));
		}
		return genericPost("/api/sendreplies", JrawUtils.args(
				"id", s.getName(),
				"state", Boolean.toString(send)
		));
	}

	@EndpointImplementation(uris = {"/api/marknsfw", "/api/unmarknsfw"})
	public RestResponse setNsfw(Submission s, boolean nsfw) throws NetworkException, ApiException {
		checkIfOwns(s);

		// "/api/marknsfw" if nsfw == true, "/api/unmarknsfw" if nsfw == false
		return genericPost(String.format("/api/%smarknsfw", nsfw ? "" : "un"), JrawUtils.args(
				"id", s.getName()
		));
	}

	/**
	 * Deletes a submission that you posted
	 * @param s The submission to delete
	 * @return The response that the Reddit API returned
	 * @throws NetworkException If there was a problem sending the request
	 * @throws ApiException If the api returned an error
	 */
	public RestResponse delete(Submission s) throws NetworkException, ApiException {
		checkIfOwns(s);
		return delete(s.getName());
	}


	/**
	 * Deletes a comment that you posted
	 * @param c The comment to delete
	 * @return The response that the Reddit API returned
	 * @throws NetworkException If there was a problem sending the request
	 * @throws ApiException If the api returned an error
	 */
	public RestResponse delete(Comment c) throws NetworkException, ApiException {
		if (!c.getAuthor().equals(getName())) {
			throw new IllegalArgumentException(String.format("Logged in user (%s) did not post this comment (by %s)", getName(), c.getAuthor()));
		}

		return delete(c.getName());
	}


	/**
	 * Deletes a comment or submission that you posted
	 * @param id The ID of the submission or comment to delete
	 * @return The response that the Reddit API returned
	 * @throws NetworkException If there was a problem sending the request
	 * @throws ApiException If the api returned an error
	 */
	@EndpointImplementation(uris = "/api/del")
	private RestResponse delete(String id) throws NetworkException, ApiException {
		return genericPost("/api/del", JrawUtils.args("id", id));
	}

	@EndpointImplementation(uris = "/api/adddeveloper")
	public RestResponse addDeveloper(String clientId, String newDev) throws NetworkException, ApiException {
		return genericPost("/api/adddeveloper", JrawUtils.args(
				"api_type", "json",
				"client_id", clientId,
				"name", newDev
		));
	}

	@EndpointImplementation(uris = "/api/removedeveloper")
	public RestResponse removeDeveloper(String clientId, String oldDev) throws NetworkException, ApiException {
		return genericPost("/api/removedeveloper", JrawUtils.args(
				"api_type", "json",
				"client_id", clientId,
				"name", oldDev
		));
	}

	/**
	 * Executes a generic POST request that returns a RedditResponse. Used primarily for convenience and standardization
	 * of the messages of RedditExceptions that are thrown.
	 *
	 * @param path       The path relative of the domain to send a request to
	 * @param args       The arguments to send in the POST body
	 * @return A representation of the response by the Reddit API
	 * @throws NetworkException If needsLogin is true and the user was not logged in, or there was an error making the
	 *                          HTTP request.
	 */
	private RestResponse genericPost(String path, Map<String, String> args) throws NetworkException, ApiException {
		RestResponse response = creator.execute(new RestRequest(HttpVerb.POST, path, args));
		if (response.hasErrors()) {
			throw response.getApiExceptions()[0];
		}

		return response;
	}

	private void checkIfOwns(Submission s) {
		if (!s.getAuthor().equals(getName())) {
			throw new IllegalArgumentException(String.format("Logged in user (%s) did not post this submission (by %s)", getName(), s.getAuthor()));
		}
	}

	/**
	 * This class provides a way to configure posting parameters of a new submission
	 */
	public static class SubmissionBuilder {
		private final SubmissionType type;
		private final String selfText;
		private final URL url;
		private final String subreddit;
		private final String title;
		private boolean saveAfter; // = false;
		private boolean sendRepliesToInbox; // = false;
		private boolean resubmit; // = false;

		/**
		 * Instantiates a new SubmissionBuilder that will result in a self post.
		 * @param selfText The body text of the submission, formatted in Markdown
		 * @param subreddit The subreddit to submit the link to (e.g. "funny", "pics", etc.)
		 * @param title The title of the submission
		 */
		public SubmissionBuilder(String selfText, String subreddit, String title) {
			this.type = SubmissionType.SELF;
			this.selfText = selfText;
			this.url = null;
			this.subreddit = subreddit;
			this.title = title;
		}

		/**
		 * Instantiates a new SubmissionBuilder that will result in a link post.
		 * @param url The URL that this submission will link to
		 * @param subreddit The subreddit to submit the link to (e.g. "funny", "pics", etc.)
		 * @param title The title of the submission
		 */
		public SubmissionBuilder(URL url, String subreddit, String title) {
			this.type = SubmissionType.LINK;
			this.url = url;
			this.selfText = null;
			this.subreddit = subreddit;
			this.title = title;
		}

		/**
		 * Whether to save after right after posting
		 * @param flag To save or not to save, that is the question
		 * @return This builder
		 */
		public SubmissionBuilder saveAfter(boolean flag) {
			this.saveAfter = flag;
			return this;
		}

		/**
		 * Whether to send top-level replies to your inbox
		 * @param flag Send replies to your inbox?
		 * @return This builder
		 */
		public SubmissionBuilder sendRepliesToInbox(boolean flag) {
			this.sendRepliesToInbox = flag;
			return this;
		}

		/**
		 * Set whether or not the Reddit API will return an error if the link's URL has already been posted
		 * @param flag If there should be an exception if there is already a post like this
		 * @return This builder
		 */
		public SubmissionBuilder resubmit(boolean flag) {
			this.resubmit = flag;
			return this;
		}
	}
}
