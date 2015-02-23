package net.dean.jraw.http.oauth;

import java.net.URL;

/**
 * Thrown when Reddit 403's a request because the client lacks the required OAuth2 scope.
 */
public class InvalidScopeException extends RuntimeException {
    public InvalidScopeException(URL requestUrl) {
            super("Lacking required scope for \'" + requestUrl.getPath() + '\'');
    }
}
