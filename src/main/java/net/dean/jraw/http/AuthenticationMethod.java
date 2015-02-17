package net.dean.jraw.http;

import net.dean.jraw.http.oauth.AppType;

/**
 * A list of ways a client can authenticate themselves using Reddit's API
 */
public enum AuthenticationMethod {
    /** Not yet authenticated. */
    NOT_YET,
    /** OAuth2 authentication on a website. See {@link AppType#WEB} for more. */
    WEBAPP,
    /** OAuth2 authentication on an installed app. See {@link AppType#INSTALLED} for more. */
    APP,
    /** OAuth2 authentication on a script. See {@link AppType#SCRIPT} for more. */
    SCRIPT,
    /** OAuth2 authentication without the context of a user. If this is a mobile app, use {@link #USERLESS_APP}. */
    USERLESS(true), // Either WEB or SCRIPT could be used, doesn't really matter
    /**
     * OAuth2 authentication without the context of a user. Use this over {@link #USERLESS} if this is being used on a
     * mobile app and thus cannot retain a secret.
     */
    USERLESS_APP(true);

    private boolean userless;
    private AuthenticationMethod() {
        this(false);
    }
    private AuthenticationMethod(boolean userless) {
        this.userless = userless;
    }

    /**
     * Returns true if this AuthenticationMethod does not require a user
     */
    public boolean isUserless() {
        return userless;
    }
}
