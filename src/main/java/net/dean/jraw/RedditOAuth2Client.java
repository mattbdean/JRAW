package net.dean.jraw;

import com.google.common.base.Joiner;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.Credentials;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.http.RestRequest;
import net.dean.jraw.http.oauth.AuthData;
import net.dean.jraw.http.oauth.OAuthHelper;
import net.dean.jraw.models.AccountPreferences;
import net.dean.jraw.models.KarmaBreakdown;
import net.dean.jraw.models.LoggedInAccount;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a way to interact with the Reddit API using OAuth2. Before the program has exited, it is
 * recommended to revoke the access token (see {@link #revokeToken(Credentials)}).
 */
public class RedditOAuth2Client extends RedditClient {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private boolean hasRefreshed;
    private AuthData authData;
    private OAuthHelper authHelper;

    /**
     * Instantiates a new OAuth2RedditClient
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
    public RedditOAuth2Client(String userAgent) {
        super(userAgent, REQUESTS_PER_MINUTE_OAUTH2);
        this.hasRefreshed = false;
        this.authHelper = new OAuthHelper(this);
        setHttpsDefault(true);
    }

    @Override
    public RestRequest.Builder request() {
        return super.request().host(HOST_OAUTH);
    }

    @Override
    public LoggedInAccount login(Credentials credentials) throws NetworkException, ApiException {
        if (!credentials.getAuthenticationMethod().isOAuth2()) {
            throw new IllegalArgumentException("Credentials are not for OAuth2");
        }
        if (credentials.getAuthenticationMethod() != AuthenticationMethod.SCRIPT) {
            throw new IllegalArgumentException("Only 'script' app types supported on this method. Please use" +
                    "getOAuthHelper() instead to");
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
    public LoggedInAccount onAuthorized(AuthData data, Credentials credentials) throws NetworkException {
        this.authData = data;
        defaultHeaders.put(HEADER_AUTHORIZATION, "bearer " + authData.getAccessToken());

        LoggedInAccount me = me();
        this.authenticatedUser = me.getFullName();
        this.authMethod = credentials.getAuthenticationMethod();
        return me;
    }

    @Override
    @EndpointImplementation(Endpoints.OAUTH_ME)
    public LoggedInAccount me() throws NetworkException {
        RedditResponse response = execute(request()
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

    /**
     * Revokes the OAuth2 access token. You will need to login again to continue using this client without error.
     * @param creds The credentials to use. The username and password are irrelevant; only the client ID and secret will
     *              be used.
     * @throws NetworkException If the request was not successful
     */
    public void revokeToken(Credentials creds) throws NetworkException {
        executeWithBasicAuth(request()
                .host(HOST_SPECIAL)
                .path("/api/v1/revoke_token")
                .post(JrawUtils.args(
                        "token", authData.getAccessToken(),
                        "token_type_hint", hasRefreshed ? "refresh_token" : "access_token"
                )).build(),
                creds.getClientId(), creds.getClientSecret());

        authData = null;
        authMethod = AuthenticationMethod.NONE;
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
            query.put("fields", Joiner.on(',').join(names));
        }

        RedditResponse response = execute(request()
                .endpoint(Endpoints.OAUTH_ME_PREFS_GET)
                .query(query)
                .build());
        return new AccountPreferences(response.getJson());
    }

    /**
     * Gets a breakdown of link and comment karma by subreddit
     * @return A KarmaBreakdown for this account
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.OAUTH_ME_KARMA)
    public KarmaBreakdown getKarmaBreakdown() throws NetworkException {
        RedditResponse response = execute(request()
                .endpoint(Endpoints.OAUTH_ME_KARMA)
                .build());
        return new KarmaBreakdown(response.getJson().get("data"));
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
    public AuthData getAuthData() {
        return authData;
    }
}
