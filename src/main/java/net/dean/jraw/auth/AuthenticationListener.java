package net.dean.jraw.auth;

import net.dean.jraw.http.oauth.OAuthData;

/**
 * A listener that is called whenever a RedditClient is authenticated.
 */
public interface AuthenticationListener {
    void onAuthenticated(OAuthData data);
}
