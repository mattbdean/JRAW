package net.dean.jraw;

import com.squareup.okhttp.Response;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.Credentials;
import net.dean.jraw.http.MediaTypes;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.http.RestClient;
import net.dean.jraw.http.RestRequest;
import net.dean.jraw.models.Account;
import net.dean.jraw.models.Captcha;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.LiveThread;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.More;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.models.Thing;
import net.dean.jraw.models.meta.Model;
import net.dean.jraw.paginators.Paginators;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides a gateway to the services this library provides
 */
public class RedditClient extends RestClient<RedditResponse> {

    /** The host that will be used to execute most HTTP(S) requests */
    public static final String HOST = "www.reddit.com";

    /** The host that will be used to execute OAuth requests */
    public static final String HOST_OAUTH = "oauth.reddit.com";

    /** The host that will be used for OAuth authorizations, and preferences */
    public static final String HOST_SPECIAL = "ssl.reddit.com";

    /** The name of the header that will be assigned upon a successful standard login */
    private static final String HEADER_MODHASH = "X-Modhash";

    /** The amount of requests allowed per minute without using OAuth2 */
    public static final int REQUESTS_PER_MINUTE = 30;

    /** The amount of requests allowed per minute when using OAuth2 */
    public static final int REQUESTS_PER_MINUTE_OAUTH2 = 60;

    /** The amount of trending subreddits that will appear in each /r/trendingsubreddits post */
    private static final int NUM_TRENDING_SUBREDDITS = 5;

    /** The username of the user who is currently authenticated */
    protected String authenticatedUser;

    /** The method of authentication currently being used */
    protected AuthenticationMethod authMethod;

    /**
     * Instantiates a new RedditClient and adds the given user agent to the default headers
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
     * @param requestsPerMinute The amount of requests per minute to send
     */
    public RedditClient(String userAgent, int requestsPerMinute) {
        super(HOST, userAgent, requestsPerMinute);
        this.authMethod = AuthenticationMethod.NONE;
    }

    /**
     * Instantiates a new RedditClient and adds the given user agent to the default headers
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
        this(userAgent, REQUESTS_PER_MINUTE);
    }

    @Override
    protected RedditResponse initResponse(Response response) {
        return new RedditResponse(response);
    }

    /**
     * Gets the name of the currently logged in user
     * @return The name of the currently logged in user
     */
    public String getAuthenticatedUser() {
        return authenticatedUser;
    }

    /**
     * Logs in to an account and returns the data associated with it
     *
     * @param credentials The credentials to use to log in
     * @return An Account object that has the same username as the username parameter
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the API returned an error (most likely because of an incorrect password)
     */
    @EndpointImplementation(Endpoints.LOGIN)
    public LoggedInAccount login(Credentials credentials) throws NetworkException, ApiException {
        RestRequest request = request()
                .https(true) // Always HTTPS
                .endpoint(Endpoints.LOGIN)
                .post(JrawUtils.args(
                        "user", credentials.getUsername(),
                        "passwd", credentials.getPassword(),
                        "api_type", "json"
                )).sensitiveArgs("passwd")
                .build();

        RedditResponse loginResponse = execute(request);

        if (loginResponse.hasErrors()) {
            throw loginResponse.getErrors()[0];
        }

        setHttpsDefault(loginResponse.getJson().get("json").get("data").get("need_https").asBoolean());

        String modhash = loginResponse.getJson().get("json").get("data").get("modhash").getTextValue();

        // Add the X-Modhash header, or update it if it already exists
        defaultHeaders.put(HEADER_MODHASH, modhash);

        LoggedInAccount me = me();
        this.authenticatedUser = me.getFullName();
        this.authMethod = AuthenticationMethod.STANDARD;
        return me;
    }

    /**
     * Logs out of Reddit.
     * @throws NetworkException If the request was not successful
     */
    public void logout() throws NetworkException {
        execute(request()
                .path("/logout")
                .expected(MediaTypes.HTML.type())
                .post(null).build());
        defaultHeaders.remove(HEADER_MODHASH);
        authMethod = AuthenticationMethod.NONE;
    }

    /**
     * Registers a new account
     * @param username The username
     * @param password The password
     * @param email The email to use. Can be null.
     * @param captcha The captcha being answered
     * @param captchaAttempt The attempt at the captcha
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the API returned an error
     * @return The account that was just created
     */
    @EndpointImplementation(Endpoints.REGISTER)
    public LoggedInAccount register(String username, String password, String email, Captcha captcha, String captchaAttempt) throws NetworkException, ApiException {
        Map<String, String> args = JrawUtils.args(
                "api_type", "json",
                "captcha", captchaAttempt,
                "iden", captcha.getId(),
                "passwd", password,
                "passwd2", password,
                "user", username
        );
        if (email != null && !email.isEmpty()) {
            args.put("email", email);
        }
        RedditResponse response = execute(request()
                .https(true)
                .host(HOST)
                .endpoint(Endpoints.REGISTER)
                .post(args)
                .sensitiveArgs("passwd", "passwd2")
                .build());
        if (response.hasErrors()) {
            throw response.getErrors()[0];
        }

        setHttpsDefault(response.getJson().get("json").get("data").get("need_https").asBoolean());

        String modhash = response.getJson().get("json").get("data").get("modhash").getTextValue();

        // Add the X-Modhash header, or update it if it already exists
        defaultHeaders.put(HEADER_MODHASH, modhash);

        LoggedInAccount me = me();
        this.authenticatedUser = me.getFullName();
        this.authMethod = AuthenticationMethod.STANDARD;
        return me;
    }

    /**
     * Checks if the user is logged in
     * @return True if the user is logged in
     */
    public boolean isLoggedIn() {
        return authMethod != AuthenticationMethod.NONE;
    }

    /**
     * Gets the currently logged in account
     *
     * @return The currently logged in account
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.ME)
    public LoggedInAccount me() throws NetworkException {
        RedditResponse response = execute(request()
                .endpoint(Endpoints.ME)
                .build());
        return new LoggedInAccount(response.getJson().get("data"));
    }

    /**
     * Checks if the current user needs a captcha to do specific actions such as submit links and compose private
     * messages. This will always be true if there is no logged in user. Usually, this method will return {@code true}
     * if the current logged in user has more than 10 link karma
     *
     * @return True if the user needs a captcha to do a specific action, else if not or not logged in.
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.NEEDS_CAPTCHA)
    public boolean needsCaptcha() throws NetworkException {
        try {
            // This endpoint does not return JSON, but rather just "true" or "false"
            RedditResponse response = execute(request()
                    .endpoint(Endpoints.NEEDS_CAPTCHA)
                    .get()
                    .build());
            return Boolean.parseBoolean(response.getRaw());
        } catch (NetworkException e) {
            throw new NetworkException("Unable to make the request to /api/needs_captcha.json", e);
        }
    }

    /**
     * Fetches a new captcha from the API
     *
     * @return A new Captcha
     * @throws NetworkException If the request was not successful
     * @throws ApiException If the Reddit API returned an error
     */
    @EndpointImplementation(Endpoints.NEW_CAPTCHA)
    public Captcha getNewCaptcha() throws NetworkException, ApiException {
        try {
            RedditResponse response = execute(request()
                    .endpoint(Endpoints.NEW_CAPTCHA)
                    .post(JrawUtils.args(
                            "api_type", "json"
                    )).build());

            if (response.hasErrors()) {
                throw response.getErrors()[0];
            }
            String id = response.getJson().get("json").get("data").get("iden").asText();

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
     */
    @EndpointImplementation(Endpoints.CAPTCHA_IDEN)
    public Captcha getCaptcha(String id) {
        // Use Request to format the URL
        RestRequest request = request()
                .endpoint(Endpoints.CAPTCHA_IDEN, id)
                .get()
                .build();

        return new Captcha(id, request.getUrl());
    }

    /**
     * Gets a user with a specific username
     *
     * @param username The name of the desired user
     * @return An Account whose name matches the given username
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.USER_USERNAME_ABOUT)
    public Account getUser(String username) throws NetworkException {
        return execute(request()
                .endpoint(Endpoints.USER_USERNAME_ABOUT, username)
                .get()
                .build()).as(Account.class);
    }

    /**
     * Gets a link with a specific ID
     *
     * @param id The link's ID, ex: "92dd8"
     * @return A new Link object
     * @throws NetworkException If the request was not successful
     */
    public Submission getSubmission(String id) throws NetworkException {
        return getSubmission(new SubmissionRequest(id));
    }

    @EndpointImplementation(Endpoints.COMMENTS_ARTICLE)
    public Submission getSubmission(SubmissionRequest request) throws NetworkException {
        Map<String, String> args = new HashMap<>();
        if (request.depth != null)
            args.put("depth", Integer.toString(request.depth));
        if (request.context != null)
            args.put("context", Integer.toString(request.context));
        if (request.limit != null)
            args.put("limit", Integer.toString(request.limit));
        if (request.focus != null && !JrawUtils.isFullName(request.focus))
            args.put("comment", request.focus);
        if (request.sort != null)
            args.put("sort", request.sort.name().toLowerCase());

        return execute(request()
                .path(String.format("/comments/%s.json", request.id))
                .query(args)
                .build()).as(Submission.class);

    }

    /**
     * Gets a Subreddit
     *
     * @param name The subreddit's name
     * @return A new Subreddit object
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.SUBREDDIT_ABOUT)
    public Subreddit getSubreddit(String name) throws NetworkException {
        return execute(request()
                .endpoint(Endpoints.SUBREDDIT_ABOUT, name)
                .build()).as(Subreddit.class);
    }

    /**
     * Checks if a given username is available
     *
     * @param name The username to test
     * @return True if that username is available for registration, false if else
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.USERNAME_AVAILABLE)
    public boolean isUsernameAvailable(String name) throws NetworkException {
        RedditResponse response = execute(request()
                .endpoint(Endpoints.USERNAME_AVAILABLE)
                .query("user", name)
                .build());

        return Boolean.parseBoolean(response.getRaw());
    }

    /**
     * Gets a random submission
     * @return A random submission
     * @throws NetworkException If the request was not successful
     */
    public Submission getRandomSubmission() throws NetworkException {
        return getRandomSubmission(null);
    }

    /**
     * Gets a random submission from a specific subreddit
     * @param subreddit The subreddit to use
     * @return A random submission
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.RANDOM)
    public Submission getRandomSubmission(String subreddit) throws NetworkException  {
        String path = JrawUtils.getSubredditPath(subreddit, "/random.json");

        // Favor path() instead of endpoint() because we have already decided the path above
        return execute(request()
                .path(path)
                .build()).as(Submission.class);
    }

    /**
     * Gets a random subreddit
     * @return A random subreddit
     * @throws NetworkException If the request was not successful
     */
    public Subreddit getRandomSubreddit() throws NetworkException {
        return getSubreddit("random");
    }

    /**
     * Gets the text displayed in the "submit link" form.
     * @param subreddit The subreddit to use
     * @return The text displayed int he "submit link" form
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.SUBMIT_TEXT)
    public String getSubmitText(String subreddit) throws NetworkException {
        String path = JrawUtils.getSubredditPath(subreddit, "/api/submit_text.json");

        JsonNode node = execute(request()
                .path(path)
                .build()).getJson();
        return node.get("submit_text").asText();
    }

    /**
     * Gets a list of subreddit names by a topic. For example, the topic "programming" returns "programming",
     * "ProgrammerHumor", etc.
     *
     * @param topic The topic to use
     * @return A list of subreddits related to the given topic
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.SUBREDDITS_BY_TOPIC)
    public List<String> getSubredditsByTopic(String topic) throws NetworkException {
        List<String> subreddits = new ArrayList<>();

        RestRequest request = request()
                .endpoint(Endpoints.SUBREDDITS_BY_TOPIC)
                .query("query", topic)
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
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.SEARCH_REDDIT_NAMES)
    public List<String> searchSubreddits(String start, boolean includeNsfw) throws NetworkException {
        List<String> subs = new ArrayList<>();

        RestRequest request = request()
                .endpoint(Endpoints.SEARCH_REDDIT_NAMES)
                .post(JrawUtils.args(
                        "query", start,
                        "include_over_18", includeNsfw
                )).build();
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
     * @throws NetworkException If the request was not successful or the Content-Type header was not {@code text/css}.
     */
    @EndpointImplementation(Endpoints.STYLESHEET)
    public String getStylesheet(String subreddit) throws NetworkException {
        String path = JrawUtils.getSubredditPath(subreddit, "/stylesheet");

        RestRequest r = request()
                .path(path)
                .expected(MediaTypes.CSS.type())
                .build();
        RedditResponse response = execute(r);

        return response.getRaw();
    }

    /**
     * Gets a live thread by ID
     * @param id The thread's ID
     * @return Information about a LiveThread with the given ID
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.LIVE_THREAD_ABOUT)
    public LiveThread getLiveThread(String id) throws NetworkException {
        return execute(request()
                .endpoint(Endpoints.LIVE_THREAD_ABOUT, id)
                .build()).as(LiveThread.class);
    }

    /**
     * Gets a list of trending subreddits' names. See <a href="http://www.reddit.com/r/trendingsubreddits/">here</a> for more.
     * @return A list of trending subreddits' names
     */
    public List<String> getTrendingSubreddits() {
        SubredditPaginator paginator = Paginators.subreddit(this, "trendingsubreddits");
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
     * Retrieves more comments from the comment tree. Note that the replies are flat, as they do not have a 'replies'
     * key. The resulting list will also include More objects.
     *
     * @param submission The submission where the desired 'more' object is found
     * @param sort How to sort the returned comments
     * @param more The More object to retrieve the children of
     * @return A list of CompactComments that the More object represents
     * @throws NetworkException
     * @throws ApiException
     */
    @EndpointImplementation(Endpoints.MORECHILDREN)
    public List<Thing> getMoreComments(Submission submission, CommentSort sort, More more)
            throws NetworkException, ApiException {
        // TODO: Map the comments into a tree
        List<String> moreIds = more.getChildrenIds();
        StringBuilder ids = new StringBuilder(moreIds.get(0));
        for (int i = 1; i < moreIds.size(); i++) {
            String other = moreIds.get(i);
            ids.append(',').append(other);
        }

        // POST with a body could be used instead of GET with a query to avoid an unnecessarily long URL, but Reddit
        // seems to handle it fine.
        RedditResponse response = execute(request()
                .path(Endpoints.MORECHILDREN.getEndpoint().getUri() + ".json")
                .query(JrawUtils.args(
                        "children", ids.toString(),
                        "link_id", submission.getFullName(),
                        "sort", sort.name().toLowerCase(),
                        "api_type", "json"
                )).build());
        if (response.hasErrors()) {
            throw response.getErrors()[0];
        }

        JsonNode things = response.getJson().get("json").get("data").get("things");
        List<Thing> commentList = new ArrayList<>(things.size());
        for (JsonNode node : things) {
            String kind = node.get("kind").asText();
            JsonNode data = node.get("data");
            if (node.get("kind").asText().equals(Model.Kind.COMMENT.getValue())) {
                commentList.add(new Comment(data));
            } else if (node.get("kind").asText().equals(Model.Kind.MORE.getValue())) {
                commentList.add(new More(data));
            } else {
                throw new IllegalArgumentException(String.format("Illegal data type: %s. Expecting %s or %s",
                        kind, Model.Kind.COMMENT, Model.Kind.MORE));
            }
        }

        return commentList;
    }

    /**
     * Returns how the user was authenticated
     * @return How the user was authenticated
     */
    public AuthenticationMethod getAuthenticationMethod() {
        return authMethod;
    }

    /**
     * This class is used by {@link #getSubmission(net.dean.jraw.RedditClient.SubmissionRequest)} to specify the
     * parameters of the request.
     */
    public static class SubmissionRequest {
        private final String id;
        private Integer depth;
        private Integer limit;
        private Integer context;
        private CommentSort sort;
        private String focus;

        /**
         * Instantiates a new SubmissionRequeslt
         * @param id The link's ID, ex: "92dd8"
         */
        public SubmissionRequest(String id) {
            this.id = id;
        }

        /**
         * Sets the maximum amount of subtrees returned by this request. If the number is less than 1, it is ignored by
         * the Reddit API and no depth restriction is enacted.
         * @param depth The depth
         * @return This SubmissionRequest
         */
        public SubmissionRequest depth(Integer depth) {
            this.depth = depth;
            return this;
        }

        /**
         * Sets the maximum amount of comments to return
         * @param limit The limit
         * @return This SubmissionRequest
         */
        public SubmissionRequest limit(Integer limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Sets the number of parents shown in relation to the focused comment. For example, if the focused comment is
         * in the eighth level of the comment tree (meaning there are seven replies above it), and the context is set to
         * six, then the response will also contain the six direct parents of the given comment. For a better
         * understanding, play with
         * <a href="https://www.reddit.com/comments/92dd8?comment=c0b73aj&context=8">this link</a>.
         *
         * @param context The number of parent comments to return in relation to the focused comment.
         * @return This SubmissionRequest
         */
        public SubmissionRequest context(Integer context) {
            this.context = context;
            return this;
        }

        /**
         * Sets the sorting for the comments in the response
         * @param sort The sorting
         * @return This SubmissionRequest
         */
        public SubmissionRequest sort(CommentSort sort) {
            this.sort = sort;
            return this;
        }

        /**
         * Sets the ID of the comment to focus on. If this comment does not exist, then this parameter is ignored.
         * Otherwise, only one comment tree is returned: the one in which the given comment resides.
         *
         * @param focus The ID of the comment to focus on. For example: "c0b6xx0".
         * @return This SubmissionRequest
         */
        public SubmissionRequest focus(String focus) {
            this.focus = focus;
            return this;
        }
    }

}
