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
import java.util.Optional;

public class LoggedInAccount extends Account {
	private RedditClient creator;

	public LoggedInAccount(JsonNode data, RedditClient creator) {
		super(data);
		this.creator = creator;
	}


	/**
	 * Submits a link
	 *
	 * @param type               The type of submission, either ${@link net.dean.jraw.models.SubmissionType#LINK} or
	 *                           ${@link net.dean.jraw.models.SubmissionType#SELF}.
	 * @param url                The URL that this submission will link to. Not necessary if the submission type is
	 *                           ${@link net.dean.jraw.models.SubmissionType#SELF}
	 * @param selfText           The body text of the submission, formatted in Markdown. Not necessary if the submission type is
	 *                           ${@link net.dean.jraw.models.SubmissionType#LINK}
	 * @param subreddit          The subreddit to submit the link to (e.g. "funny", "pics", etc.)
	 * @param title              The title of the submission
	 * @param saveAfter          Whether to save the submission right after posting
	 * @param sendRepliesToInbox Whether to send all top level replies to the poster's inbox
	 * @param resubmit           Whether the Reddit API will return an error if the link's URL has already been posted
	 * @return A representation of the newly submitted Link
	 * @throws net.dean.jraw.http.NetworkException If there was a problem sending the HTTP request
	 * @throws net.dean.jraw.ApiException If the API returned an error
	 */
	public Submission submitContent(SubmissionType type, Optional<URL> url, Optional<String> selfText, String subreddit,
	                                String title, boolean saveAfter, boolean sendRepliesToInbox, boolean resubmit) throws NetworkException, ApiException {

		return submitContent(type, url, selfText, subreddit, title, saveAfter, sendRepliesToInbox, resubmit, Optional.empty(), Optional.empty());
	}

	/**
	 * Submits a link with a given captcha. Only really needed if the user has less than 10 link karma.
	 *
	 * @param type               The type of submission, either ${@link net.dean.jraw.models.SubmissionType#LINK} or
	 *                           ${@link net.dean.jraw.models.SubmissionType#SELF}.
	 * @param url                The URL that this submission will link to. Not necessary if the submission type is
	 *                           ${@link net.dean.jraw.models.SubmissionType#SELF}
	 * @param selfText           The body text of the submission, formatted in Markdown. Not necessary if the submission
	 *                           type is ${@link net.dean.jraw.models.SubmissionType#LINK}
	 * @param subreddit          The subreddit to submit the link to (e.g. "funny", "pics", etc.)
	 * @param title              The title of the submission
	 * @param saveAfter          Whether to save the submission right after posting
	 * @param sendRepliesToInbox Whether to send all top level replies to the poster's inbox
	 * @param resubmit           Whether the Reddit API will return an error if the link's URL has already been posted
	 * @param captcha            The captcha the user is trying to answer
	 * @param captchaAttempt     The user's attempt at the captcha
	 * @return A representation of the newly submitted Link
	 * @throws NetworkException If there was a problem sending the HTTP request
	 * @throws net.dean.jraw.ApiException If the API returned an error
	 */
	@EndpointImplementation(uris = "/api/submit")
	public Submission submitContent(SubmissionType type, Optional<URL> url, Optional<String> selfText, String subreddit,
	                                String title, boolean saveAfter, boolean sendRepliesToInbox, boolean resubmit, Optional<Captcha> captcha,
	                                Optional<String> captchaAttempt) throws NetworkException, ApiException {

		Map<String, String> args = JrawUtils.args(
				"api_type", "json",
				"extension", "json",
				"kind", type.name().toLowerCase(),
				"resubmit", resubmit,
				"save", saveAfter,
				"sendreplies", sendRepliesToInbox,
				"sr", subreddit,
				"then", "comments",
				"title", title
		);

		if (type == SubmissionType.LINK) {
			args.put("url", url.get().toExternalForm());
		} else if (type == SubmissionType.SELF) {
			args.put("text", selfText.get());
		} else {
			throw new IllegalArgumentException("Unknown SubmissionType: " + type);
		}

		if (captcha.isPresent()) {
			if (!captchaAttempt.isPresent()) {
				throw new IllegalArgumentException("Captcha present but the attempt is not");
			}

			args.put("iden", captcha.get().getId());
			args.put("captcha", captchaAttempt.get());
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
	 * Saves a submission.
	 *
	 * @param s The submission to save
	 * @return The JSON response from the API
	 * @throws NetworkException If there was a problem sending the HTTP request
	 * @throws ApiException If the API returned an error
	 */
	@EndpointImplementation(uris = "/api/save")
	public RestResponse save(Submission s) throws NetworkException, ApiException {
		return genericPost("/api/save", JrawUtils.args(
				"id", ThingType.LINK.getPrefix() + "_" + s.getId()
		));
	}

	/**
	 * Unsaves a submission
	 *
	 * @param s The sumbission to unsave
	 * @return The JSON response from the API
	 * @throws NetworkException If there was a problem sending the HTTP request
	 * @throws ApiException If the API returned an error
	 */
	@EndpointImplementation(uris = "/api/unsave")
	public RestResponse unsave(Submission s) throws NetworkException, ApiException {
		return genericPost("/api/unsave", JrawUtils.args(
				"id", ThingType.LINK.getPrefix() + "_" + s.getId()
		));
	}

	@EndpointImplementation(uris = "/api/sendreplies")
	public RestResponse setSendRepliesToInbox(Submission s, boolean send) throws NetworkException, ApiException {
		return genericPost("/api/sendreplies", JrawUtils.args(
				"id", s.getId(),
				"state", Boolean.toString(send)
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
}
