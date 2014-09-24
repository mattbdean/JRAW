package net.dean.jraw;

import net.dean.jraw.http.*;
import net.dean.jraw.models.*;
import net.dean.jraw.models.core.Account;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.models.core.Subreddit;
import net.dean.jraw.pagination.Sorting;
import net.dean.jraw.pagination.SubredditPaginator;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
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

import static net.dean.jraw.http.HttpVerb.GET;
import static net.dean.jraw.http.HttpVerb.POST;

/**
 * This class provides access to the most basic Reddit features such as logging in. It is recommended that only one instance
 * of this class is used at a time, unless you disable request management and implement your own.
 */
public class RedditClient extends RestClient<RestRequest, RedditResponse> {

    /** The host that will be used to execute basic HTTP requests */
    public static final String HOST = "www.reddit.com";

    /** The host that will be used to execute secure HTTP requests */
    public static final String HOST_SSL = "ssl.reddit.com";

    /** The name of the header that will be assigned upon a successful login */
    private static final String HEADER_MODHASH = "X-Modhash";

    /** The amount of requests allowed per minute without using OAuth */
    private static final int REQUESTS_PER_MINUTE = 30;

    /** The amount of trending subreddits that will appear in each /r/trendingsubreddits post */
    private static final int NUM_TRENDING_SUBREDDITS = 5;

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
    public RedditResponse initResponse(RestRequest request, HttpResponse response) {
        return new RedditResponse(response, request.getExpected());
    }

    @Override
    public RedditResponse execute(RestRequest request) throws NetworkException {
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
                        JrawUtils.logger().error("Interrupted", e);
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
    @EndpointImplementation(Endpoints.LOGIN)
    public LoggedInAccount login(String username, String password) throws NetworkException, ApiException {
        HttpRequest request = new HttpRequest.Builder(POST, HOST_SSL, "/api/login")
                .args(JrawUtils.args(
                        "user", username,
                        "passwd", password,
                        "api_type", "json"
                )).cookieSpec(HttpHelper.COOKIE_SPEC_REDDIT)
                .build();

        RedditResponse loginResponse = new RedditResponse(http.execute(request));

        if (loginResponse.hasErrors()) {
            throw loginResponse.getApiExceptions()[0];
        }

        //Add the mod has header
        Header h = new BasicHeader(HEADER_MODHASH,
                loginResponse.getJson().get("json").get("data").get("modhash").getTextValue());

        http.addHeader(h);

        return new LoggedInAccount(execute(request(GET, "/api/me.json")).getJson().get("data"), this);
    }

    /**
     * Gets the currently logged in account
     *
     * @return The currently logged in account
     * @throws NetworkException If the user has not been logged in yet
     */
    @EndpointImplementation(Endpoints.ME)
    public Account me() throws NetworkException {
        loginCheck();
        return execute(request(GET, "/api/me.json")).as(Account.class);
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
    @EndpointImplementation(Endpoints.NEEDS_CAPTCHA)
    public boolean needsCaptcha() throws NetworkException {
        try {
            // This endpoint does not return JSON, but rather just "true" or "false"
            RedditResponse response = execute(request(GET, "/api/needs_captcha.json"));
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
    @EndpointImplementation(Endpoints.NEW_CAPTCHA)
    public Captcha getNewCaptcha() throws NetworkException {
        try {
            RedditResponse response = execute(request(POST, "/api/new_captcha"));

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
    @EndpointImplementation(Endpoints.CAPTCHA_IDEN)
    public Captcha getCaptcha(String id) throws NetworkException {
        try {
            CloseableHttpResponse response = http.execute(new HttpRequest.Builder(GET, HOST, "/captcha/" + id + ".png").build());

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
    @EndpointImplementation(Endpoints.USER_USERNAME_ABOUT)
    public Account getUser(String username) throws NetworkException {
        return execute(request(GET, "/user/" + username + "/about.json")).as(Account.class);
    }

    /**
     * Gets a link with a specific ID
     *
     * @param id The link's ID, ex: "92dd8"
     * @return A new Link object
     * @throws NetworkException If the link does not exist or there was a problem making the request
     */
    public Submission getSubmission(String id) throws NetworkException {
        return execute(request(GET, "/" + id + ".json")).as(Submission.class);
    }

    /**
     * Gets a Subreddit
     *
     * @param name The subreddit's name
     * @return A new Subreddit object
     * @throws NetworkException If there was a problem executing the request
     */
    @EndpointImplementation(Endpoints.SUBREDDIT_ABOUT)
    public Subreddit getSubreddit(String name) throws NetworkException {
        return execute(request(GET, "/r/" + name + "/about.json")).as(Subreddit.class);
    }

    /**
     * Checks if a given username is available
     *
     * @param name The username to test
     * @return True if that username is available for registration, false if else
     * @throws NetworkException If there was a problem executing the request
     */
    @EndpointImplementation(Endpoints.USERNAME_AVAILABLE)
    public boolean isUsernameAvailable(String name) throws NetworkException {
        return Boolean.parseBoolean(execute(request(GET, "/api/username_available.json?user=" + name)).getRaw());
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
    @EndpointImplementation(Endpoints.RANDOM)
    public Submission getRandom(String subreddit) throws NetworkException  {
        String path = getSubredditPath(subreddit, "/random.json");
        return execute(request(GET, path)).as(Submission.class);
    }

    /**
     * Gets the text displayed in the "submit link" form.
     * @param subreddit The subreddit to use
     * @return The text displayed int he "submit link" form
     * @throws NetworkException If there was a problem executing the request
     */
    @EndpointImplementation(Endpoints.SUBMIT_TEXT)
    public RenderStringPair getSubmitText(String subreddit) throws NetworkException {
        String path = getSubredditPath(subreddit, "/api/submit_text.json");

        JsonNode node = execute(request(GET, path)).getJson();
        return new RenderStringPair(node.get("submit_text").asText(), node.get("submit_text_html").asText());
    }

    /**
     * Gets a list of subreddit names by a topic. For example, the topic "programming" returns "programming", "ProgrammerHumor", etc.
     * @param topic The topic to use
     * @return A list of subreddits related to the given topic
     * @throws NetworkException If there was a problem executing the request
     */
    @EndpointImplementation(Endpoints.SUBREDDITS_BY_TOPIC)
    public List<String> getSubredditsByTopic(String topic) throws NetworkException {
        List<String> subreddits = new ArrayList<>();

        RestRequest request = requestBuilder(GET, "/api/subreddits_by_topic.json")
                .args("query", topic)
                .build();

        JsonNode node = execute(request).getJson();
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
    @EndpointImplementation(Endpoints.SEARCH_REDDIT_NAMES)
    public List<String> searchSubreddits(String start, boolean includeNsfw) throws NetworkException {
        List<String> subs = new ArrayList<>();

        RestRequest request = requestBuilder(POST, "/api/search_reddit_names.json")
                .args(
                        "query", start,
                        "include_over_18", includeNsfw
                ).build();
        JsonNode node = execute(request).getJson();

        for (JsonNode name : node.get("names")) {
            subs.add(name.asText());
        }

        return subs;
    }

    /**
     * Gets the contents of the CSS file affiliated with a given subreddit (or the front page)
     * @param subreddit The subreddit to use, or null for the front page.
     * @return The content of the raw CSS file
     * @throws NetworkException If there was a problem sending the request, or the {@code Content-Type} header's value was
     *                          not {@code text/css}.
     */
    @EndpointImplementation(Endpoints.STYLESHEET)
    public String getStylesheet(String subreddit) throws NetworkException {
        String path = getSubredditPath(subreddit, "/stylesheet");

        RestRequest request = requestBuilder(GET, path)
                .expectedContentType(ContentType.CSS)
                .build();
        RestResponse response = execute(request);

        ContentType type = response.getContentType();
        if (!type.equals(ContentType.CSS)) {
            throw new NetworkException(String.format("The request did not return a Content-Type of %s (was \"%s\")",
                    ContentType.CSS.asHeader(), type));
        }

        return response.getRaw();
    }

    /**
     * Gets a list of trending subreddits' names. See <a href="http://www.reddit.com/r/trendingsubreddits/">here</a> for more.
     * @return A list of trending subreddits' names
     */
    public List<String> getTrendingSubreddits() {
        SubredditPaginator paginator = new SubredditPaginator(this);
        paginator.setSubreddit("trendingsubreddits");
        paginator.setSorting(Sorting.NEW);

        Submission latest = paginator.next().get(0);
        String title = latest.getTitle();
        String[] parts = title.split(" ");
        List<String> subreddits = new ArrayList<>(NUM_TRENDING_SUBREDDITS);

        for (String part : parts) {
            if (part.startsWith("/r/")) {
                String sub = part.substring("/r/".length());
                // All but the last part will be formatted like "/r/{name},", so remove the commas
                sub = sub.replace(",", "");
                subreddits.add(sub);
            }
        }

        return subreddits;
    }

    /**
     * Gets a list of names of wiki pages for Reddit
     * @return A list of Reddit's wiki pages
     * @throws NetworkException If there was a problem sending the HTTP request
     */
    public List<String> getWikiPages() throws NetworkException {
        return getWikiPages(null);
    }

    /**
     * Gets a list of names of wiki pages for a certain subreddit
     * @param subreddit The subreddit to use
     * @return A list of wiki pages for this subreddit
     * @throws NetworkException If there was a problem sending the HTTP request
     */
    @EndpointImplementation(Endpoints.WIKI_PAGES)
    public List<String> getWikiPages(String subreddit) throws NetworkException {
        String path = getSubredditPath(subreddit, "/wiki/pages.json");

        List<String> pages = new ArrayList<>();
        JsonNode pagesNode = execute(request(GET, path)).getJson().get("data");

        for (JsonNode page : pagesNode) {
            pages.add(page.asText());
        }

        return pages;
    }

    /**
     * Gets a WikiPage that represents one of Reddit's main pages. See <a href="http://www.reddit.com/wiki/pages">here</a>
     * for a list.
     *
     * @param page The page to get
     * @return A WikiPage for the given page
     * @throws NetworkException If there was a problem sending the HTTP request
     *
     * @see #getWikiPages()
     */
    public WikiPage getWikiPage(String page) throws NetworkException {
        return getWikiPage(null, page);
    }

    /**
     * Gets a WikiPage for a certain subreddit
     * @param subreddit The subreddit to use
     * @param page The page to get
     * @return A WikiPage for the given page
     * @throws NetworkException If there was a problem sending the HTTP request
     */
    @EndpointImplementation(Endpoints.WIKI_PAGE)
    public WikiPage getWikiPage(String subreddit, String page) throws NetworkException {
        String path = getSubredditPath(subreddit, "/wiki/" + page + ".json");
        return execute(request(GET, path)).as(WikiPage.class);
    }


    /**
     * Prepends "/r/{subreddit}" to {@code path} if {@code subreddit} is not null
     * @param subreddit The subreddit to use
     * @param path The path to use
     * @return "/r/{subreddit}/{path}" if {@code subreddit} is not null, otherwise "{path}"
     */
    private String getSubredditPath(String subreddit, String path) {
        if (subreddit != null) {
            path = "/r/" + subreddit + path;
        }

        return path;
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
