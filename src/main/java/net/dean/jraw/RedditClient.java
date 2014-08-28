package net.dean.jraw;

import net.dean.jraw.http.*;
import net.dean.jraw.models.Captcha;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.MultiReddit;
import net.dean.jraw.models.RenderStringPair;
import net.dean.jraw.models.core.Account;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.models.core.Subreddit;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicHeader;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static net.dean.jraw.http.HttpVerb.*;

/**
 * This class provides access to the most basic Reddit features such as logging in.
 */
public class RedditClient extends RestClient {

    /** The host that will be used to execute basic HTTP requests */
    public static final String HOST = "www.reddit.com";

    /** The host that will be used to execute secure HTTP requests */
    public static final String HOST_SSL = "ssl.reddit.com";

    /** The name of the header that will be assigned upon a successful login */
    private static final String HEADER_MODHASH = "X-Modhash";

    /** The amount of requests allowed per minute without using OAuth */
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
                if (i > 0) {
                    // Make sure that we leave enough time to make sure we have 30 requests max in the last minute
                    LocalDateTime before = history.get(i - 1).getExecuted();
                    Duration timeToWait = Duration.between(executed, before);
                    long millis = timeToWait.toMillis();
                    if (millis <= 0) {
                        millis = -millis;
                    }
                    try {
                        // Wait the time between the two times
                        JrawUtils.logger().info("Sleeping for {} milliseconds", millis);
                        Thread.sleep(millis);
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
     * @throws NetworkException If there was a problem sending the request
     * @throws ApiException If the API returned an error (most likely because of an incorrect password)
     */
    @EndpointImplementation(uris = "GET /api/login")
    public LoggedInAccount login(String username, String password) throws NetworkException, ApiException {
        RestResponse loginResponse = new RestResponse(http.execute(new HttpHelper.RequestBuilder(POST, HOST_SSL, "/api/login")
                .args(JrawUtils.args("user", username, "passwd", password, "api_type", "json"))));

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

        return new LoggedInAccount(execute(new RestRequest(GET, "/api/me.json")).getJson().get("data"), this);
    }

    /**
     * Gets the currently logged in account
     *
     * @return The currently logged in account
     * @throws NetworkException If the user has not been logged in yet
     */
    @EndpointImplementation(uris = "GET /api/me.json")
    public Account me() throws NetworkException {
        loginCheck();
        return execute(new RestRequest(GET, "/api/me.json")).as(Account.class);
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
     * This will always be true if there is no logged in user. Usually, this method will return {@code true} if
     * the current logged in user has more than 10 link karma
     *
     * @return True if the user needs a captcha to do a specific action, else if not or not logged in.
     * @throws NetworkException If there was an issue sending the HTTP request
     */
    @EndpointImplementation(uris = "GET /api/needs_captcha.json")
    public boolean needsCaptcha() throws NetworkException {
        try {
            // This endpoint does not return JSON, but rather just "true" or "false"
            RestResponse response = execute(new RestRequest(GET, "/api/needs_captcha.json"));
            return Boolean.parseBoolean(response.getRaw());
        } catch (NetworkException e) {
            throw new NetworkException("Unable to make the request to /api/needs_captcha.json", e);
        }
    }

    /**
     * Fetches a new captcha from the API
     *
     * @return A new Captcha
     * @throws NetworkException If there was a problem executing the HTTP request
     */
    @EndpointImplementation(uris = "POST /api/new_captcha")
    public Captcha getNewCaptcha() throws NetworkException {
        try {
            RestResponse response = execute(new RestRequest(POST, "/api/new_captcha"));

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
    @EndpointImplementation(uris = "GET /captcha/{iden}")
    public Captcha getCaptcha(String id) throws NetworkException {
        try {
            CloseableHttpResponse response = http.execute(new HttpHelper.RequestBuilder(GET, HOST, "/captcha/" + id + ".png"));

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
    @EndpointImplementation(uris = "GET /user/{username}/about.json")
    public Account getUser(String username) throws NetworkException {
        return execute(new RestRequest(GET, "/user/" + username + "/about.json")).as(Account.class);
    }

    /**
     * Gets a link with a specific ID
     *
     * @param id The link's ID, ex: "92dd8"
     * @return A new Link object
     * @throws NetworkException If the link does not exist or there was a problem making the request
     */
    public Submission getSubmission(String id) throws NetworkException {
        return execute(new RestRequest(GET, "/" + id + ".json")).as(Submission.class);
    }

    /**
     * Gets a Subreddit
     *
     * @param name The subreddit's name
     * @return A new Subreddit object
     * @throws NetworkException If there was a problem executing the request
     */
    @EndpointImplementation(uris = "GET /r/{subreddit}/about.json")
    public Subreddit getSubreddit(String name) throws NetworkException {
        return execute(new RestRequest(GET, "/r/" + name + "/about.json")).as(Subreddit.class);
    }

    /**
     * Checks if a given username is available
     *
     * @param name The username to test
     * @return True if that username is available for registration, false if else
     * @throws NetworkException If there was a problem executing the request
     */
    @EndpointImplementation(uris = "GET /api/username_available.json")
    public boolean isUsernameAvailable(String name) throws NetworkException {
        return Boolean.parseBoolean(execute(new RestRequest(GET, "/api/username_available.json?user=" + name)).getRaw());
    }

    /**
     * Gets a publicly available MultiReddit
     * @param username The owner of the multireddit
     * @param multiName The name of the multireddit
     * @return A MultiReddit
     * @throws NetworkException If there was a problem making the request
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(uris = {
            "GET /api/multi/{multipath}",
            "GET /api/multi/{multipath}/r/{srname}"
    })
    public MultiReddit getPublicMulti(String username, String multiName) throws NetworkException, ApiException {
        JsonNode node = execute(new RestRequest(GET, String.format("/api/multi/user/%s/m/%s", username, multiName))).getJson();
        checkMultiRedditError(node);
        return new MultiReddit(node.get("data"));
    }

    /**
     * Fetches the description of a public or self-owned multireddit. The first String in the resulting array is the
     * Markdown version of the text, while the second is the HTML version
     *
     * @param username The owner of the multireddit
     * @param multiName The name of the multireddit
     * @return A String array in which the first index is Markdown and the second is HTML
     * @throws NetworkException If there was a problem sending the request
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(uris = "GET /api/multi/{multipath}/description")
    public RenderStringPair getPublicMultiDescription(String username, String multiName) throws NetworkException, ApiException {
        JsonNode node = execute(new RestRequest(GET, String.format("/api/multi/user/%s/m/%s/description", username, multiName))).getJson();
        checkMultiRedditError(node);
        node = node.get("data");
        return new RenderStringPair(node.get("body_md").asText(), node.get("body_html").asText());
    }

    /**
     * Gets a random submission
     * @return A random submission
     * @throws NetworkException If there was a problem executing the request
     */
    public Submission getRandom() throws NetworkException {
        return getRandom(null);
    }

    /**
     * Gets a random submission from a specific subreddit
     * @param subreddit The subreddit to use
     * @return A random submission
     * @throws NetworkException If there was a problem executing the request
     */
    @EndpointImplementation(uris = "GET /random")
    public Submission getRandom(String subreddit) throws NetworkException  {
        String path = "/random.json";
        if (subreddit != null) {
            path = "/r/" + subreddit + path;
        }
        return execute(new RestRequest(GET, path)).as(Submission.class);
    }

    /**
     * Gets the text displayed in the "submit link" form.
     * @param subreddit The subreddit to use
     * @return The text displayed int he "submit link" form
     * @throws NetworkException If there was a problem executing the request
     */
    @EndpointImplementation(uris = "GET /api/submit_text.json")
    public RenderStringPair getSubmitText(String subreddit) throws NetworkException {
        String query = "/api/submit_text.json";
        if (subreddit != null) {
            query = "/r/" + subreddit + query;
        }

        JsonNode node = execute(new RestRequest(GET, query)).getJson();
        return new RenderStringPair(node.get("submit_text").asText(), node.get("submit_text_html").asText());
    }

    /**
     * Gets a list of subreddit names by a topic. For example, the topic "programming" returns "programming", "ProgrammerHumor", etc.
     * @param topic The topic to use
     * @return A list of subreddits related to the given topic
     * @throws NetworkException If there was a problem executing the request
     */
    @EndpointImplementation(uris = "GET /api/subreddits_by_topic.json")
    public List<String> getSubredditsByTopic(String topic) throws NetworkException {
        List<String> subreddits = new ArrayList<>();

        JsonNode node = execute(new RestRequest(GET, "/api/subreddits_by_topic.json", JrawUtils.args("query", topic))).getJson();
        for (JsonNode childNode : node) {
            subreddits.add(childNode.get("name").asText());
        }

        return subreddits;
    }

    /**
     * Gets a list of subreddits that start with the given string. For instance, searching for "fun" would return
     * {@code ["funny", "FunnyandSad", "funnysigns", "funnycharts", ...]}
     * @param start The begging of the subreddit to search for
     * @param includeNsfw Whether to include NSFW subreddits.
     * @return A list of subreddits that starts with the given string
     * @throws NetworkException If there was a problem executing the request
     */
    @EndpointImplementation(uris = "POST /api/search_reddit_names.json")
    public List<String> searchSubreddits(String start, boolean includeNsfw) throws NetworkException {
        List<String> subs = new ArrayList<>();

        JsonNode node = execute(new RestRequest(POST, "/api/search_reddit_names.json", JrawUtils.args(
                "query", start,
                "include_over_18", includeNsfw
        ))).getJson();

        for (JsonNode name : node.get("names")) {
            subs.add(name.asText());
        }

        return subs;
    }

    /**
     * Checks if there was an error returned by a /api/multi/* request, since those URIs return a different error handling
     * format than the rest of the API
     * @param root The root JsonNode
     * @throws ApiException If there is a visible error
     */
    private void checkMultiRedditError(JsonNode root) throws ApiException {
        if (root.has("explanation") && root.has("reason")) {
            throw new ApiException(root.get("reason").asText(), root.get("explanation").asText());
        }
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
}
