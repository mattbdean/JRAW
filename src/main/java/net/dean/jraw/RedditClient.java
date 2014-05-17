package net.dean.jraw;

import net.dean.jraw.models.core.Account;
import net.dean.jraw.models.core.Link;
import net.dean.jraw.models.core.Thing;
import org.apache.http.HttpException;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides access to the most basic Reddit features such as logging in.
 */
public class RedditClient {

	/**
	 * The host that will be used in all the HTTP requests.
	 */
	public static final String HOST = "www.reddit.com";

	/**
	 * The RestClient that will be used to execute various HTTP requests
	 */
	private RestClient restClient;

	/**
	 * Whether or not a user has been logged in or not
	 */
	private boolean loggedIn;

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
		this.loggedIn = false;
	}

	/**
	 * Logs in to an account and returns the data associated with it
	 *
	 * @param username The username to log in to
	 * @param password The password of the username
	 * @return An Account object that has the same username as the username parameter
	 * @throws RedditException If there was an error returned in the JSON
	 */
	public Account login(String username, String password) throws RedditException {
		try {
			RedditResponse loginResponse = restClient.post("/api/login/" + username,
					args("user", username, "passwd", password, "api_type", "json"));

			JsonNode errorsNode = loginResponse.getRootNode().get("json").get("errors");
			if (errorsNode.size() > 0) {
				throw new RedditException(errorsNode.get(0).asText());
			}

			loggedIn = true;

			return me();
		} catch (IOException | HttpException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Gets the currently logged in account
	 *
	 * @return The currently logged in account
	 * @throws RedditException If the user has not been logged in yet
	 */
	public Account me() throws RedditException {
		if (!loggedIn) {
			throw new RedditException("You are not logged in! Use RedditClient.login(user, pass)");
		}

		return genericGet("/api/me.json", Account.class);
	}

	/**
	 * Gets a user with a specific username
	 * @param username The name of the desired user
	 * @return An Account whose name matches the given username
	 * @throws RedditException If the user does not exist or there was a problem making the request
	 */
	public Account getUser(String username) throws RedditException {
		return genericGet("/user/" + username + "/about.json", Account.class);
	}

	/**
	 * Gets a link with a specific ID
	 *
	 * @param id The link's ID, ex: "92dd8"
	 * @return A new Link object
	 * @throws RedditException If the link does not exist or there was a problem making the request
	 */
	public Link getLink(String id) throws RedditException {
		return genericGet("/" + id + ".json", Link.class);
	}

	/**
	 * Executes a generic GET request and returns a Thing. Used primarily for convenience and standardization of the
	 * messages of the RedditExceptions that are thrown by the methods in this class
	 *
	 * @param path The path relative of the domain to send a request to
	 * @param thingClass The class to turn the request into
	 * @param <T> The return type of the request
	 * @return A new Thing
	 * @throws RedditException If there was a problem making the request
	 */
	private <T extends Thing> T genericGet(String path, Class<T> thingClass) throws RedditException{
		try {
			return restClient.get(path).as(thingClass);
		} catch (IOException | HttpException e) {
			throw new RedditException("Unable to make the request to " + path);
		}
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
	 * @param keysAndValues A list of strings to be condensed into a map. Must be of even length
	 * @return A map of the given keys and values array
	 * @throws java.lang.IllegalArgumentException If the length of the string array is not even
	 */
	public Map<String, String> args(String... keysAndValues) {
		if (keysAndValues.length % 2 != 0) {
			throw new IllegalArgumentException("Keys and values length must be even");
		}

		Map<String, String> args = new HashMap<>();
		for (int i = 0; i < keysAndValues.length; ) {
			args.put(keysAndValues[i++], keysAndValues[i++]);
		}

		return args;
	}
}
