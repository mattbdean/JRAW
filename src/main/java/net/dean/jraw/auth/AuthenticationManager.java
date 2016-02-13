package net.dean.jraw.auth;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.oauth.OAuthHelper;

import java.util.Date;

/**
 * Helps developers manage the OAuth2 authentication process better, especially if the app will need to be
 * reauthenticated using a refresh token. For apps that do not use refresh tokens (such as script and application only
 * apps), this class may be a bit over the top. However, if need be, an {@link ApatheticTokenStore} can be used to
 * initialize the manager.
 *
 * <p>This class attempts to manage a synchronous RedditClient in an asynchronous environment such as how Android is
 * encouraged to run in. AuthenticationManager's singleton instance can be retrieved using {@link #get()}. If the
 * developer wants to make use of the class, they must first initialize it with a call to
 * {@link #init(RedditClient, RefreshTokenHandler)}.
 *
 * <p>This class is designed to make authentication easier, especially when dealing with refresh tokens. Refresh tokens
 * will automatically be stored in a {@link TokenStore} as long as the RedditClient calls
 * {@link #onAuthenticated(OAuthData)}. A call to {@link #checkAuthState()} will tell a developer exactly what the
 * library knows and how the developer can go about getting the client authenticated.
 */
public final class AuthenticationManager {
    private static final AuthenticationManager INSTANCE = new AuthenticationManager();
    public static AuthenticationManager get() { return INSTANCE; }

    /** Returns true if the given OAuthData is not null and its access token is not expired */
    private static boolean isValid(OAuthData data) {
        return data != null && data.getExpirationDate().after(new Date());
    }

    private RedditClient reddit;
    private RefreshTokenHandler tokenHandler;

    private AuthenticationManager() {}

    /**
     * Initializes the AuthenticationManager. Must be called before this class can be used. Sets the RedditClient's
     * {@link AuthenticationListener}.
     */
    public void init(RedditClient reddit, RefreshTokenHandler tokenHandler) {
        this.tokenHandler = tokenHandler;
        this.reddit = reddit;
        reddit.setAuthenticationListener(new AuthenticationListener() {
            @Override
            public void onAuthenticated(OAuthData data) {
                AuthenticationManager.this.onAuthenticated(data);
            }
        });
    }

    /** Gets the lazy-initialized global RedditClient */
    public RedditClient getRedditClient() {
        if (reddit == null || tokenHandler == null)
            throw new IllegalStateException("init() the manager first");
        return reddit;
    }

    /**
     * Evaluates the manager's RedditClient and TokenStore to determine the current authentication state
     *
     * @see AuthenticationState
     */
    public AuthenticationState checkAuthState() {
        RedditClient reddit = getRedditClient();
        OAuthData authData = reddit.getOAuthData();

        // The client is authenticated with an unexpired access token
        if (reddit.isAuthenticated() && isValid(authData))
            return AuthenticationState.READY;

        // At this point the RedditClient either has an expired access token or no access token at all
        if (tokenHandler.isStored(getUsername()))
            return AuthenticationState.NEED_REFRESH;

        return AuthenticationState.NONE;
    }

    /**
     * If a refresh token exists already, this method will retrieve a new access token and update the RedditClient with
     * the new information
     *
     * @param credentials The Credentials used to request the initial access token
     */
    public void refreshAccessToken(Credentials credentials) throws NoSuchTokenException, OAuthException {
        OAuthHelper oauth = getRedditClient().getOAuthHelper();

        if (oauth.getRefreshToken() == null) {
            if (!tokenHandler.isStored(getUsername()))
                throw new IllegalStateException("Cannot refresh the access token without a refresh token");
            oauth.setRefreshToken(tokenHandler.readToken(getUsername()));
        }

        // Write the refresh token if the client has one and it's either not already stored or the stored version is
        // different from the client's
        if (oauth.canRefresh() && (!tokenHandler.isStored(getUsername()) ||
                !tokenHandler.readToken(getUsername()).equals(oauth.getRefreshToken()))) {
            tokenHandler.writeToken(getUsername(), oauth.getRefreshToken());
        }
        OAuthData data = oauth.refreshToken(credentials);
        getRedditClient().authenticate(data);
    }

    /**
     * Writes the OAuthData's refresh token to the RefreshTokenHandler. For optimal results this method should be called
     * by the RedditClient's implementation of {@link RedditClient#authenticate(OAuthData)}.
     */
    public void onAuthenticated(OAuthData o) {
        if (o.getRefreshToken() != null)
            tokenHandler.writeToken(getUsername(), o.getRefreshToken());
    }

    private String getUsername() {
        if (!reddit.isAuthenticated()) {
            if (tokenHandler.hasLastAuthenticated()) {
                try {
                    return tokenHandler.getLastAuthenticatedUser();
                } catch (NoSuchTokenException e) {
                    throw new IllegalStateException("RefreshTokenHandler.hasLastAuthenticated() is lying");
                }
            } else {
                return null;
            }
        } else if (!reddit.hasActiveUserContext()) {
            return null;
        } else {
            return reddit.getAuthenticatedUser();
        }
    }
}
