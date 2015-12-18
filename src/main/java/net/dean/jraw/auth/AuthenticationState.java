package net.dean.jraw.auth;

public enum AuthenticationState {
    /**
     * Either the client's access token is expired or nonexistent, or there is no refresh token. In either case, a new
     * OAuthData object will need to be obtained for the RedditClient.
     */
    NONE,
    /** The access token has expired or the client has not been authenticated yet, but a refresh token is available */
    NEED_REFRESH,
    /** The client is ready to use. It has at a minimum of an unexpired access token */
    READY
}
