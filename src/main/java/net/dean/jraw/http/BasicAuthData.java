package net.dean.jraw.http;

import net.dean.jraw.http.oauth.OAuthData;

/**
 * Represents a username and password to be used when executing a request using HTTP Basic Authentication. Not to be
 * confused with {@link OAuthData}
 */
public class BasicAuthData {
    private final String username;
    private final String password;

    /**
     * Instantiates a new BasicAuthData
     */
    public BasicAuthData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Returns true if and only if the username and password are not null
     * @return If this BasicAuthData contains a non-null username and password
     */
    public boolean isValid() {
        return username != null && password != null;
    }
}
