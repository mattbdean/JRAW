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
public interface HttpAdapter<T> {
    /**
     * Executes an HTTP request. A fully functional HttpAdapter should support all features described by
     * {@link HttpRequest}. GET, POST, PATCH, PUT, and DELETE should all be supported, as well as HTTP Basic
     * Authentication and sending headers.
     *
     * @return A RestResponse from the resulting request
     * @throws IOException If an implementation-specific error occurred.
     */
    RestResponse execute(HttpRequest request) throws IOException;

    /**
     * Gets the time in milliseconds the HTTP client will wait while trying to connect before timing out
     * @return Connection timeout in milliseconds
     */
    int getConnectTimeout();

    /**
     * Sets the time in milliseconds the HTTP client will wait while trying to connect before timing out
     * @param timeout Length of timeout, or 0 for none
     */
    void setConnectTimeout(long timeout, TimeUnit unit);

    /**
     * Gets the maximum amount of time in milliseconds the HTTP client will spend trying to read new connections.
     * May not be available in all libraries.
     *
     * @return Read timeout in milliseconds
     */
    int getReadTimeout();

    /**
     * Sets the maximum amount of time in milliseconds the HTTP client will spend trying to read new connections.
     * May not be available in all libraries.
     *
     * @param timeout Length of timeout, or 0 for none
     */
    void setReadTimeout(long timeout, TimeUnit unit);

    /**
     * Gets the maximum amount of time in milliseconds the HTTP client will spend trying to write to new connections.
     * May not be available in all libraries.
     *
     * @return Write timeout in milliseconds
     */
    int getWriteTimeout();

    /**
     * Gets the maximum amount of time in milliseconds the HTTP client will spend trying to write to new connections.
     * May not be available in all libraries.
     *
     * @param timeout Length of timeout, or 0 for none
     */
    void setWriteTimeout(long timeout, TimeUnit unit);

    /** Checks if this adapter will follow redirects (3xx status codes) */
    boolean isFollowingRedirects();

    /** Enables or disables following redirects */
    void setFollowRedirects(boolean flag);

    /** Gets the effective HTTP proxy */
    Proxy getProxy();

    /** Sets a proxy for the HTTP client to send requests through */
    void setProxy(Proxy proxy);

    /** Gets the CookieManager being used by the HTTP client */
    CookieManager getCookieManager();

    /**
     * Sets the CookieManager for the HTTP client to use. If the HTTP library does not support setting a CookieHandler
     * but <em>does</em> use the Java default handler, then it is recommended to use
     * {@link CookieHandler#setDefault(CookieHandler)} in addition.
     */
    void setCookieManager(CookieManager manager);

    /** Gets a not-null, mutable Map of the headers that will be sent with every new HTTP request. */
    Map<String, String> getDefaultHeaders();

    /**
     * Gets the object used to send HTTP requests for this adapter. For example, an HttpAdapter that utilizes OkHttp
     * will return an OkHttpClient.
     */
    T getNativeClient();
}
