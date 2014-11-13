package net.dean.jraw;

import com.google.common.base.Joiner;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.Credentials;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.http.RestRequest;
import net.dean.jraw.models.AccountPreferences;
import net.dean.jraw.models.KarmaBreakdown;
import net.dean.jraw.models.LoggedInAccount;
import org.codehaus.jackson.JsonNode;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a way to interact with the Reddit API using OAuth2. Before the program has exited, it is
 * recommended to revoke the access token (see {@link #revokeToken(Credentials)}).
 */
public class RedditOAuth2Client extends RedditClient {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private String[] scopes;
    private Date tokenExpiration;
    private String accessToken;
    private boolean hasRefreshed;

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
        setHttpsDefault(true);
    }

    @Override
    public RestRequest.Builder request() {
        return super.request().host(HOST_OAUTH);
    }

    @Override
    public LoggedInAccount login(Credentials credentials) throws NetworkException, ApiException {
        if (credentials.getAuthenticationMethod() != AuthenticationMethod.OAUTH2_SCRIPT) {
            throw new IllegalArgumentException("Only 'script' app types supported at this moment");
        }

        RedditResponse response = getAccessResponse(credentials);

        JsonNode root = response.getJson();
        this.accessToken = root.get("access_token").asText();
        // 'scopes' is a comma separated list of OAuth scopes
        this.scopes = root.get("scope").asText().split(",");
        this.tokenExpiration = new Date();
        // Add the time the token expires
        tokenExpiration.setTime(tokenExpiration.getTime() + root.get("expires_in").asInt() * 1000);
        defaultHeaders.put(HEADER_AUTHORIZATION, "bearer " + accessToken);

        LoggedInAccount me = me();
        this.authenticatedUser = me.getFullName();
        this.authMethod = credentials.getAuthenticationMethod();

        return me;
    }

    private RedditResponse getAccessResponse(Credentials credentials) throws NetworkException {
        if (credentials.getAuthenticationMethod() != AuthenticationMethod.OAUTH2_SCRIPT) {
            throw new IllegalArgumentException("This method authenticates only 'script' apps");
        }

        return executeWithBasicAuth(request()
                        .https(true)
                        .host(HOST_SPECIAL)
                        .path("/api/v1/access_token")
                        .post(JrawUtils.args(
                                "grant_type", "password",
                                "username", credentials.getUsername(),
                                "password", credentials.getPassword()
                        ))
                        .sensitiveArgs("password")
                        .build(),
                credentials.getClientId(), credentials.getClientSecret());
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

    @Override
    public void logout() throws NetworkException {
        throw new UnsupportedOperationException("Use revokeToken(Credentials) to logout");
    }

    /**
     * Revokes the OAuth2 access token. You will need to login again to continue using this client correctly.
     * @param creds The credentials to use. The username and password are irrelevant; only the client ID and secret will
     *              be used.
     * @throws NetworkException If the request was not successful
     */
    public void revokeToken(Credentials creds) throws NetworkException {
        executeWithBasicAuth(request()
                .host(HOST_SPECIAL)
                .path("/api/v1/revoke_token")
                .post(JrawUtils.args(
                        "token", accessToken,
                        "token_type_hint", hasRefreshed ? "refresh_token" : "access_token"
                )).build(),
                creds.getClientId(), creds.getClientSecret());

        accessToken = null;
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
     * Gets the API scopes that the app has been registered to use ('identity', 'flair', etc.)
     * @return An array of scopes
     */
    public String[] getScopes() {
        return scopes;
    }

    /**
     * Gets the date that the authorization token will expire. You will need to request a new one after this time passes.
     * @return The date at which the authorization token will expire
     */
    public Date getTokenExpiration() {
        return tokenExpiration;
    }

    /**
     * Gets the OAuth2 access token being used to send requests
     * @return The access token
     */
    public String getAccessToken() {
        return accessToken;
    }
}
