package net.dean.jraw.auth;

import net.dean.jraw.http.oauth.OAuthData;

/**
 * A listener that is called whenever an AndroidRedditClient is authenticated.
 */
public interface AuthenticationListener {
    void onAuthenticated(OAuthData data);
}
