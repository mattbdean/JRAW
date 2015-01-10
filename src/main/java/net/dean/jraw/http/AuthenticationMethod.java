package net.dean.jraw.http;

import net.dean.jraw.http.oauth.AppType;
import net.dean.jraw.RedditClient;

/**
 * A list of ways a client can authenticate themselves using Reddit's API
 */
public enum AuthenticationMethod {
    /** No authentication, uses the non-OAuth2 API */
    NONE,
    /** Non-OAuth2 authentication ({@link RedditClient#login(Credentials)} */
    STANDARD,
    /** OAuth2 authentication on a website. See {@link AppType#WEB} for more. */
    WEBAPP(AppType.WEB),
    /** OAuth2 authentication on an installed app. See {@link AppType#INSTALLED} for more. */
    APP(AppType.INSTALLED),
    /** OAuth2 authentication on a script. See {@link AppType#SCRIPT} for more. */
    SCRIPT(AppType.SCRIPT);

    private AppType appType;
    private AuthenticationMethod() {
        this(null);
    }
    private AuthenticationMethod(AppType appType) {
        this.appType = appType;
    }

    /**
     * Checks if this AuthenticationMethod is to be used in conjunction with OAuth2
     * @return If this uses OAuth2
     */
    public boolean isOAuth2() {
        return appType != null;
    }
}
