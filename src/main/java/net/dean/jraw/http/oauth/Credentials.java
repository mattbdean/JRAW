package net.dean.jraw.http.oauth;

import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.util.JrawUtils;

import java.net.URI;
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
    private final URI redirectUri;

    private Credentials(AuthenticationMethod authenticationMethod, String username, String password, String clientId,
                        String clientSecret, String redirectUri) {
        this(authenticationMethod, username, password, clientId, clientSecret, null, redirectUri);
    }

    public Credentials(AuthenticationMethod authenticationMethod, String username, String password, String clientId,
                       String clientSecret, UUID deviceId, String redirectUri) {
        this.authenticationMethod = authenticationMethod;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.deviceId = deviceId;
        this.redirectUri = redirectUri != null ? JrawUtils.newUri(redirectUri) : null;
    }

    /**
     * Gets how these Credentials are to be used
     * @return The AuthenticationMethod
     */
    public AuthenticationMethod getAuthenticationMethod() {
        return authenticationMethod;
    }

    public String getUsername() {
        if (username == null)
            throw new IllegalStateException("This method is not appropriate for this authentication method");
        return username;
    }

    public String getPassword() {
        if (username == null)
            throw new IllegalStateException("This method is not appropriate for this authentication method");
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
     * secret will always be an empty string. All parameters must be non-null.
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

    public URI getRedirectUri() {
        return redirectUri;
    }

    private static void assertNotNull(Object... objects) {
        for (Object o : objects)
            if (o == null)
                throw new NullPointerException("Arguments must not be null");
    }

    /**
     * Creates a new Credentials object for a Reddit app whose type is 'installed app.' All parameters must be non-null.
     * @param clientId The publicly-available app ID
     * @return A new Credentials
     */
    public static Credentials installedApp(String clientId, String redirectUri) {
        // Use an empty string as the client secret because "The 'password' for non-confidential clients
        // (installed apps) is an empty string". See https://github.com/reddit/reddit/wiki/OAuth2#token-retrieval
        assertNotNull(clientId, redirectUri);
        return new Credentials(AuthenticationMethod.APP, null, null, clientId, "", redirectUri);
    }

    /**
     * Creates a new Credentials object for a Reddit app whose type is 'web app.' All parameters must be non-null.
     *
     * @param username The app owner's username
     * @param password The app owner's password
     * @param clientId The publicly-available app ID
     * @param clientSecret The secret value for the application
     * @return A new Credentials
     */
    public static Credentials script(String username, String password, String clientId, String clientSecret) {
        assertNotNull(username, password, clientId, clientSecret);
        return new Credentials(AuthenticationMethod.SCRIPT, username, password, clientId, clientSecret, null);
    }

    /**
     * Creates a new Credentials object for a Reddit app whose type is 'web app.' All parameters must be non-null.
     *
     * @param username The app owner's username
     * @param password The app owner's password
     * @param clientId The publicly-available app ID
     * @param clientSecret The secret value for the application
     * @param redirectUri The uri to be redirected to
     * @return A new Credentials
     */
    public static Credentials script(String username, String password, String clientId, String clientSecret, String redirectUri) {
        assertNotNull(username, password, clientId, clientSecret);
        return new Credentials(AuthenticationMethod.SCRIPT, username, password, clientId, clientSecret, redirectUri);
    }

    /**
     * Creates a new Credentials object for a Reddit app whose type is 'web app.' All parameters must be non-null.
     * @param clientId The publicly-available app ID
     * @param clientSecret The secret value for the application
     * @return A new Credentials
     */
    public static Credentials webapp(String clientId, String clientSecret, String redirectUri) {
        assertNotNull(clientId, clientSecret, redirectUri);
        return new Credentials(AuthenticationMethod.WEBAPP, null, null, clientId, clientSecret, redirectUri);
    }

    /**
     * Creates a new Credentials object for using the API in a user-less context. All parameters must be non-null
     *
     * @param deviceId A unique, per-device ID.
     * @see AuthenticationMethod#USERLESS
     */
    public static Credentials userless(String clientId, String clientSecret, UUID deviceId) {
        assertNotNull(clientId, clientSecret, deviceId);
        return new Credentials(AuthenticationMethod.USERLESS, null, null, clientId, clientSecret, deviceId, null);
    }

    /**
     * Creates a new Credentials object for using the API in a user-less context on an installed application. All
     * parameters must be non-null.
     *
     * @param deviceId The UUID unique to this device
     * @see AuthenticationMethod#USERLESS_APP
     */
    public static Credentials userlessApp(String clientId, UUID deviceId) {
        // Use an empty string as the client secret because "The 'password' for non-confidential clients
        // (installed apps) is an empty string". See https://github.com/reddit/reddit/wiki/OAuth2#token-retrieval
        return new Credentials(AuthenticationMethod.USERLESS_APP, null, null, clientId, "", deviceId, null);
    }
}
