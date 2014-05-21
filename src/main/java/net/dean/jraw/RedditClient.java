package net.dean.jraw;

import net.dean.jraw.models.Captcha;
import net.dean.jraw.models.SubmissionType;
import net.dean.jraw.models.core.Account;
import net.dean.jraw.models.core.Link;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * This class provides access to the most basic Reddit features such as logging in.
 */
public class RedditClient {

	/**
	 * The host that will be used to execute basic HTTP requests.
	 */
	public static final String HOST = "www.reddit.com";

	/**
	 * The host that will be used to execute secure HTTP requests
	 */
	public static final String HOST_SSL = "ssl.reddit.com";

	/**
	 * The name of the header that will be assigned upon a successful login
	 */
	private static final String HEADER_MODHASH = "X-Modhash";

	/**
	 * The RestClient that will be used to execute various HTTP requests
	 */
	private RestClient restClient;

	/**
	 * Instantiates a new RedditClient and adds the given user agent to the default headers of the RestClient
	 *
	 * @param userAgent The User-Agent header that will be sent with all the HTTP requests.
	 *                  <blockquote>Change your client's
	 *                  User-Agent string to something unique and descriptive, preferably referencing your reddit
	 *                  username. From the <a href="https://github.com/reddit/reddit/wiki/API">Reddit Wiki on Github</a>:
	 *                  <ul>
	 *                  <li>Many default User-Agents (like "Python/urllib" or "Java") are drastically limited to
	 *                  encourage unique and descriptive user-agent strings.</li>
	 *                  <li>If you're making an application for others to use, please include a version number in
	 *                  the user agent. This allows us to block buggy versions without blocking all versions of
	 *                  your app.</li>
	 *                  <li>NEVER lie about your user-agent. This includes spoofing popular browsers and spoofing
	 *                  other bots. We will ban liars with extreme prejudice.</li>
	 *                  </ul>
	 *                  </blockquote>
	 */
	public RedditClient(String userAgent) {
		this.restClient = new RestClient(HOST, userAgent);
	}

	/**
	 * Logs in to an account and returns the data associated with it
	 *
	 * @param username The username to log in to
	 * @param password The password of the username
	 * @return An Account object that has the same username as the username parameter
	 * @throws NetworkException If there was an error returned in the JSON
	 */
	public Account login(String username, String password) throws NetworkException, ApiException {
		RedditResponse loginResponse = new RedditResponse(restClient.getHttpHelper().execute(HttpVerb.POST, HOST_SSL, "/api/login",
				args("user", username, "passwd", password, "api_type", "json")));

		if (loginResponse.hasErrors()) {
			throw loginResponse.getApiExceptions()[0];
		}

		List<Header> headers = restClient.getHttpHelper().getDefaultHeaders();

		Header h = new BasicHeader(HEADER_MODHASH,
				loginResponse.getRootNode().get("json").get("data").get("modhash").getTextValue());

		// Add the X-Modhash header, or update it if it already exists
		Header modhashHeader = null;
		for (Header header : headers) {
			if (header.getName().equals(HEADER_MODHASH)) {
				modhashHeader = header;
			}
		}

		if (modhashHeader != null) {
			headers.remove(modhashHeader);
		}
		headers.add(h);

		return me();
	}

	/**
	 * Gets the currently logged in account
	 *
	 * @return The currently logged in account
	 * @throws NetworkException If the user has not been logged in yet
	 */
	public Account me() throws NetworkException {
		loginCheck();
		return restClient.get("/api/me.json").as(Account.class);
	}

	/**
	 * Tests if the user is logged in by checking if a cookie is set called "reddit_session" and its domain is "reddit.com"
	 *
	 * @return True if the user is logged in
	 */
	public boolean isLoggedIn() {
		for (Cookie cookie : restClient.getHttpHelper().getCookieStore().getCookies()) {
			if (cookie.getName().equals("reddit_session") && cookie.getDomain().equals("reddit.com")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the current user needs a captcha to do specific actions such as submit links and compose private messages.
	 * This will always be false if there is no logged in user. Usually, this method will return <code>true</code> if
	 * the current logged in user has less than 10 link karma
	 *
	 * @return True if the user needs a captcha to do a specific action, else if not or not logged in.
	 * @throws NetworkException
	 */
	public boolean needsCaptcha() throws NetworkException {
		if (isLoggedIn()) {
			return false;
		}
		try {
			// This endpoint does not return JSON, but rather just "true" or "false"
			CloseableHttpResponse response = restClient.getHttpHelper().execute(HttpVerb.GET, HOST, "/api/needs_captcha.json");

			// Read the contents of the response
			Scanner s = new Scanner(response.getEntity().getContent()).useDelimiter("\\A");
			String raw = s.hasNext() ? s.next() : "";

			return Boolean.parseBoolean(raw);
		} catch (NetworkException | IOException e) {
			throw new NetworkException("Unable to make the request to /api/needs_captcha.json", e);
		}
	}

	/**
	 * Fetches a new captcha from the API
	 *
	 * @return A new Captcha
	 * @throws NetworkException If there was a problem executing the HTTP request
	 */
	public Captcha getNewCaptcha() throws NetworkException {
		try {
			RedditResponse response = restClient.post("/api/new_captcha");

			// Some strange response here
			String id = response.getRootNode().get("jquery").get(11).get(3).get(0).getTextValue();

			return getCaptcha(id);
		} catch (NetworkException e) {
			throw new NetworkException("Unable to make the request to /api/new_captcha", e);
		}
	}

	/**
	 * Gets a Captcha by its ID
	 *
	 * @param id The ID of the wanted captcha
	 * @return A new Captcha object
	 * @throws NetworkException If there was a problem executing the HTTP request
	 */
	public Captcha getCaptcha(String id) throws NetworkException {
		try {
			CloseableHttpResponse response = restClient.getHttpHelper().execute(HttpVerb.GET, HOST, "/captcha/" + id + ".png");

			return new Captcha(id, response.getEntity().getContent());
		} catch (IOException | NetworkException e) {
			throw new NetworkException("Unable to get the captcha \"" + id + "\"", e);
		}
	}

	/**
	 * Gets a user with a specific username
	 *
	 * @param username The name of the desired user
	 * @return An Account whose name matches the given username
	 * @throws NetworkException If the user does not exist or there was a problem making the request
	 */
	public Account getUser(String username) throws NetworkException {
		return restClient.get("/user/" + username + "/about.json").as(Account.class);
	}

	/**
	 * Gets a link with a specific ID
	 *
	 * @param id The link's ID, ex: "92dd8"
	 * @return A new Link object
	 * @throws NetworkException If the link does not exist or there was a problem making the request
	 */
	public Link getLink(String id) throws NetworkException {
		return restClient.get("/" + id + ".json").as(Link.class);
	}

	/**
	 * Submits a link
	 *
	 * @param type The type of submission, either ${@link net.dean.jraw.models.SubmissionType#LINK} or
	 *             ${@link net.dean.jraw.models.SubmissionType#SELF}.
	 * @param url The URL that this submission will link to. Not necessary if the submission type is
	 *            ${@link net.dean.jraw.models.SubmissionType#SELF}
	 * @param selfText The body text of the submission, formatted in Markdown. Not necessary if the submission type is
	 *                 ${@link net.dean.jraw.models.SubmissionType#LINK}
	 * @param subreddit The subreddit to submit the link to (e.g. "funny", "pics", etc.)
	 * @param title The title of the submission
	 * @param saveAfter Whether to save the submission right after posting
	 * @param sendRepliesToInbox Whether to send all top level replies to the poster's inbox
	 * @param resubmit Whether the Reddit API will return an error if the link's URL has already been posted
	 * @return A representation of the newly submitted Link
	 * @throws NetworkException If there was a problem sending the HTTP request
	 */
	public Link submitLink(SubmissionType type, Optional<URL> url, Optional<String> selfText, String subreddit,
	                                 String title, boolean saveAfter, boolean sendRepliesToInbox, boolean resubmit) throws NetworkException, ApiException {

		return submitLink(type, url, selfText, subreddit, title, saveAfter, sendRepliesToInbox, resubmit, Optional.empty(), Optional.empty());
	}

	/**
	 * Submits a link with a given captcha. Only really needed if the user has less than 10 link karma.
	 *
	 * @param type The type of submission, either ${@link net.dean.jraw.models.SubmissionType#LINK} or
	 *             ${@link net.dean.jraw.models.SubmissionType#SELF}.
	 * @param url The URL that this submission will link to. Not necessary if the submission type is
	 *            ${@link net.dean.jraw.models.SubmissionType#SELF}
	 * @param selfText The body text of the submission, formatted in Markdown. Not necessary if the submission type is
	 *                 ${@link net.dean.jraw.models.SubmissionType#LINK}
	 * @param subreddit The subreddit to submit the link to (e.g. "funny", "pics", etc.)
	 * @param title The title of the submission
	 * @param saveAfter Whether to save the submission right after posting
	 * @param sendRepliesToInbox Whether to send all top level replies to the poster's inbox
	 * @param resubmit Whether the Reddit API will return an error if the link's URL has already been posted
	 * @param captcha The captcha the user is trying to answer
	 * @param captchaAttempt The user's attempt at the captcha
	 * @return A representation of the newly submitted Link
	 * @throws NetworkException If there was a problem sending the HTTP request
	 */
	public Link submitLink(SubmissionType type, Optional<URL> url, Optional<String> selfText, String subreddit,
	                       String title, boolean saveAfter, boolean sendRepliesToInbox, boolean resubmit, Optional<Captcha> captcha,
	                       Optional<String> captchaAttempt) throws NetworkException, ApiException {
		loginCheck();

		Map<String, String> args = args(
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

		RedditResponse response = genericPost("/api/submit", args, true);
		String jsonLink = response.getRootNode().get("json").get("data").get("url").getTextValue();

		return restClient.get(jsonLink).as(Link.class);

	}

	/**
	 * Checks a user is logged in. If not, throws a RedditException
	 * @throws NetworkException If there is no logged in user
	 */
	private void loginCheck() throws NetworkException {
		if (!isLoggedIn()) {
			throw new NetworkException("You are not logged in! Use RedditClient.login(user, pass)");
		}
	}

	/**
	 * Executes a generic POST request that returns a RedditResponse. Used primarily for convenience and standardization
	 * of the messages of RedditExceptions that are thrown.
	 *
	 * @param path The path relative of the domain to send a request to
	 * @param args The arguments to send in the POST body
	 * @param needsLogin Whether or not to check for a logged in user
	 * @return A representation of the response by the Reddit API
	 * @throws NetworkException If needsLogin is true and the user was not logged in, or there was an error making the
	 *         HTTP request.
	 */
	private RedditResponse genericPost(String path, Map<String, String> args, boolean needsLogin) throws NetworkException, ApiException {
		if (needsLogin) {
			loginCheck();
		}

		RedditResponse response = restClient.post(path, args);
		if (response.hasErrors()) {
			throw response.getApiExceptions()[0];
		}

		return response;
	}

	/**
	 * Convenience method to combine a list of strings into a map. Sample usage:<br>
	 * <br>
	 * <code>
	 * Map&lt;String, String&gt; mapOfArguments = args("key1", "value1", "key2", "value2");
	 * </code><br><br>
	 * would result in this:
	 * <pre>
	 * {@code
	 * {
	 *     "key1" => "value1",
	 *     "key2" => "value2"
	 * }
	 * }
	 * </pre>
	 *
	 * @param keysAndValues A list of objects to be turned into strings and condensed into a map. Must be of even length
	 * @return A map of the given keys and values array
	 * @throws java.lang.IllegalArgumentException If the length of the string array is not even
	 */
	public Map<String, String> args(Object... keysAndValues) {
		if (keysAndValues.length % 2 != 0) {
			throw new IllegalArgumentException("Keys and values length must be even");
		}

		Map<String, String> args = new HashMap<>();
		for (int i = 0; i < keysAndValues.length; ) {
			args.put(String.valueOf(keysAndValues[i++]), String.valueOf(keysAndValues[i++]));
		}

		return args;
	}
}
