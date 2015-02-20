package net.dean.jraw.managers;

import net.dean.jraw.*;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.models.FlairTemplate;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Thing;
import net.dean.jraw.models.attr.Votable;

import java.util.Map;

/**
 * This class manages actions most commonly reserved for moderators (although some of them can be used on yourself
 * without moderator permissions).
 */
public class ModerationManager extends AbstractManager {
	/**
	 * Instantiates a new AbstractManager
	 *
	 * @param reddit The RedditClient to use
	 */
	public ModerationManager(RedditClient reddit) {
		super(reddit);
	}

	/**
	 * Sets whether or not this submission should be marked as not safe for work
	 *
	 * @param s    The submission to modify
	 * @param nsfw Whether or not this submission is not safe for work
	 * @throws net.dean.jraw.http.NetworkException If the request was not successful
	 * @throws net.dean.jraw.ApiException          If the API returned an error
	 */
	@EndpointImplementation({Endpoints.MARKNSFW, Endpoints.UNMARKNSFW})
	public void setNsfw(Submission s, boolean nsfw) throws NetworkException,
			ApiException {
		// "/api/marknsfw" if nsfw == true, "/api/unmarknsfw" if nsfw == false
		genericPost(reddit.request()
				.endpoint(nsfw ? Endpoints.MARKNSFW : Endpoints.UNMARKNSFW)
				.post(JrawUtils.mapOf(
						"id", s.getFullName()
				)).build());
	}

	/**
	 * Deletes a submission that you posted
	 *
	 * @param thing The submission to delete
	 * @param <T>   The Votable Thing to delete
	 * @throws NetworkException If the request was not successful
	 * @throws ApiException     If the API returned an error
	 */
	public <T extends Thing & Votable> void delete(T thing)
			throws NetworkException, ApiException {
		delete(thing.getFullName());
	}

	/**
	 * Deletes a comment or submission that you posted
	 *
	 * @param id The ID of the submission or comment to delete
	 * @throws NetworkException If the request was not successful
	 * @throws ApiException     If the API returned an error
	 */
	@EndpointImplementation(Endpoints.DEL)
	public void delete(String id) throws NetworkException, ApiException {
		genericPost(reddit.request()
				.endpoint(Endpoints.DEL)
				.post(JrawUtils.mapOf(
						"id", id
				)).build());
	}

	/**
	 * Set or unset a self post as a sticky. You must be a moderator of the subreddit the submission was posted in for
	 * this request to complete successfully.
	 *
	 * @param s The submission to set as a sticky. Must be a self post
	 * @param sticky Whether or not to set the submission as a stickied post
	 * @throws NetworkException If the request was not successful
	 * @throws ApiException If the Reddit API returned an error
	 */
	@EndpointImplementation(Endpoints.SET_SUBREDDIT_STICKY)
	public void setSticky(Submission s, boolean sticky) throws NetworkException, ApiException {
		genericPost(reddit.request()
				.endpoint(Endpoints.SET_SUBREDDIT_STICKY)
				.post(JrawUtils.mapOf(
						"api_type", "json",
						"id", s.getFullName(),
						"state", sticky
				)).build());
	}

	/**
	 * Sets the flair for the currently authenticated user
	 * @param subreddit The subreddit to set the flair on
	 * @param template The template to use
	 * @param text Optional text that will be used if the FlairTemplate's text is editable. If this is null and the
	 *             template is editable, the template's default text will be used.
	 * @throws NetworkException If the request was not successful
	 * @throws ApiException If the API returned an error
	 */
	public void setFlair(String subreddit, FlairTemplate template, String text) throws NetworkException, ApiException {
		setFlair(subreddit, template, text, (String) null);
	}

	/**
	 * Sets the flair for a certain user. Must be a moderator of the subreddit if the user is not the currently
	 * authenticated user.
	 *
	 * @param subreddit The subreddit to set the flair on
	 * @param template The template to use
	 * @param text Optional text that will be used if the FlairTemplate's text is editable. If this is null and the
	 *             template is editable, the template's default text will be used.
	 * @param username The name of the user to set the flair for. If this is null the authenticated user's name will be
	 *                 used.
	 * @throws NetworkException If the request was not successful
	 * @throws ApiException If the API returned an error
	 */
	public void setFlair(String subreddit, FlairTemplate template, String text, String username) throws NetworkException, ApiException {
		setFlair(subreddit, template, text, null, username);
	}

	/**
	 * Sets the flair for a certain submission. If the currently authenticated user is <em>not</em> a moderator of the
	 * subreddit where the submission was posted, then the user must have posted the submission.
	 *
	 * @param subreddit The subreddit to set the flair on
	 * @param template The template to use
	 * @param text Optional text that will be used if the FlairTemplate's text is editable. If this is null and the
	 *             template is editable, the template's default text will be used.
	 * @param submission The submission to set the flair for
	 * @throws NetworkException If the request was not successful
	 * @throws ApiException If the API returned an error
	 */
	public void setFlair(String subreddit, FlairTemplate template, String text, Submission submission) throws NetworkException, ApiException {
		setFlair(subreddit, template, text, submission, null);
	}

	/**
	 * Sets either a user's flair or a submission's flair. If the submission and username are both non-null, then the
	 * submission will be used in the request. If they are both null and there is no authenticated user, then an
	 * IllegalArgumentException will be thrown.
	 *
	 * @param subreddit The subreddit where the flair will take effect
	 * @param template The template to use
	 * @param text Optional text that will be used if the FlairTemplate's text is editable. If this is null and the
	 *             template is editable, the template's default text will be used.
	 * @param submission The submission to set the flair for
	 * @param username The name of the user to set the flair for. If this is null the authenticated user's name will be
	 *                 used.
	 * @throws IllegalArgumentException If both the submission and the username are null
	 * @throws NetworkException If the request was not successful
	 */
	@EndpointImplementation(Endpoints.SELECTFLAIR)
	private void setFlair(String subreddit, FlairTemplate template, String text, Submission submission, String username)
			throws IllegalArgumentException, NetworkException, ApiException {
		if (subreddit == null) {
			throw new IllegalArgumentException("subreddit cannot be null");
		}
		Map<String, String> args = JrawUtils.mapOf(
				"api_type", "json",
				"flair_template_id", template.getId()
		);

		if (submission != null) {
			args.put("link", submission.getFullName());
		} else {
			if (username == null) {
				if (reddit.getAuthenticationMethod() == AuthenticationMethod.NOT_YET) {
					throw new IllegalArgumentException("Not logged in and both submission and username were null");
				}
                if (!reddit.hasActiveUserContext())
                    throw new IllegalStateException("Cannot set the flair for self because there is no active user context");
				username = reddit.getAuthenticatedUser();
			}
			args.put("name", username);
		}

		if (template.isTextEditable()) {
			if (text == null) {
				// Set default text flair if none is provided
				text = template.getText();
			}
			args.put("text", text);
		}

		RestResponse response = reddit.execute(reddit.request()
				.post(args)
				.path("/r/" + subreddit + Endpoints.SELECTFLAIR.getEndpoint().getUri())
				.build());
		if (response.hasErrors()) {
			throw response.getError();
		}
	}
}
