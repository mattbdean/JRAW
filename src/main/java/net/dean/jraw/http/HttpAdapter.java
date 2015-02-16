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
     * Executes an HTTP request. A fully functional HttpAdapter should support all features described by
     * {@link HttpRequest}. GET, POST, PATCH, PUT, and DELETE should all be supported, as well as HTTP Basic
     * Authentication and sending headers.
     *
     * @return A RestResponse from the resulting request
     * @throws IOException If an implementation-specific error occurred.
     */
    public RestResponse execute(HttpRequest request) throws IOException;

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

    /** Gets a not-null, mutable Map of the headers that will be sent with every new HTTP request. */
    public Map<String, String> getDefaultHeaders();
}
