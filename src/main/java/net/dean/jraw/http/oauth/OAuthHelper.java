package net.dean.jraw.http.oauth;

import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.Credentials;
import net.dean.jraw.http.MediaTypes;
import net.dean.jraw.http.NetworkAccessible;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RedditResponse;
import net.dean.jraw.http.RestRequest;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Map;

/**
 * <p>
 *     This class assists developers using this library in authenticating users via OAuth2. For the app types of
 *     'installed' or 'web', a typical use of this class is as follows:<br>
 * </p>
 * <ol>
 *     <li>Obtain an authorization URL using {@link #getAuthorizationUrl(String, String, boolean, String...)}.
 *     <li>Point the user's browser to that URL and have the user login and then press either 'yes' or 'no' on the
 *         authentication form. The URL that the browser redirects to will be your app's redirect URI with a few
 *         arguments in the query.
 *     <li>Give this data as well as an instance of {@link Credentials} to
 *         {@link #onUserChallenge(String, String, Credentials)}. This method will parse the query arguments and report
 *         any errors. Once the request's integrity has been verified, a request to obtain the OAuth access code will be
 *         made and an instance of {@link OAuthData} retrieved.
 * </ol>
 * <p>
 *     Authentication is simpler when the app type is 'script', as this enables the bypassing of showing the initial
 *     authorization URL to the user. However, it comes at the cost of only being able to log into the accounts of users
 *     registered as "developers". To authenticate with a 'script' app, one may simply use
 *     {@link #doScriptApp(Credentials)}.
 * </p>
 */
public class OAuthHelper implements NetworkAccessible<RedditResponse, RedditClient> {
    private SecureRandom secureRandom;
    private String state;
    private boolean started;
    private RedditClient reddit;

    /**
     * Instantiates a new OAuthHelper
     * @param reddit The RedditClient to use to help authenticate
     */
    public OAuthHelper(RedditClient reddit) {
        this.reddit = reddit;
    }

    /**
     * Generates a URL used to authorize a user using OAuth2 'installed' or 'web' type app.
     * @param clientId The app's client ID
     * @param redirectUri The app's redirect URI. Must match exactly as in the app settings.
     * @param permanent Whether or not to request a 'refresh' token which can be exchanged for an additional
     *                  Authorization token in the future.
     * @param scopes OAuth scopes to be requested. A full list of scopes can be found
     *              <a href="https://www.reddit.com/dev/api/oauth>here</a>
     * @return The URL clients are sent to in order to authorize themselves
     */
    public String getAuthorizationUrl(String clientId, String redirectUri, boolean permanent, String... scopes) {
        if (started) started = false; // Restarting

        if (secureRandom == null)
            secureRandom = new SecureRandom();
        // http://stackoverflow.com/a/41156/1275092
        this.state = new BigInteger(130, secureRandom).toString(32);

        RestRequest r = new RestRequest.Builder()
                .https(true)
                .host(RedditClient.HOST)
                .path("/api/v1/authorize")
                .expected(MediaTypes.HTML.type())
                .query(JrawUtils.args(
                        "client_id", clientId,
                        "response_type", "code",
                        "state", state,
                        "redirect_uri", redirectUri,
                        "duration", permanent ? "permanent" : "temporary",
                        "scope", JrawUtils.join(scopes)
                )).build();
        this.started = true;
        return r.getUrl();
    }

    /**
     * Used obtain an access token for 'web' or 'installed' app types. This method parses the query arguments passed to
     * this URI. If no error is present and the 'state' code matches the one <em>most recently</em> generated, then an
     * access token is requested.
     * @param redirectUri The app's redirect URI. Must match exactly as in the app settings.
     * @param finalUrl The URL that the HTTP client redirected to after the user chose either to authorize or not
     *                 authorize the application. This will be the app's redirect URI with the addition of a few query
     *                 parameters.
     * @param creds The credentials to retrieve the access token with. If the authorization method is
     *              {@link AuthenticationMethod#SCRIPT}, stop what you're doing and use
     *              {@link #doScriptApp(Credentials)} instead.
     * @throws OAuthException If there was a problem with any of the parameters given
     * @throws NetworkException If the request was not successful
     * @throws MalformedURLException If {@code finalUrl} is not a valid URL
     * @throws IllegalStateException If the state last generated with
     *                               {@link #getAuthorizationUrl(String, String, boolean, String...)} did not match the
     *                               value of the 'state' query parameter.
     * @return An AuthData that holds the new access token among other things
     */
    public OAuthData onUserChallenge(String finalUrl, String redirectUri, Credentials creds) throws NetworkException,
            OAuthException, IllegalStateException, MalformedURLException {
        if (!creds.getAuthenticationMethod().isOAuth2()) {
            throw new IllegalArgumentException("Credentials provided are not for an OAuth2 login.");
        }

        if (creds.getAuthenticationMethod() == AuthenticationMethod.SCRIPT) {
            JrawUtils.logger().warn("Unnecessarily complicated auth process. Use doScriptApp() instead.");
            return doScriptApp(creds);
        }

        if (!started) {
            throw new IllegalStateException("Auth flow not started yet. See getAuthorizationUrl()");
        }
        RestRequest request = RestRequest.from("invalid", new URL(finalUrl));
        Map<String, String> query = request.getQuery();
        if (!query.containsKey("state")) {
            throw new IllegalArgumentException("Final redirect URI did not contain the 'state' query parameter");
        }
        if (!query.get("state").equals(state)) {
            throw new IllegalArgumentException("State did not match");
        }
        if (query.containsKey("error")) {
            throw new OAuthException(query.get("error"));
        }
        if (!query.containsKey("code")) {
            throw new IllegalArgumentException("Final redirect URI did not contain the 'code' parameter");
        }

        String code = query.get("code");

        try {
            RedditResponse response = reddit.execute(reddit.request()
                    .https(true)
                    .host(RedditClient.HOST)
                    .path("/api/v1/access_token")
                    .post(JrawUtils.args(
                            "grant_type", "authorization_code",
                            "code", code,
                            "redirect_uri", redirectUri
                    ))
                    .basicAuth(creds.getClientId(), creds.getClientSecret())
                    .build());
            return new OAuthData(response.getJson());
        } catch (NetworkException e) {
            if (e.getCode() == 401) {
                throw new OAuthException("Invalid client ID/secret", e);
            }
            throw e;
        }
    }

    /**
     * Authorizes a 'script' app by utilizing a shortcut specific to Reddit's OAuth2 implementation which allows
     * 'script' apps to skip the authorization prompt in-browser and directly authorize with their Reddit username and
     * password and the app's client ID and secret. However, only users listed as developers of the app my be
     * authorized. This method is most frequently used to authorize a user in a headless environment or a bot.
     *
     * @param credentials The credentials to use. The authentication method must be {@link AuthenticationMethod#SCRIPT}.
     * @return The data returned from the authorization request
     * @throws NetworkException If the request was not successful
     */
    public OAuthData doScriptApp(Credentials credentials) throws NetworkException {
        if (credentials.getAuthenticationMethod() != AuthenticationMethod.SCRIPT) {
            throw new IllegalArgumentException("This method only authenticates 'script' apps");
        }

        RedditResponse response = reddit.execute(reddit.request()
                .https(true)
                .host(RedditClient.HOST_SPECIAL)
                .path("/api/v1/access_token")
                .post(JrawUtils.args(
                        "grant_type", "password",
                        "username", credentials.getUsername(),
                        "password", credentials.getPassword()
                ))
                .sensitiveArgs("password")
                .basicAuth(credentials.getClientId(), credentials.getClientSecret())
                .build());

        return new OAuthData(response.getJson());
    }

    @Override
    public RedditClient getHttpClient() {
        return reddit;
    }
}
