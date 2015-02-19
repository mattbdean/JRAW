package net.dean.jraw;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.BasicAuthData;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.HttpAdapter;
import net.dean.jraw.http.HttpRequest;
import net.dean.jraw.http.MediaTypes;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.OkHttpAdapter;
import net.dean.jraw.http.RequestBody;
import net.dean.jraw.http.RestClient;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthHelper;
import net.dean.jraw.models.Account;
import net.dean.jraw.models.AccountPreferences;
import net.dean.jraw.models.Award;
import net.dean.jraw.models.Captcha;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.KarmaBreakdown;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.LiveThread;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.models.Thing;
import net.dean.jraw.models.UserRecord;
import net.dean.jraw.models.meta.Model;
import net.dean.jraw.models.meta.SubmissionSerializer;
import net.dean.jraw.paginators.Paginators;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.SubredditPaginator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides a gateway to the services this library provides
 */
public class RedditClient extends RestClient {
    /** The host that will be used to execute OAuth requests */
    public static final String HOST = "oauth.reddit.com";
    /** Used under special circumstances, such as OAuth authentications */
    public static final String HOST_SPECIAL = "www.reddit.com";
    /** The amount of requests allowed per minute when using OAuth2 */
    public static final int REQUESTS_PER_MINUTE = 60;
    /** The amount of trending subreddits that appear in each /r/trendingsubreddits post */
    private static final int NUM_TRENDING_SUBREDDITS = 5;
    /** Name of the 'Authorization' header */
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_RATELIMIT_RESET = "X-Ratelimit-Reset";
    private static final String HEADER_RATELIMIT_REMAINING = "X-Ratelimit-Remaining";
    /** The username of the user who is currently authenticated */
    private String authenticatedUser;
    private boolean adjustRatelimit;

    /** The method of authentication currently being used */
    private AuthenticationMethod authMethod;
    private OAuthData authData;
    private OAuthHelper authHelper;

    /**
     * Instantiates a new RedditClient and adds the given user agent to the default headers
     *
     * @param userAgent The User-Agent header that will be sent with all the HTTP requests.
     */
    public RedditClient(UserAgent userAgent) {
        this(userAgent, REQUESTS_PER_MINUTE);
    }

    /**
     * Instantiates a new RedditClient and adds the given user agent to the default headers
     *
     * @param userAgent The User-Agent header that will be sent with all the HTTP requests.
     * @param requestsPerMinute The upper bound on the amount of requests allowed in one minute
     */
    public RedditClient(UserAgent userAgent, int requestsPerMinute) {
        this(userAgent, requestsPerMinute, new OkHttpAdapter());
    }

    /**
     * Instantiates a new RedditClient and adds the given user agent to the default headers
     *
     * @param userAgent The User-Agent header that will be sent with all the HTTP requests.
     * @param adapter How the client will send HTTP requests
     */
    public RedditClient(UserAgent userAgent, HttpAdapter adapter) {
        this(userAgent, REQUESTS_PER_MINUTE, adapter);
    }

    /**
     * Instantiates a new RedditClient and adds the given user agent to the default headers
     *
     * @param userAgent The User-Agent header that will be sent with all the HTTP requests.
     * @param requestsPerMinute The upper bound on the amount of requests allowed in one minute
     * @param adapter How the client will send HTTP requests
     */
    public RedditClient(UserAgent userAgent, int requestsPerMinute, HttpAdapter adapter) {
        super(adapter, HOST, userAgent, requestsPerMinute);
        this.authMethod = AuthenticationMethod.NOT_YET;
        this.authHelper = new OAuthHelper(this);
        this.adjustRatelimit = true;
        setHttpsDefault(true);
    }

    /**
     * Gets the name of the currently logged in user
     * @return The name of the currently logged in user
     */
    public String getAuthenticatedUser() {
        return authenticatedUser;
    }

    /**
     * Provides this RedditClient with the information to perform OAuth2-related activities. This method
     * <strong>must</strong> be called in order to use the API. All endpoints will return a 403 Forbidden otherwise.
     * @param authData Authentication data. Most commonly obtained from {@link #getOAuthHelper()}.
     * @throws NetworkException Thrown when there was a problem setting the authenticated user. Can only happen when
     *                          the authentication method is not userless.
     */
    public void authenticate(OAuthData authData) throws NetworkException {
        if (authData.getAuthenticationMethod() == null)
            throw new NullPointerException("OAuthData.getAuthenticationMethod() cannot be null");
        this.authMethod = authData.getAuthenticationMethod();
        this.authData = authData;
        httpAdapter.getDefaultHeaders().put(HEADER_AUTHORIZATION, "bearer " + authData.getAccessToken());

        if (!authMethod.isUserless()) {
            this.authenticatedUser = me().getFullName();
        }
    }

    /**
     * Revokes the OAuth2 access token. You will need to login again to continue using this client without error.
     * @param creds The credentials to use. The username and password are irrelevant; only the client ID and secret will
     *              be used.
     * @throws NetworkException If the request was not successful
     */
    public void revokeToken(Credentials creds) throws NetworkException {
        if (!isLoggedIn())
            return;
        execute(request()
                .host(HOST)
                .path("/api/v1/revoke_token")
                .post(JrawUtils.mapOf(
                        "token", authData.getAccessToken()
                )).basicAuth(new BasicAuthData(creds.getClientId(), creds.getClientSecret()))
                .build());

        authData = null;
        httpAdapter.getDefaultHeaders().remove(HEADER_AUTHORIZATION);
        authMethod = AuthenticationMethod.NOT_YET;
    }

    @Override
    public RestResponse execute(HttpRequest request) throws NetworkException {
        RestResponse response = super.execute(request);
        if (adjustRatelimit)
            adjustRatelimit(response);
        return response;
    }

    /** Adjust rate limit dynamically based off of X-Ratelimit-{Remaining,Reset} headers. */
    private void adjustRatelimit(RestResponse response) {
        if (response.getHeaders().get(HEADER_RATELIMIT_RESET) == null ||
                response.getHeaders().get(HEADER_RATELIMIT_REMAINING) == null) {
            // Could not find the necessary headers
            return;
        }
        int reset; // Time in seconds when the ratelimit will reset. Has an integer value
        double remaining; // How many requests are left. Has decimal value
        try {
            reset = Integer.parseInt(response.getHeaders().get(HEADER_RATELIMIT_RESET));
            remaining = Double.parseDouble(response.getHeaders().get(HEADER_RATELIMIT_REMAINING));
        } catch (NumberFormatException e) {
            JrawUtils.logger().warn("Unable to parse ratelimit headers, using default", e);
            // One request per minute for OAuth2, as specified by the docs
            reset = 600;
            remaining = 600.0;
        }

        double resetMinutes = reset / 60.0;
        int requestsPerMinute = (int) Math.floor(remaining / resetMinutes);
        // Prevent an IllegalArgumentException
        if (requestsPerMinute < 1) {
            requestsPerMinute = 1;
        }
        setRatelimit(requestsPerMinute);
    }

    /** Checks whether the ratelimit will be changed based on specific headers returned from Reddit API responses. */
    public boolean isAdjustingRatelimit() {
        return adjustRatelimit;
    }

    /**
     * Sets if the ratelimit should be adjusted based off values provided by Reddit in the form of headers from API
     * responses.
     */
    public void setAdjustRatelimit(boolean flag) {
        this.adjustRatelimit = flag;
    }

    /**
     * Checks if the user is logged in
     * @return True if the user is logged in
     */
    public boolean isLoggedIn() {
        return authMethod != AuthenticationMethod.NOT_YET && authData != null;
    }

    /**
     * Gets the currently logged in account
     *
     * @return The currently logged in account
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.OAUTH_ME)
    public LoggedInAccount me() throws NetworkException {
        RestResponse response = execute(request()
                .endpoint(Endpoints.OAUTH_ME)
                .build());
        // Usually we would use response.as(), but /api/v1/me does not return a "data" or "kind" node.
        return new LoggedInAccount(response.getJson());
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
        // This endpoint does not return JSON, but rather just "true" or "false"
        RestResponse response = execute(request()
                .endpoint(Endpoints.NEEDS_CAPTCHA)
                .get()
                .build());
        return Boolean.parseBoolean(response.getRaw());
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
        RestResponse response = execute(request()
                .endpoint(Endpoints.NEW_CAPTCHA)
                .post(JrawUtils.mapOf(
                        "api_type", "json"
                )).build());

        if (response.hasErrors()) {
            throw response.getError();
        }
        String id = response.getJson().get("json").get("data").get("iden").asText();

        return getCaptcha(id);
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
        HttpRequest request = request()
                .host(HOST_SPECIAL)
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

        CommentSort sort = request.sort;
        if (sort == null)
            // Reddit sorts by confidence by default
            sort = CommentSort.CONFIDENCE;
        args.put("sort", sort.name().toLowerCase());


        RestResponse response = execute(request()
                .path(String.format("/comments/%s", request.id))
                .query(args)
                .build());
        return SubmissionSerializer.withComments(response.getJson(), sort);
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
        String path = JrawUtils.getSubredditPath(subreddit, "/random");

        // Favor path() instead of endpoint() because we have already decided the path above
        RestResponse response = execute(request()
                .path(path)
                .build());

        // We don't really know which sort will be used so just guess Reddit's default
        return SubmissionSerializer.withComments(response.getJson(), CommentSort.CONFIDENCE);
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
        String path = JrawUtils.getSubredditPath(subreddit, "/api/submit_text");

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

        HttpRequest request = request()
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

        HttpRequest request = request()
                .endpoint(Endpoints.SEARCH_REDDIT_NAMES)
                .post(JrawUtils.mapOf(
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

        HttpRequest r = request()
                .path(path)
                .expected(MediaTypes.CSS.type())
                .build();
        RestResponse response = execute(r);

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
     * Gets a list of similar subreddits based on the given ones.
     *
     * @param subreddits A list of subreddit names to act as the seed
     * @param omit An array of subreddits to explicitly avoid. These will not appear in the result.
     * @return A list of similar subreddit names
     * @throws NetworkException If the request was not successful.
     */
    @EndpointImplementation(Endpoints.RECOMMEND_SR_SRNAMES)
    public List<String> getRecommendations(List<String> subreddits, List<String> omit) throws NetworkException {
        if (subreddits.isEmpty()) {
            return new ArrayList<>();
        }

        RestResponse response = execute(request()
                .get()
                .endpoint(Endpoints.RECOMMEND_SR_SRNAMES, JrawUtils.join(subreddits))
                .query("omit", omit != null ? JrawUtils.join(omit) : "")
                .build());
        JsonNode json = JrawUtils.fromString(response.getRaw());
        List<String> recommendations = new ArrayList<>();
        for (JsonNode node : json) {
            recommendations.add(node.get("sr_name").asText());
        }

        return recommendations;
    }

    /**
     * Gets a Listing of the given full names. Only submissions, comments, and subreddits will be returned
     * @param fullNames A list of full names
     * @return A Listing of Things
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.INFO)
    public Listing<Thing> get(String... fullNames) throws NetworkException {
        for (String name : fullNames) {
            if (!(name.startsWith(Model.Kind.LINK.getValue()) ||
                    name.startsWith(Model.Kind.COMMENT.getValue()) ||
                    name.startsWith(Model.Kind.SUBREDDIT.getValue()))) {
                // Send the data, but warn the developer
                JrawUtils.logger().warn("Name '{}' is not a submission, comment, or subreddit", name);
            }
        }
        return execute(request()
                .endpoint(Endpoints.INFO)
                .query("id", JrawUtils.join(fullNames))
                .build()).asListing(Thing.class);
    }

    /**
     * Gets the preferences for this account
     * @param names The specific names of the desired preferences. These can be found
     *              <a href="https://www.reddit.com/dev/api#GET_api_v1_me_prefs">here</a>.
     * @return An AccountPreferences that represent this account's preferences
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.OAUTH_ME_PREFS_GET)
    public AccountPreferences getPreferences(String... names) throws NetworkException {
        Map<String, String> query = new HashMap<>();
        if (names.length > 0) {
            query.put("fields", JrawUtils.join(',', names));
        }

        RestResponse response = execute(request()
                .endpoint(Endpoints.OAUTH_ME_PREFS_GET)
                .query(query)
                .build());
        return new AccountPreferences(response.getJson());
    }

    /**
     * Updates the preferences for this account
     * @param prefs The preferences
     * @return The preferences after they were updated
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.OAUTH_ME_PREFS_PATCH)
    public AccountPreferences updatePreferences(AccountPreferencesEditor prefs) throws NetworkException {
        RestResponse response = execute(request()
                .endpoint(Endpoints.OAUTH_ME_PREFS_PATCH)
                .patch(RequestBody.create(MediaTypes.JSON.type(), JrawUtils.toJson(prefs.getArgs())))
                .build());
        return new AccountPreferences(response.getJson());
    }

    /**
     * Gets the trophies for the currently authenticated user
     * @throws NetworkException If the request was not successful
     */
    public List<Award> getTrophies() throws NetworkException {
        return getTrophies(null);
    }

    /**
     * Gets the trophies for a specific user
     * @param username The username to find the trophies for
     * @throws NetworkException If the request was not successful
     * @return A list of awards
     */
    @EndpointImplementation({
            Endpoints.OAUTH_ME_TROPHIES,
            Endpoints.OAUTH_USER_USERNAME_TROPHIES
    })
    public List<Award> getTrophies(String username) throws NetworkException {
        if (username == null || username.isEmpty()) {
            if (authMethod == AuthenticationMethod.NOT_YET) {
                throw new IllegalArgumentException("No username given and not logged in");
            }

            username = authenticatedUser;
        }

        RestResponse response = execute(request()
                .endpoint(Endpoints.OAUTH_USER_USERNAME_TROPHIES, username)
                .build());

        List<Award> awards = new ArrayList<>();
        for (JsonNode awardNode : response.getJson().get("data").get("trophies")) {
            awards.add(new Award(awardNode.get("data")));
        }

        return awards;
    }

    /**
     * Gets a breakdown of link and comment karma by subreddit
     * @return A KarmaBreakdown for this account
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.OAUTH_ME_KARMA)
    public KarmaBreakdown getKarmaBreakdown() throws NetworkException {
        RestResponse response = execute(request()
                .endpoint(Endpoints.OAUTH_ME_KARMA)
                .build());
        return new KarmaBreakdown(response.getJson().get("data"));
    }

    /**
     * Removes a friend
     * @param friend The username of the friend
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.OAUTH_ME_FRIENDS_USERNAME_DELETE)
    public void deleteFriend(String friend) throws NetworkException {
        execute(request()
                .delete()
                .endpoint(Endpoints.OAUTH_ME_FRIENDS_USERNAME_DELETE, friend)
                .build());
    }

    /**
     * Gets a user record pertaining to a particular relationship
     * @param name The name of the user
     * @return A UserRecord representing the relationship
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.OAUTH_ME_FRIENDS_USERNAME_GET)
    public UserRecord getFriend(String name) throws NetworkException {
        RestResponse response = execute(request()
                .endpoint(Endpoints.OAUTH_ME_FRIENDS_USERNAME_GET, name)
                .build());
        return new UserRecord(response.getJson());
    }

    /**
     * Adds of updates a friend
     * @param name The name of the user
     * @throws NetworkException If the request was not successful
     * @return A UserRecord representing the new or updated relationship
     */
    @EndpointImplementation(Endpoints.OAUTH_ME_FRIENDS_USERNAME_PUT)
    public UserRecord updateFriend(String name) throws NetworkException {
        RestResponse response = execute(request()
                .put(RequestBody.create(MediaTypes.JSON.type(), JrawUtils.toJson(new FriendModel(name))))
                .endpoint(Endpoints.OAUTH_ME_FRIENDS_USERNAME_PUT, name)
                .build());
        return new UserRecord(response.getJson());
    }

    /**
     * Gets the object that will help clients authenticate users with their Reddit app
     */
    public OAuthHelper getOAuthHelper() {
        return authHelper;
    }

    /**
     * Gets the data that shows that this client has been authenticated
     */
    public OAuthData getAuthData() {
        return authData;
    }

    /**
     * Checks if the given endpoint is applicable for the current OAuth scopes
     */
    public boolean isAuthorizedFor(Endpoints endpoint) {
        if (authData == null) return false;

        for (String scope : authData.getScopes()) {
            if (scope.equalsIgnoreCase(endpoint.getScope())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if this RedditClient has an active session in which a user is authenticated. This will return true for
     * "normal" authentication, in which a user must login with their username/password to use this app, or false if
     * this client hasn't been authenticated yet or if using user-less (application only) OAuth.
     *
     * @return If there is an authenticated user
     */
    public boolean hasActiveUserContext() {
        return authMethod != null && !authMethod.isUserless();
    }

    private static final class FriendModel {
        private final String name;

        private FriendModel(String name) {
            this.name = name == null ? "" : name;
        }

        public String getName() {
            return name;
        }
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
         * @param commentId The ID of the comment to focus on. For example: "c0b6xx0".
         * @return This SubmissionRequest
         */
        public SubmissionRequest focus(String commentId) {
            this.focus = commentId;
            return this;
        }
    }
}
