package net.dean.jraw.http.oauth;

import net.dean.jraw.http.AuthenticationMethod;

import java.util.UUID;

/**
 * This class is responsible for handling any kind of Reddit user and OAuth2 credentials
 */
public final class Credentials {
    private final AuthenticationMethod authenticationMethod;
    private final String username;
    private final String password;
    private final String clientId;
    private final String clientSecret;
    private final UUID deviceId;

    private Credentials(AuthenticationMethod authenticationMethod, String username, String password, String clientId,
                       String clientSecret) {
        this(authenticationMethod, username, password, clientId, clientSecret, null);
    }

    public Credentials(AuthenticationMethod authenticationMethod, String username, String password, String clientId,
                       String clientSecret, UUID deviceId) {
        this.authenticationMethod = authenticationMethod;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.deviceId = deviceId;
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
     * Gets a UUID that is unique for every device. Only non-null when instantiated using
     * {@link #userlessApp(String, UUID)}.
     */
    public UUID getDeviceId() {
        return deviceId;
    }

    /**
     * Creates a new Credentials object for a Reddit app whose type is 'installed app'
     * @param username The app owner's username
     * @param password The app owner's password
     * @param clientId The publicly-available app ID
     * @return A new Credentials
     */
    public static Credentials installedApp(String username, String password, String clientId) {
        // Use an empty string as the client secret because "The 'password' for non-confidential clients
        // (installed apps) is an empty string". See https://github.com/reddit/reddit/wiki/OAuth2#token-retrieval
        return new Credentials(AuthenticationMethod.APP, username, password, clientId, "");
    }

    /**
     * Creates a new Credentials object for a Reddit app whose type is 'web app'
     * @param username The app owner's username
     * @param password The app owner's password
     * @param clientId The publicly-available app ID
     * @param clientSecret The secret value for the application
     * @return A new Credentials
     */
    public static Credentials script(String username, String password, String clientId, String clientSecret) {
        return new Credentials(AuthenticationMethod.SCRIPT, username, password, clientId, clientSecret);
    }

    /**
     * Creates a new Credentials object for a Reddit app whose type is 'web app'
     * @param username The app owner's username
     * @param password The app owner's password
     * @param clientId The publicly-available app ID
     * @param clientSecret The secret value for the application
     * @return A new Credentials
     */
    public static Credentials webapp(String username, String password, String clientId, String clientSecret) {
        return new Credentials(AuthenticationMethod.WEBAPP, username, password, clientId, clientSecret);
    }

    /**
     * Creates a new Credentials object for using the API in a user-less context.
     * @see AuthenticationMethod#USERLESS
     */
    public static Credentials userless(String clientId, String clientSecret, UUID deviceId) {
        return new Credentials(AuthenticationMethod.USERLESS, null, null, clientId, clientSecret, deviceId);
    }

    /**
     * Creates a new Credentials object for using the API in a user-less context on an installed application.
     * @param deviceId The UUID unique to this device
     * @see AuthenticationMethod#USERLESS_APP
     */
    public static Credentials userlessApp(String clientId, UUID deviceId) {
        // Use an empty string as the client secret because "The 'password' for non-confidential clients
        // (installed apps) is an empty string". See https://github.com/reddit/reddit/wiki/OAuth2#token-retrieval
        return new Credentials(AuthenticationMethod.USERLESS_APP, null, null, clientId, "", deviceId);
    }
}
