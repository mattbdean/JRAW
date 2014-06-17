package net.dean.jraw;

import net.dean.jraw.endpointgen.EndpointImplementation;
import net.dean.jraw.models.Captcha;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.core.Account;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.pagination.SimplePaginator;
import net.dean.jraw.pagination.UserPaginatorSubmission;
import net.dean.jraw.pagination.Where;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;

/**
 * This class provides access to the most basic Reddit features such as logging in.
 */
public class RedditClient extends RestClient {

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
	 * The amount of requests allowed per minute without using OAuth
	 */
	private static final int REQUESTS_PER_MINUTE = 30;

	/**
	 * Whether to stall the requests to make sure that no more than ${@value #REQUESTS_PER_MINUTE} requests have been made
	 * in the past minute
	 */
	private boolean requestManagement;

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
		super(HOST, userAgent);
		this.requestManagement = true;
	}

	/**
	 * Whether to automatically manage the execution of HTTP requests based on time (enabled by default). If there has
	 * been more than 30 requests in the last minute, this class will wait to execute the next request in order to
	 * minimize the chance of getting IP banned by Reddit, or simply having the API return a 403.
	 *
	 * @param enabled Whether to enable request management
	 */
	public void setRequestManagementEnabled(boolean enabled) {
		this.requestManagement = enabled;
	}

	@Override
	public RestResponse execute(RestRequest request) throws NetworkException {
		if (!requestManagement) {
			// All in your hands, buddy
			return super.execute(request);
		}

		// No history, safe to assume that there were no recent requests
		if (history.size() == 0) {
			return super.execute(request);
		}

		// Transverse the history backwards and look for the latest request executed just after one minute
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime executed;
		int execAmount = 0; // Amount of times executed in the last minute
		for (int i = history.size() - 1; i >= 0; i--) {
			executed = history.get(i).getExecuted();
			// Request was executed before 60 seconds ago or has there been over 30 requests executed already?
			if (executed.isBefore(now.minus(60, ChronoUnit.SECONDS)) || ++execAmount >= REQUESTS_PER_MINUTE) {
				if (i >= 0) {
					// Make sure that we leave enough time to make sure we have 30 requests max in the last minute
					LocalDateTime before = history.get(i - 1).getExecuted();
					Duration timeToWait = Duration.between(executed, before);
					try {
						// Wait the time between the two times
						Thread.sleep(timeToWait.toMillis());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// We've waited our time
				break;
			}
		}

		return super.execute(request);
	}


	/**
	 * Logs in to an account and returns the data associated with it
	 *
	 * @param username The username to log in to
	 * @param password The password of the username
	 * @return An Account object that has the same username as the username parameter
	 * @throws NetworkException If there was an error returned in the JSON
	 */
	@EndpointImplementation(uris = "/api/login")
	public LoggedInAccount login(String username, String password) throws NetworkException, ApiException {
		RestResponse loginResponse = new RestResponse(http.execute(HttpVerb.POST, HOST_SSL, "/api/login",
				JrawUtils.args("user", username, "passwd", password, "api_type", "json")));

		if (loginResponse.hasErrors()) {
			throw loginResponse.getApiExceptions()[0];
		}

		List<Header> headers = http.getDefaultHeaders();

		Header h = new BasicHeader(HEADER_MODHASH,
				loginResponse.getJson().get("json").get("data").get("modhash").getTextValue());

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

		return new LoggedInAccount(execute(new RestRequest(HttpVerb.GET, "/api/me.json")).getJson().get("data"), this);
	}

	/**
	 * Gets the currently logged in account
	 *
	 * @return The currently logged in account
	 * @throws NetworkException If the user has not been logged in yet
	 */
	@EndpointImplementation(uris = "/api/me.json")
	public Account me() throws NetworkException {
		loginCheck();
		return execute(new RestRequest(HttpVerb.GET, "/api/me.json")).as(Account.class);
	}

	/**
	 * Tests if the user is logged in by checking if a cookie is set called "reddit_session" and its domain is "reddit.com"
	 *
	 * @return True if the user is logged in
	 */
	public boolean isLoggedIn() {
		for (Cookie cookie : http.getCookieStore().getCookies()) {
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
	@EndpointImplementation(uris = "/api/needs_captcha.json")
	public boolean needsCaptcha() throws NetworkException {
		try {
			// This endpoint does not return JSON, but rather just "true" or "false"
			CloseableHttpResponse response = http.execute(HttpVerb.GET, HOST, "/api/needs_captcha.json");

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
	@EndpointImplementation(uris = "/api/new_captcha")
	public Captcha getNewCaptcha() throws NetworkException {
		try {
			RestResponse response = execute(new RestRequest(HttpVerb.POST, "/api/new_captcha"));

			// Some strange response you got there, reddit...
			String id = response.getJson().get("jquery").get(11).get(3).get(0).getTextValue();

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
	@EndpointImplementation(uris = "/captcha/iden")
	public Captcha getCaptcha(String id) throws NetworkException {
		try {
			CloseableHttpResponse response = http.execute(HttpVerb.GET, HOST, "/captcha/" + id + ".png");

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
	@EndpointImplementation(uris = "/user/username/about.json")
	public Account getUser(String username) throws NetworkException {
		return execute(new RestRequest(HttpVerb.GET, "/user/" + username + "/about.json")).as(Account.class);
	}

	/**
	 * Gets a link with a specific ID
	 *
	 * @param id The link's ID, ex: "92dd8"
	 * @return A new Link object
	 * @throws NetworkException If the link does not exist or there was a problem making the request
	 */
	public Submission getSubmission(String id) throws NetworkException {
		return execute(new RestRequest(HttpVerb.GET, "/" + id + ".json")).as(Submission.class);
	}

	/**
	 * Checks a user is logged in. If not, throws a RedditException
	 *
	 * @throws NetworkException If there is no logged in user
	 */
	private void loginCheck() throws NetworkException {
		if (!isLoggedIn()) {
			throw new NetworkException("You are not logged in! Use RedditClient.login(user, pass)");
		}
	}

	/**
	 * Gets a Paginator to browse the front page of Reddit
	 *
	 * @return A new SimplePaginator for the front page
	 */
	public SimplePaginator getFrontPage() {
		return SimplePaginator.ofFrontPage(this);
	}

	/**
	 * Gets a Paginator to browse a particular subreddit
	 *
	 * @param subreddit The subreddit to browse
	 * @return A new SimplePaginator for a particular subreddit
	 */
	public SimplePaginator getSubreddit(String subreddit) {
		return SimplePaginator.ofSubreddit(this, subreddit);
	}

	/**
	 * Gets a Paginator to interact with /user/&lt;username&gt;/&lt;where&gt; API endpoints
	 *
	 * @param username The username to use
	 * @param where Where to browse
	 * @return A new UserPaginatorSubmission to browse submissions
	 */
	public UserPaginatorSubmission getUserPaginator(String username, Where where) {
		return new UserPaginatorSubmission(this, username, where);
	}
}
