package net.dean.jraw.http;

/**
 * This class is responsible for handling any kind of Reddit user and OAuth2 credentials
 */
public final class Credentials {
    private final AuthenticationMethod authenticationMethod;
    private final String username;
    private final String password;
    private final String clientId;
    private final String clientSecret;

    private Credentials(AuthenticationMethod authenticationMethod, String username, String password, String clientId,
                       String clientSecret) {
        this.authenticationMethod = authenticationMethod;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    /**
     * Gets how these Credentials are to be used
     * @return The AuthenticationMethod
     */
    public AuthenticationMethod getAuthenticationMethod() {
        return authenticationMethod;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Gets the OAuth2 app's ID. Applicable for all OAuth app types.
     * @return The app's ID
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Gets the OAuth2 app's secret key. Applicable for all OAuth app types, however, for 'installed app' app types, the
     * secret will always be an empty string.
     *
     * @return The secret value for the application
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Creates new new Credentials object for "normal" (non-OAuth2) authentication
     * @param username The username
     * @param password The password
     * @return A new Credentials
     */
    public static Credentials standard(String username, String password) {
        return new Credentials(AuthenticationMethod.STANDARD, username, password, null, null);
    }

    /**
     * Creates a new Credentials object for a Reddit app whose type is 'installed app'
     * @param username The username
     * @param password The password
     * @param clientId The publicly-available app ID
     * @return A new Credentials
     */
    public static Credentials oauth2App(String username, String password, String clientId) {
        // Use an empty string as the client secret because "The 'password' for non-confidential clients
        // (installed apps) is an empty string". See https://github.com/reddit/reddit/wiki/OAuth2#token-retrieval
        return new Credentials(AuthenticationMethod.APP, username, password, clientId, "");
    }

    /**
     * Creates a new Credentails object for a Reddit app whose type is 'web app'
     * @param username The username
     * @param password The password
     * @param clientId The publicly-available app ID
     * @param clientSecret The secret value for the application
     * @return A new Credentials
     */
    public static Credentials oauth2Script(String username, String password, String clientId, String clientSecret) {
        return new Credentials(AuthenticationMethod.SCRIPT, username, password, clientId, clientSecret);
    }

    /**
     * Creates a new Credentails object for a Reddit app whose type is 'web app'
     * @param username The username
     * @param password The password
     * @param clientId The publicly-available app ID
     * @param clientSecret The secret value for the application
     * @return A new Credentials
     */
    public static Credentials oauth2Webapp(String username, String password, String clientId, String clientSecret) {
        return new Credentials(AuthenticationMethod.WEBAPP, username, password, clientId, clientSecret);
    }
}
