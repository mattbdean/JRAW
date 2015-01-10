package net.dean.jraw.http.oauth;

/**
 * Represents the list of available Reddit OAuth2 apps
 */
public enum AppType {
    /** Runs as part of a web service on a server you control. Can keep a secret. */
    WEB,
    /**
     * Runs on devices you don't control, such as the user's mobile phone. Cannot keep a secret, and therefore, does not
     * receive one.
     */
    INSTALLED,
    /**
     * Runs on hardware you control, such as your own laptop or server. Can keep a secret. Only has access to your
     * account.
     */
    SCRIPT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
