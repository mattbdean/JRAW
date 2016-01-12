package net.dean.jraw.auth;

import net.dean.jraw.RedditClient;

/**
 * Handles the saving of refresh tokens. Supports multiple users.
 *
 * <p>This class is a specialized implementation of TokenStore whose only objective is to store refresh tokens for
 * various users. This class combines two sources for reading, writing, and checking: a RedditClient and a generic
 * TokenStore. The {@link #isStored(String)} and {@link #readToken(String)} methods will use both sources, while
 * {@link #writeToken(String, String)} will only use the TokenStore. Although it is not required, it is
 * recommended that the TokenStore source write to some place more permanent, such as a file or a database.
 *
 * <p>In the TokenStore, the key for a particular username is itself prefixed with {@link #REFRESH_TOKEN_PREFIX}.
 *
 * <p>For a RedditClient to be a valid token source for a given username, it must
 * <ol>
 *    <li>Be currently authenticated,
 *    <li>Return the same value as the given username when {@link RedditClient#getAuthenticatedUser()} is called, and
 *    <li>Have an OAuthData object containing a refresh token.
 * </ol>
 */
public final class RefreshTokenHandler implements TokenStore {
    public static final String REFRESH_TOKEN_PREFIX = "refresh_token_";
    private static final String KEY_LAST_USER = "last_user";

    private static String getKeyFor(String username) {
        return REFRESH_TOKEN_PREFIX + username;
    }

    private final TokenStore store;
    private final RedditClient reddit;

    /** Instantiates a new RefreshTokenHandler */
    public RefreshTokenHandler(TokenStore store, RedditClient reddit) {
        if (store == null || reddit == null)
            throw new NullPointerException("Neither the TokenStore nor the RedditClient may be null");
        this.store = store;
        this.reddit = reddit;
    }

    @Override
    public boolean isStored(String username) {
        return store.isStored(getKeyFor(username)) || isClientValidSource(username);
    }

    @Override
    public String readToken(String username) throws NoSuchTokenException {
        if (reddit.getOAuthData() == null)
            return store.readToken(getKeyFor(username));
        if (reddit.getOAuthData().getRefreshToken() != null)
            return reddit.getOAuthData().getRefreshToken();
        throw new NoSuchTokenException("Refresh token does not exist in the TokenStore nor the RedditClient");
    }

    @Override
    public void writeToken(String username, String token) {
        store.writeToken(getKeyFor(username), token);
        store.writeToken(KEY_LAST_USER, username);
    }

    /**
     * Gets the name of the last authenticated user, throws a NoSuchTokenException if the TokenStore does not know
     *
     * @see #hasLastAuthenticated()
     */
    public String getLastAuthenticatedUser() throws NoSuchTokenException {
        return store.readToken(KEY_LAST_USER);
    }

    /** Checks if the TokenStore knows the name of the last authenticated user */
    public final boolean hasLastAuthenticated() {
        return store.isStored(KEY_LAST_USER);
    }

    private boolean isClientValidSource(String username) {
        return reddit.isAuthenticated() &&
                reddit.getAuthenticatedUser().equals(username) &&
                reddit.getOAuthData().getRefreshToken() != null;
    }
}
