package net.dean.jraw.http.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.net.MediaType;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.BasicAuthData;
import net.dean.jraw.http.HttpRequest;
import net.dean.jraw.http.MediaTypes;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestResponse;

import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This class assists developers using this library in authenticating users via OAuth2. For the app types of
 *    'installed' or 'web', a typical use of this class is as follows:
 *
 * <ol>
 *     <li>Obtain an authorization URL using {@link #getAuthorizationUrl(Credentials, boolean, String...)}.
 *     <li>Point the user's browser to that URL and have the user login and then press either 'yes' or 'no' on the
 *         authentication form. The URL that the browser redirects to will be your app's redirect URI with some
 *         arguments in the query.
 *     <li>Provide this data as well as an instance of {@link Credentials} to
 *         {@link #onUserChallenge(String, Credentials)}. This method will parse the query arguments and report any
 *         errors. Once the response's integrity has been verified, a request to obtain the OAuth access code will be
 *         made and an instance of {@link OAuthData} retrieved.
 * </ol>
 *
 * <p>Authentication is simpler when the app type is 'script', as this enables the bypassing of showing the initial
 *    authorization URL to the user. However, it comes at the cost of only being able to log into the accounts of users
 *    registered as "developers." To authenticate with a 'script' app or in a userless context, one may simply use
 *    {@link #easyAuth(Credentials)}.
 */
public class OAuthHelper {
    private static final String GRANT_TYPE = "https://oauth.reddit.com/grants/installed_client";
    private AuthStatus authStatus;
    private String refreshToken;
    private SecureRandom secureRandom;
    private String state;
    private RedditClient reddit;

    /**
     * Instantiates a new OAuthHelper
     * @param reddit The RedditClient to use to help authenticate
     */
    public OAuthHelper(RedditClient reddit) {
        this.reddit = reddit;
        this.authStatus = AuthStatus.NOT_YET;
    }

    /**
     * Generates a URL used to authorize a user using OAuth2 'installed' or 'web' type app.
     *
     * @param creds The app's credentials
     * @param permanent Whether or not to request a refresh token which can be exchanged for an additional authorization
     *                  token in the future.
     * @param scopes OAuth scopes to be requested. A full list of scopes can be found
     *               <a href="https://www.reddit.com/dev/api/oauth">here</a>.
     * @return The URL clients are sent to in order to authorize themselves
     */
    public URL getAuthorizationUrl(Credentials creds, boolean permanent, String... scopes) {
        if (secureRandom == null)
            secureRandom = new SecureRandom();
        // http://stackoverflow.com/a/41156/1275092
        this.state = new BigInteger(130, secureRandom).toString(32);

        HttpRequest r = new HttpRequest.Builder()
                .https(true)
                .host(RedditClient.HOST_SPECIAL)
                .path("/api/v1/authorize")
                .expected(MediaTypes.HTML.type())
                .query(JrawUtils.mapOf(
                        "client_id", creds.getClientId(),
                        "response_type", "code",
                        "state", state,
                        "redirect_uri", creds.getRedirectUrl().toExternalForm(),
                        "duration", permanent ? "permanent" : "temporary",
                        "scope", JrawUtils.join(' ', scopes)
                )).build();

        this.authStatus = AuthStatus.WAITING_FOR_CHALLENGE;
        return r.getUrl();
    }

    /**
     * Used obtain an access token for 'web' or 'installed' app types. This method parses the query arguments passed to
     * this URI. If no error is present and the 'state' code matches the one <em>most recently</em> generated, then an
     * access token is requested.
     *
     * @param finalUrl The URL that the HTTP client redirected to after the user chose either to authorize or not
     *                 authorize the application. This will be the app's redirect URI with the addition of a few query
     *                 parameters.
     * @param creds The credentials to retrieve the access token with. If the authorization method is
     *              {@link AuthenticationMethod#SCRIPT} or application-only, stop what you're doing and use
     *              {@link #easyAuth(Credentials)} instead.
     * @throws OAuthException If there was a problem with any of the parameters given
     * @throws NetworkException If the request was not successful
     * @throws IllegalStateException If the state last generated with {@link #getAuthorizationUrl} did not match the
     *                               value of the 'state' query parameter.
     * @return An OAuthData that holds the new access token among other things
     */
    public OAuthData onUserChallenge(String finalUrl, Credentials creds) throws NetworkException,
            OAuthException, IllegalStateException {
        if (authStatus != AuthStatus.WAITING_FOR_CHALLENGE) {
            throw new IllegalStateException("Auth flow not started yet. See getAuthorizationUrl()");
        }

        HttpRequest request = HttpRequest.from("irrelevant", JrawUtils.newUrl(finalUrl));
        Map<String, String> query = JrawUtils.parseUrlEncoded(request.getUrl().getQuery());
        if (!query.containsKey("state"))
            throw new IllegalArgumentException("Final redirect URI did not contain the 'state' query parameter");
        if (!query.get("state").equals(state))
            throw new IllegalStateException("State did not match");
        if (query.containsKey("error"))
            throw new OAuthException(query.get("error"));
        if (!query.containsKey("code"))
            throw new IllegalArgumentException("Final redirect URI did not contain the 'code' parameter");

        String code = query.get("code");

        try {
            RestResponse response = reddit.execute(reddit.request()
                    .https(true)
                    .host(RedditClient.HOST_SPECIAL)
                    .path("/api/v1/access_token")
                    .expected(MediaType.ANY_TYPE)
                    .post(JrawUtils.mapOf(
                            "grant_type", "authorization_code",
                            "code", code,
                            "redirect_uri", creds.getRedirectUrl()
                    ))
                    .basicAuth(new BasicAuthData(creds.getClientId(), creds.getClientSecret()))
                    .build());
            this.authStatus = AuthStatus.AUTHORIZED;
            OAuthData data = new OAuthData(creds.getAuthenticationMethod(), response.getJson());
            this.refreshToken = data.getRefreshToken();
            return data;
        } catch (NetworkException e) {
            if (e.getResponse().getStatusCode() == 401) {
                throw new OAuthException("Invalid client ID/secret", e);
            }
            throw e;
        }
    }

    /**
     * Authorizes a 'script' app or any other in application-only (user-less) mode.
     * @return A new OAuthData representing the access token response
     * @throws NetworkException If the request was not successful. If the HTTP status code is 403, then it is likely
     *                          that the Credentials object provided had incorrect data.
     * @throws OAuthException If the API returned a JSON error. Only thrown when using application-only authentication.
     */
    public OAuthData easyAuth(Credentials creds) throws NetworkException, OAuthException {
        switch (creds.getAuthenticationMethod()) {
            case SCRIPT:
                return doScriptApp(creds);
            case USERLESS:
            case USERLESS_APP:
                return doApplicationOnly(creds);
            default:
                throw new IllegalArgumentException("Only 'script' app types and userless authentication is supported by " +
                        "this method. Please use getOAuthHelper() instead to log in.");
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
    private OAuthData doScriptApp(Credentials credentials) throws NetworkException {
        if (credentials.getAuthenticationMethod() != AuthenticationMethod.SCRIPT) {
            throw new IllegalArgumentException("This method only authenticates 'script' apps");
        }

        RestResponse response = reddit.execute(accessTokenRequest(
                new BasicAuthData(credentials.getClientId(), credentials.getClientSecret()))
                .post(JrawUtils.mapOf(
                        "grant_type", "password",
                        "username", credentials.getUsername(),
                        "password", credentials.getPassword()
                ))
                .sensitiveArgs("password")
                .build());
        authStatus = AuthStatus.AUTHORIZED;
        state = null;
        return new OAuthData(credentials.getAuthenticationMethod(), response.getJson());
    }

    /**
     * Authenticates using application-only OAuth2 (in a user-less context).
     * @param credentials The app's credentials. The authentication method must be
     *                    {@link AuthenticationMethod#isUserless() user-less}.
     * @return The data returned from the authorization request
     * @throws NetworkException If the request was not successful
     */
    private OAuthData doApplicationOnly(Credentials credentials) throws NetworkException, OAuthException {
        if (credentials.getAuthenticationMethod() != AuthenticationMethod.USERLESS &&
                credentials.getAuthenticationMethod() != AuthenticationMethod.USERLESS_APP) {
            throw new IllegalArgumentException("This method is for user-less authorizations only");
        }

        Map<String, String> args = new HashMap<>();
        args.put("grant_type", GRANT_TYPE);
        if (credentials.getAuthenticationMethod().isUserless()) {
            if (credentials.getDeviceId() == null)
                throw new NullPointerException("Authentication method was userless but no device ID was present");
            args.put("device_id", credentials.getDeviceId().toString());
        }

        RestResponse response = reddit.execute(accessTokenRequest(
                new BasicAuthData(credentials.getClientId(), credentials.getClientSecret()))
                .post(args)
                .build());
        checkError(response.getJson());
        authStatus = AuthStatus.AUTHORIZED;
        return new OAuthData(credentials.getAuthenticationMethod(), response.getJson());
    }

    /**
     * Revokes the OAuth2 access token. You will need to login again to continue using this client without error.
     * @param creds The credentials to use. The username and password are irrelevant; only the client ID and secret will
     *              be used.
     * @throws NetworkException If the request was not successful
     */
    public void revokeAccessToken(Credentials creds) throws NetworkException {
        if (!reddit.isAuthenticated())
            return;
        revokeToken(creds, reddit.getOAuthData().getAccessToken(), "access_token");
        authStatus = AuthStatus.REVOKED;
        reddit.deauthenticate();
    }

    /**
     * Revokes the OAuth2 refresh token. You will need to have the user reauthenticate using
     * {@link #getAuthorizationUrl}.
     *
     * @param creds The credentials used to request the original token
     */
    public void revokeRefreshToken(Credentials creds) throws NetworkException {
        if (!canRefresh()) return;
        revokeToken(creds, refreshToken, "refresh_token");
        refreshToken = null;
    }

    /**
     * Revokes an OAuth2 token
     * @param creds The credentials used to request the original token
     * @param token Token value
     * @param tokenType One of "access_token" or "refresh_token"
     */
    private void revokeToken(Credentials creds, String token, String tokenType) throws NetworkException {
        reddit.execute(reddit.request()
                .host(RedditClient.HOST_SPECIAL)
                .path("/api/v1/revoke_token")
                .post(JrawUtils.mapOf(
                        "token", token,
                        "token_type_hint", tokenType
                )).basicAuth(new BasicAuthData(creds.getClientId(), creds.getClientSecret()))
                .build());
    }

    /**
     * Refreshes the access token. Must have requested an access token when calling {@link #getAuthorizationUrl}.
     *
     * @param creds The credentials used to request the original access token. Only the client ID and client secret will
     *              be used.
     * @return A new OAuthData
     * @throws OAuthException If the client ID or secret was incorrect
     */
    public OAuthData refreshToken(Credentials creds) throws NetworkException, OAuthException {
        if (!canRefresh()) {
            throw new IllegalStateException("No refresh token");
        }

        try {
            RestResponse response = reddit.execute(reddit.request()
                    .https(true)
                    .host(RedditClient.HOST_SPECIAL)
                    .path("/api/v1/access_token")
                    .expected(MediaType.ANY_TYPE)
                    .post(JrawUtils.mapOf(
                            "grant_type", "refresh_token",
                            "refresh_token", refreshToken
                    ))
                    .basicAuth(new BasicAuthData(creds.getClientId(), creds.getClientSecret()))
                    .build());
            this.authStatus = AuthStatus.AUTHORIZED;
            return new OAuthData(creds.getAuthenticationMethod(), response.getJson());
        } catch (NetworkException e) {
            if (e.getResponse().getStatusCode() == 401) {
                throw new OAuthException("Invalid client ID/secret", e);
            }
            throw e;
        }
    }

    /** Gets the token that will allow the client to get more access tokens */
    public String getRefreshToken() {
        return refreshToken;
    }

    /** Assigns the refresh token. */
    public void setRefreshToken(String refreshToken) {
        if (refreshToken == null)
            throw new NullPointerException("refreshToken cannot be null");
        this.refreshToken = refreshToken;
    }

    /** Checks if there is a refresh token available. */
    public boolean canRefresh() {
        return refreshToken != null;
    }

    private void checkError(JsonNode json) throws OAuthException {
        if (json.has("error")) {
            throw new OAuthException(String.format("%s (%s)", json.get("error").asText(), json.get("error_description").asText()));
        }
    }

    private HttpRequest.Builder accessTokenRequest(BasicAuthData authData) {
        return reddit.request()
                .https(true)
                .host(RedditClient.HOST_SPECIAL)
                .path("/api/v1/access_token")
                .basicAuth(authData);
    }


    /** Gets the current state of authorization */
    public AuthStatus getAuthStatus() {
        return authStatus;
    }

    /**
     * Represents the different states of authorization this class can be in
     */
    public static enum AuthStatus {
        /** No action has been performed */
        NOT_YET,
        /** An authorization URL has been created, but the user has not accepted/declined yet */
        WAITING_FOR_CHALLENGE,
        /** Authorized and ready to send requests */
        AUTHORIZED,
        /** The access token has been revoked and is no longer usable. */
        REVOKED
    }
}
