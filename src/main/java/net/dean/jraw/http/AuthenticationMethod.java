package net.dean.jraw.http;

public enum AuthenticationMethod {
    NONE,
    STANDARD,
    OAUTH2_WEBAPP,
    OAUTH2_APP,
    OAUTH2_SCRIPT;

    public boolean isOAuth2() {
        return name().startsWith("OAUTH2");
    }
}
