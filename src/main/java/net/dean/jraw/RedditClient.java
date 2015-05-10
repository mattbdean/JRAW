package net.dean.jraw;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.HttpAdapter;
import net.dean.jraw.http.HttpRequest;
import net.dean.jraw.http.MediaTypes;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.OkHttpAdapter;
import net.dean.jraw.http.RestClient;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.http.SubmissionRequest;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.InvalidScopeException;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthHelper;
import net.dean.jraw.models.Account;
import net.dean.jraw.models.Award;
import net.dean.jraw.models.Captcha;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.models.Thing;
import net.dean.jraw.models.meta.Model;
import net.dean.jraw.models.meta.SubmissionSerializer;
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

    /** The default amount of times a request will be retried if a server-side error is encountered. */
    public static final int DEFAULT_RETRY_LIMIT = 5;
    /** The amount of trending subreddits that appear in each /r/trendingsubreddits post */
    private static final int NUM_TRENDING_SUBREDDITS = 5;
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_RATELIMIT_RESET = "X-Ratelimit-Reset";
    private static final String HEADER_RATELIMIT_REMAINING = "X-Ratelimit-Remaining";
    /** The username of the user who is currently authenticated */
    private String authenticatedUser;
    private boolean adjustRatelimit;
    private int retryLimit;

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
        this(userAgent, new OkHttpAdapter());
    }

    /**
     * Instantiates a new RedditClient and adds the given user agent to the default headers
     *
     * @param userAgent The User-Agent header that will be sent with all the HTTP requests.
     * @param adapter How the client will send HTTP requests
     */
    public RedditClient(UserAgent userAgent, HttpAdapter<?> adapter) {
        super(adapter, HOST, userAgent, REQUESTS_PER_MINUTE);
        this.authMethod = AuthenticationMethod.NOT_YET;
        this.authHelper = new OAuthHelper(this);
        this.adjustRatelimit = true;
        this.retryLimit = DEFAULT_RETRY_LIMIT;
        setHttpsDefault(true);
    }

    /**
     * Gets the name of the currently logged in user. Will be null if the "identity" scope was not included or when
     * using application-only authentication.
     *
     * @throws IllegalStateException If this client has not been authenticated or if this client used application-only
     *                               OAuth to authenticated.
     */
    public String getAuthenticatedUser() {
        if (!(isAuthenticated() && hasActiveUserContext()))
            throw new IllegalStateException("Not authenticated or no active user context");
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
        if (authHelper.getAuthStatus() != OAuthHelper.AuthStatus.AUTHORIZED)
            throw new IllegalStateException("OAuthHelper says it is not authorized");
        if (authData.getAuthenticationMethod() == null ||
                authData.getScopes() == null ||
                authData.getAccessToken() == null ||
                authData.getExpirationDate() == null)
            throw new NullPointerException("Missing important data from OAuth JSON: " + authData.getDataNode());

        this.authMethod = authData.getAuthenticationMethod();
        this.authData = authData;
        httpAdapter.getDefaultHeaders().put(HEADER_AUTHORIZATION, "bearer " + authData.getAccessToken());

        if (!authMethod.isUserless() && isAuthorizedFor(Endpoints.OAUTH_ME)) {
            this.authenticatedUser = me().getFullName();
        }
    }

    /**
     * Removes any authentication data. The access token needs to be revoked first using
     * {@link OAuthHelper#revokeAccessToken(Credentials)}.
     */
    public void deauthenticate() {
        if (authHelper.getAuthStatus() != OAuthHelper.AuthStatus.REVOKED)
            throw new IllegalArgumentException("Revoke the access token first");
        authData = null;
        httpAdapter.getDefaultHeaders().remove(HEADER_AUTHORIZATION);
        authMethod = AuthenticationMethod.NOT_YET;
    }

    @Override
    public RestResponse execute(HttpRequest request) throws NetworkException, InvalidScopeException {
        return execute(request, 0);
    }

    private RestResponse execute(HttpRequest request, int retryCount) throws NetworkException, InvalidScopeException {
        RestResponse response;
        try {
            response = super.execute(request);
        } catch (NetworkException e) {
            RestResponse errorResponse = e.getResponse();
            final int code = errorResponse.getStatusCode();
            if (code == 403 && errorResponse.getHeaders().get("WWW-Authenticate") != null) {
                // Invalid scope
                throw new InvalidScopeException(errorResponse.getOrigin().getUrl());
            } else if (code >= 500 && code < 600) {
                // Server-side error, retry
                if (retryCount++ > retryLimit) {
                    throw new IllegalStateException("Reached retry limit", e);
                }
                return execute(request, retryCount);
            }
            throw e;
        }

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

    /** Gets the amount of times a request will be retried if a server-side error is encountered. */
    public int getRetryLimit() {
        return retryLimit;
    }

    /**
     * Sets the amount of times a request will be retried if a server-side error is encountered. A negative value is not
     * accepted.
     *
     * @see #DEFAULT_RETRY_LIMIT
     */
    public void setRetryLimit(int retryLimit) {
        if (retryLimit < 0)
            throw new IllegalArgumentException("Limit cannot be less than 0");
        this.retryLimit = retryLimit;
    }

    /** Checks if this RedditClient is current authenticated. */
    public boolean isAuthenticated() {
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
        return new Captcha(id);
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
        if (request.getDepth() != null)
            args.put("depth", Integer.toString(request.getDepth()));
        if (request.getContext() != null)
            args.put("context", Integer.toString(request.getContext()));
        if (request.getLimit() != null)
            args.put("limit", Integer.toString(request.getLimit()));
        if (request.getFocus() != null && !JrawUtils.isFullname(request.getFocus()))
            args.put("comment", request.getFocus());

        CommentSort sort = request.getSort();
        if (sort == null)
            // Reddit sorts by confidence by default
            sort = CommentSort.CONFIDENCE;
        args.put("sort", sort.name().toLowerCase());


        RestResponse response = execute(request()
                .path(String.format("/comments/%s", request.getId()))
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
     * Gets the contents of the CSS file affiliated with a given subreddit
     * @param subreddit The name of the subreddit whose stylesheet will be fetched. Must not be null.
     * @return The content of the raw CSS file
     * @throws NetworkException If the request was not successful or the Content-Type header was not {@code text/css}.
     */
    @EndpointImplementation(Endpoints.STYLESHEET)
    public String getStylesheet(String subreddit) throws NetworkException {
        if (subreddit == null) throw new NullPointerException("subreddit cannot be null");
        String path = subreddit + "/stylesheet";

        HttpRequest r = request()
                .path(path)
                .expected(MediaTypes.CSS.type())
                .build();
        RestResponse response = execute(r);

        return response.getRaw();
    }

    /**
     * Gets a list of trending subreddits' names. See <a href="http://www.reddit.com/r/trendingsubreddits/">here</a> for more.
     * @return A list of trending subreddits' names
     */
    public List<String> getTrendingSubreddits() {
        SubredditPaginator paginator = new SubredditPaginator(this, "trendingsubreddits");
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
     * Gets a Listing of the given fullnames. Only submissions, comments, and subreddits will be returned
     * @param fullNames A list of fullnames
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
        if (username == null)
            assertNotUserless();
        username = authenticatedUser;

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
     * Gets the object that will help clients authenticate users with their Reddit app
     */
    public OAuthHelper getOAuthHelper() {
        return authHelper;
    }

    /**
     * Gets the data that shows that this client has been authenticated
     */
    public OAuthData getOAuthData() {
        return authData;
    }

    /**
     * Checks if the given endpoint is applicable for the current OAuth scopes
     */
    public boolean isAuthorizedFor(Endpoints endpoint) {
        if (authData == null) return false;
        if (authData.getScopes().length > 0 && authData.getScopes()[0].equals("*")) {
            // All scopes
            return true;
        }

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

    private void assertNotUserless() {
        if (!hasActiveUserContext()) {
            throw new IllegalArgumentException("Not applicable for application-only authentication");
        }
    }

    /** Returns how the user was authenticated */
    public AuthenticationMethod getAuthenticationMethod() {
        return authMethod;
    }
}
