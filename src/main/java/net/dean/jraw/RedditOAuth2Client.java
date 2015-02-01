package net.dean.jraw;

import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.BasicAuthData;
import net.dean.jraw.http.Credentials;
import net.dean.jraw.http.HttpAdapter;
import net.dean.jraw.http.HttpRequest;
import net.dean.jraw.http.MediaTypes;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.OkHttpAdapter;
import net.dean.jraw.http.RequestBody;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthHelper;
import net.dean.jraw.models.AccountPreferences;
import net.dean.jraw.models.Award;
import net.dean.jraw.models.Captcha;
import net.dean.jraw.models.KarmaBreakdown;
import net.dean.jraw.models.LoggedInAccount;
import net.dean.jraw.models.UserRecord;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides a way to interact with the Reddit API using OAuth2. Before the program has exited, it is
 * recommended to revoke the access token (see {@link #revokeToken(Credentials)}).
 */
public class RedditOAuth2Client extends RedditClient {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private OAuthData authData;
    private OAuthHelper authHelper;

    /**
     * Instantiates a new OAuth2RedditClient with the default HttpAdapter ({@link OkHttpAdapter})
     *
     * @param userAgent The User-Agent header that will be sent with all the HTTP requests.
     *                  <blockquote>
     *                  Change your client's User-Agent string to something unique and descriptive, preferably
     *                  referencing your reddit username. From the
     *                  <a href="https://github.com/reddit/reddit/wiki/API">Reddit Wiki on GitHub</a>:
     *
     *                  <ul>
     *                    <li>Many default User-Agents (like "Python/urllib" or "Java") are drastically limited to
     *                        encourage unique and descriptive user-agent strings.
     *                    <li>If you're making an application for others to use, please include a version number in
     *                        the user agent. This allows us to block buggy versions without blocking all versions of
     *                        your app.
     *                    <li>NEVER lie about your user-agent. This includes spoofing popular browsers and spoofing
     *                        other bots. We will ban liars with extreme prejudice.</li>
     *                  </ul>
     *                  </blockquote>
     */
    public RedditOAuth2Client(String userAgent) {
        super(userAgent, REQUESTS_PER_MINUTE_OAUTH2);
        this.authHelper = new OAuthHelper(this);
        setHttpsDefault(true);
    }

    /**
     * Instantiates a new OAuth2RedditClient with a custom HttpAdapter
     * @param userAgent The User-Agent header that will be sent with all the HTTP requests.
     *                  <blockquote>
     *                  Change your client's User-Agent string to something unique and descriptive, preferably
     *                  referencing your reddit username. From the
     *                  <a href="https://github.com/reddit/reddit/wiki/API">Reddit Wiki on GitHub</a>:
     *
     *                  <ul>
     *                    <li>Many default User-Agents (like "Python/urllib" or "Java") are drastically limited to
     *                        encourage unique and descriptive user-agent strings.
     *                    <li>If you're making an application for others to use, please include a version number in
     *                        the user agent. This allows us to block buggy versions without blocking all versions of
     *                        your app.
     *                    <li>NEVER lie about your user-agent. This includes spoofing popular browsers and spoofing
     *                        other bots. We will ban liars with extreme prejudice.</li>
     *                  </ul>
     *                  </blockquote>
     * @param adapter How the client will send HTTP requests
     */
    public RedditOAuth2Client(String userAgent, HttpAdapter adapter) {
        super(userAgent, adapter);
    }

    @Override
    public HttpRequest.Builder request() {
        return super.request().host(HOST_OAUTH);
    }

    @Override
    public LoggedInAccount login(Credentials credentials) throws NetworkException, ApiException {
        if (!credentials.getAuthenticationMethod().isOAuth2()) {
            throw new IllegalArgumentException("Credentials are not for OAuth2");
        }
        if (credentials.getAuthenticationMethod() != AuthenticationMethod.SCRIPT) {
            throw new IllegalArgumentException("Only 'script' app types supported on this method. Please use " +
                    "getOAuthHelper() instead to log in.");
        }

        return onAuthorized(authHelper.doScriptApp(credentials), credentials);
    }

    /**
     * Signifies that a successful authorization has been made
     * @param data The AuthData retrieved after requesting the access token
     * @param credentials The credentials used to retrieve this access token
     * @return The currently authenticated user
     * @throws NetworkException If the request to retrieve the authenticated user's data was not successful
     */
    public LoggedInAccount onAuthorized(OAuthData data, Credentials credentials) throws NetworkException {
        this.authData = data;
        httpAdapter.getDefaultHeaders().put(HEADER_AUTHORIZATION, "bearer " + authData.getAccessToken());

        LoggedInAccount me = me();
        this.authenticatedUser = me.getFullName();
        this.authMethod = credentials.getAuthenticationMethod();
        return me;
    }

    @Override
    @EndpointImplementation(Endpoints.OAUTH_ME)
    public LoggedInAccount me() throws NetworkException {
        RestResponse response = execute(request()
                .endpoint(Endpoints.OAUTH_ME)
                .build());
        // Usually we would use response.as(), but /api/v1/me does not return a "data" or "kind" node.
        return new LoggedInAccount(response.getJson());
    }

    /**
     * Throws an UnsupportedOperationException. Use {@link #revokeToken(Credentials)} instead.
     *
     * @throws NetworkException Never
     * @deprecated Use {@link #revokeToken(Credentials)}
     */
    @Override
    @Deprecated
    public void logout() throws NetworkException {
        throw new UnsupportedOperationException("Use revokeToken(Credentials) to logout");
    }

    @Override
    public LoggedInAccount register(String username, String password, String email, Captcha captcha, String captchaAttempt) throws NetworkException, ApiException {
        throw new UnsupportedOperationException("Not available through OAuth2");
    }

    /**
     * Revokes the OAuth2 access token. You will need to login again to continue using this client without error.
     * @param creds The credentials to use. The username and password are irrelevant; only the client ID and secret will
     *              be used.
     * @throws NetworkException If the request was not successful
     */
    public void revokeToken(Credentials creds) throws NetworkException {
        execute(request()
                .host(HOST_SPECIAL)
                .path("/api/v1/revoke_token")
                .post(JrawUtils.mapOf(
                        "token", authData.getAccessToken(),
                        "token_type_hint", "access_token"
                )).basicAuth(new BasicAuthData(creds.getClientId(), creds.getClientSecret()))
                .build());

        authData = null;
        authMethod = AuthenticationMethod.NONE;
    }

    /**
     * Refreshes the current access token
     * @param creds The credentials to use. The username and password are irrelevant; only the client ID and secret will
     *              be used.
     * @throws NetworkException If the request was not successful
     */
    public void refreshToken(Credentials creds) throws NetworkException {
        if (authData.getRefreshToken() == null) {
            throw new IllegalArgumentException("No refresh token was requested, therefore no refresh can be performed");
        }
        RestResponse response = execute(request()
                .https(true)
                .host(RedditClient.HOST_SPECIAL)
                .path("/api/v1/access_token")
                .post(JrawUtils.mapOf(
                        "grant_type", "refresh_token",
                        "refresh_token", authData.getRefreshToken()
                )).basicAuth(new BasicAuthData(creds.getClientId(), creds.getClientSecret()))
                .build());
        this.authData = new OAuthData(response.getJson());
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
            if (authMethod == AuthenticationMethod.NONE) {
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

    private static final class FriendModel {
        private final String name;

        private FriendModel(String name) {
            this.name = name == null ? "" : name;
        }

        public String getName() {
            return name;
        }
    }
}
