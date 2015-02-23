package net.dean.jraw.http;

import net.dean.jraw.http.oauth.InvalidScopeException;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Provides a high-level overview of basic features of a HTTP client.
 */
public interface HttpClient extends NetworkAccessible {
    /**
     * Creates a new {@link HttpRequest.Builder} which uses HTTPS if {@link #isHttpsDefault()} is true, and with
     * {@link #getHttpAdapter()}'s {@link HttpAdapter#getDefaultHeaders() default headers} already applied. Other
     * properties may be applied.
     *
     * @return A new RestRequest Builder
     */
    public HttpRequest.Builder request();

    /**
     * Executes a HTTP request. HTTP Basic Authentication, rate limiting, request and response logging, and Content-Type
     * checking will be used if applicable. If using a rate limit, this method will block until it can obtain a ticket.
     *
     * @param request The request to send
     * @return A RestResponse modeling the response sent from the server
     * @throws InvalidScopeException If the client does not have the required OAuth scope for an endpoint. Takes
     *                               priority over NetworkException.
     * @throws NetworkException If the request returned had a failing status code (not 2XX).
     */
    public RestResponse execute(HttpRequest request) throws NetworkException, InvalidScopeException;

    /**
     * Gets the HttpLogger that will log the HTTP requests and responses that this class sends and receives.
     * @return This RestClient's HttpLogger
     */
    public HttpLogger getHttpLogger();

    /**
     * Gets when this HttpClient is logging HTTP requests.
     *
     * @return If requests are being logged
     * @see #getHttpLogger()
     */
    public LoggingMode getLoggingMode();

    /**
     * Sets when to log HTTP requests and responses.
     *
     * @see #getHttpLogger()
     */
    public void setLoggingMode(LoggingMode mode);

    /**
     * Sets whether or not {@link HttpRequest.Builder}s returned from {@link #request()} will be executed with HTTPS by
     * default. Note that this can be changed per request later.
     *
     * @param flag If HTTPS will be used by default
     * @see HttpRequest.Builder#https(boolean)
     */
    public void setHttpsDefault(boolean flag);

    /**
     * Checks to see if {@link HttpRequest.Builder}s returned from {@link #request()} will be executed with HTTPS. Note
     * that this can be changed per request later.
     * @return If HTTPS will be used by default
     * @see HttpRequest.Builder#https(boolean)
     */
    public boolean isHttpsDefault();

    /**
     * Notifies the client to log every response received. You can access this data by using {@link #getHistory()}.
     * @param flag Whether or not to save the HTTP responses received
     * @see #getHistory()
     */
    public void setSaveResponseHistory(boolean flag);

    /**
     * Notifies the client to log every response received. You can access this data by using {@link #getHistory()}. This
     * defaults to false.
     *
     * @return Checks if this client is saving response history
     * @see #getHistory()
     */
    public boolean isSavingResponseHistory();

    /**
     * Gets a map of responses to Dates, in which the Date refers to the time that the response was received. Will be
     * empty unless saving response history was enabled changed using {@link #setSaveResponseHistory(boolean)}.
     * @return The response history
     */
    public LinkedHashMap<RestResponse, Date> getHistory();

    /** Gets the default value of the User-Agent header */
    public String getUserAgent();

    /** Sets the default value of the User-Agent header */
    public void setUserAgent(UserAgent userAgent);
}
