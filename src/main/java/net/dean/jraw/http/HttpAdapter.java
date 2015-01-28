package net.dean.jraw.http;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This interface provides a library-inspecific way of executing HTTP requests.
 */
public interface HttpAdapter {
    /**
     * Executes an HTTP request.
     *
     * @param request A model containing all necessary data
     * @return A RestResponse from the resulting request
     * @throws IOException If an implementation-specific error occurred.
     * @throws NetworkException If the request was not successful. A request is considered unsuccessful if the HTTP
     *                          status code is less than 300 and greater than or equal to 200.
     */
    public RestResponse execute(RestRequest request) throws NetworkException, IOException;

    /**
     * Gets the HTTP status code from the library-specific object
     * @param response The object returned by {@link #execute(RestRequest)}
     * @return An integer that represents the HTTP status code of the response
     */
    public int getCode(Object response);

    /**
     * Gets the time in milliseconds the HTTP client will wait while trying to connect before timing out
     * @return Connection timeout in milliseconds
     */
    public int getConnectTimeout();

    /**
     * Sets the time in milliseconds the HTTP client will wait while trying to connect before timing out
     * @param timeout Length of timeout, or 0 for none
     */
    public void setConnectTimeout(long timeout, TimeUnit unit);

    /**
     * Gets the maximum amount of time in milliseconds the HTTP client will spend trying to read new connections.
     * May not be available in all libraries.
     *
     * @return Read timeout in milliseconds
     */
    public int getReadTimeout();

    /**
     * Sets the maximum amount of time in milliseconds the HTTP client will spend trying to read new connections.
     * May not be available in all libraries.
     *
     * @param timeout Length of timeout, or 0 for none
     */
    public void setReadTimeout(long timeout, TimeUnit unit);

    /**
     * Gets the maximum amount of time in milliseconds the HTTP client will spend trying to write to new connections.
     * May not be available in all libraries.
     *
     * @return Write timeout in milliseconds
     */
    public int getWriteTimeout();

    /**
     * Gets the maximum amount of time in milliseconds the HTTP client will spend trying to write to new connections.
     * May not be available in all libraries.
     *
     * @param timeout Length of timeout, or 0 for none
     */
    public void setWriteTimeout(long timeout, TimeUnit unit);

    /** Checks if this adapter will follow redirects (3xx status codes) */
    public boolean isFollowingRedirects();

    /** Enables or disables following redirects */
    public void setFollowRedirects(boolean flag);

    /** Gets the effective HTTP proxy */
    public Proxy getProxy();

    /** Sets a proxy for the HTTP client to send requests through */
    public void setProxy(Proxy proxy);

    /** Gets the CookieManager being used by the HTTP client */
    public CookieManager getCookieManager();

    /**
     * Sets the CookieManager for the HTTP client to use. If the HTTP library does not support setting a CookieHandler
     * but <em>does</em> use the Java default handler, then it is recommended to use
     * {@link CookieHandler#setDefault(CookieHandler)} in addition.
     */
    public void setCookieManager(CookieManager manager);

    /**
     * Sets the username and password to use when a RestRequest is using HTTP Basic Authentication. These credentials
     * should not be revoked until {@link #deauthenticate()} is called.
     */
    public void authenticate(BasicAuthData authData);

    /**
     * Revokes the current HTTP Basic Authentication credentials. HTTP Basic Authentication should not be used until
     * {@link #authenticate(BasicAuthData)} is called.
     */
    public void deauthenticate();

    /**
     * Sets a default header. This will be sent with all future requests.
     * @param key Header name
     * @param value Header value
     * @see #removeDefaultHeader(String)
     * @see #getDefaultHeader(String)
     */
    public void setDefaultHeader(String key, String value);

    /**
     * Removes a default header
     * @param key The header's name
     * @see #setDefaultHeader(String, String)
     * @see #getDefaultHeader(String)
     */
    public void removeDefaultHeader(String key);

    /**
     * Gets the value of a default header
     * @param key The name of the header
     * @return The value of the header, or null if it does not exist
     * @see #removeDefaultHeader(String)
     * @see #setDefaultHeader(String, String)
     */
    public String getDefaultHeader(String key);

    /** Creates a copy of the default headers and returns it. */
    public Map<String, String> getDefaultHeaders();
}
