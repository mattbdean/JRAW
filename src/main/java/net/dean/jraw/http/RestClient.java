package net.dean.jraw.http;

import com.google.common.util.concurrent.RateLimiter;
import com.squareup.okhttp.Headers;
import net.dean.jraw.JrawUtils;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class provides a high-level API to send REST-oriented HTTP requests with.
 */
public abstract class RestClient implements NetworkAccessible {
    /** The HttpAdapter used to send HTTP requests */
    protected final HttpAdapter httpAdapter;
    private final String defaultHost;
    /** The CookieStore that will contain all the cookies saved by {@link #httpAdapter} */
    protected final HttpLogger logger;
    /** A list of Requests sent in the past */
    protected final LinkedHashMap<RestResponse, Date> history;
    private RateLimiter rateLimiter;
    private boolean useHttpsDefault;
    private boolean enforceRatelimit;
    private boolean saveResponseHistory;
    private boolean requestLogging;

    /**
     * Instantiates a new RestClient
     *
     * @param defaultHost The host on which to operate
     * @param userAgent The User-Agent header which will be sent with all requests
     * @param requestsPerMinute The amount of HTTP requests that can be sent in one minute. A value greater than 0 will
     *                          enable rate limit enforcing, one less than or equal to 0 will disable it.
     */
    public RestClient(HttpAdapter httpAdapter, String defaultHost, String userAgent, int requestsPerMinute) {
        this.httpAdapter = httpAdapter;
        this.defaultHost = defaultHost;
        this.saveResponseHistory = false;
        this.logger = new HttpLogger(JrawUtils.logger());
        this.requestLogging = false;
        this.history = new LinkedHashMap<>();
        this.useHttpsDefault = false;
        setUserAgent(userAgent);
        setEnforceRatelimit(requestsPerMinute);
    }

    /**
     * Gets the host that will be used by default when creating new RestRequest.Builders.
     * @return The default host
     */
    public String getDefaultHost() {
        return defaultHost;
    }

    /**
     * Checks to see if RequestBuilders returned from {@link #request()} will be executed with HTTPS. Note that this can
     * be changed per request later.
     * @return If HTTPS will be used by default
     */
    public boolean isHttpsDefault() {
        return useHttpsDefault;
    }

    /**
     * Sets whether or not RequestBuilders returned from {@link #request()} will be executed with HTTPS. Note that this
     * can be changed per request later
     * @param useHttpsDefault If HTTPS will be used by default
     */
    public void setHttpsDefault(boolean useHttpsDefault) {
        this.useHttpsDefault = useHttpsDefault;
    }

    /**
     * Creates a new {@link RestRequest.Builder} whose host is {@link #getDefaultHost()} and uses HTTPS if
     * {@link #isHttpsDefault()} is true, and with {@link #getHttpAdapter()}'s
     * {@link HttpAdapter#getDefaultHeaders() default headers} already applied.
     *
     * @return A new RestRequest Builder
     */
    public RestRequest.Builder request() {
        RestRequest.Builder builder = new RestRequest.Builder()
                .host(defaultHost)
                .https(useHttpsDefault);
        for (Map.Entry<String, String> entry: httpAdapter.getDefaultHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        return builder;
    }

    /**
     * Whether to automatically manage the execution of HTTP requests based on time (enabled by default). If there has
     * been more than a certain amount of requests in the last minute (30 for normal API, 60 for OAuth), this class will
     * wait to execute the next request in order to minimize the chance of Reddit IP banning this client or simply
     * returning a 403 Forbidden.
     *
     * @param requestsPerMinute The amount of HTTP requests that can be sent in one minute. A value greater than 0 will
     *                          enable rate limit enforcing, one less than or equal to 0 will disable it.
     */
    public void setEnforceRatelimit(int requestsPerMinute) {
        this.enforceRatelimit = requestsPerMinute > 0;
        this.rateLimiter = enforceRatelimit ? RateLimiter.create((double) requestsPerMinute / 60) : null;
    }

    /**
     * Checks if the rate limit is being enforced. If true, then thread that {@link #execute(RestRequest)} is called on
     * will block until enough time has passed
     * @return If the rate limit is being enforced.
     */
    public boolean isEnforcingRatelimit() {
        return enforceRatelimit;
    }

    /**
     * Executes a HTTP request. HTTP Basic Authentication, rate limiting, request and response logging, and Content-Type
     * checking will be used if applicable. If using a rate limit, this method will block until it can obtain a ticket.
     *
     * @param request The request to send
     * @return A RestResponse modeling the response sent from the server
     * @throws NetworkException
     */
    public RestResponse execute(RestRequest request) throws NetworkException {
        if (request.isUsingBasicAuth()) {
            httpAdapter.authenticate(request.getBasicAuthData());
        }

        Headers.Builder builder = request.getHeaders().newBuilder();
        for (Map.Entry<String, String> defaultHeader : httpAdapter.getDefaultHeaders().entrySet()) {
            builder.add(defaultHeader.getKey(), defaultHeader.getValue());
        }

        if (enforceRatelimit) {
            // Try to get a ticket without waiting
            if (!rateLimiter.tryAcquire()) {
                // Could not get a ticket immediately, block until we can
                double time = rateLimiter.acquire();
                if (requestLogging) {
                    JrawUtils.logger().info("Slept for {} seconds", time);
                }
            }
        }

        try {
            if (requestLogging)
                logger.log(request);

            RestResponse response = httpAdapter.execute(request);
            if (requestLogging)
                logger.log(response);

            if (!JrawUtils.isEqual(response.getType(), request.getExpectedType())) {
                throw new NetworkException(String.format("Expected Content-Type ('%s/%s') did not match actual Content-Type ('%s/%s')",
                        request.getExpectedType().type(), request.getExpectedType().subtype(),
                        response.getType().type(), response.getType().subtype()));
            }

            if (saveResponseHistory)
                history.put(response, new Date());
            return response;
        } catch (IOException e) {
            throw new NetworkException("Could not execute the request: " + request, e);
        } finally {
            httpAdapter.deauthenticate();
        }
    }

    /**
     * Gets the value of the User-Agent header for this RestClient
     * @return The value of the User-Agent header
     */
    public String getUserAgent() {
        return httpAdapter.getDefaultHeader("User-Agent");
    }

    /**
     * Sets the User-Agent header for this RestClient
     * @param userAgent The new User-Agent header
     */
    public void setUserAgent(String userAgent) {
        httpAdapter.setDefaultHeader("User-Agent", userAgent);
    }

    /**
     * Notifies the client to log every response received. You can access this data by using {@link #getHistory()}. This
     * defaults to false.
     * 
     * @return Checks if this client is saving response history
     */
    public boolean isSavingResponseHistory() {
        return saveResponseHistory;
    }

    /**
     * Notifies the client to log every response received. You can access this data by using {@link #getHistory()}.
     * @param saveResponseHistory Whether or not to save the HTTP responses received
     */
    public void setSaveResponseHistory(boolean saveResponseHistory) {
        this.saveResponseHistory = saveResponseHistory;
    }

    /**
     * Checks if this RestClient is logging HTTP requests using SLF4J. The full URL, form data, and time spent sleeping
     * are displayed also (unless it has been marked as sensitive). Enabled by default.
     *
     * @return If requests are being logged
     * @see RestRequest#getSensitiveArgs()
     */
    public boolean isLoggingRequests() {
        return requestLogging;
    }

    /**
     * Sets whether or not to log HTTP requests. The full URL, form data, and time spent sleeping are displayed also.
     * Enabled by default.
     *
     * @param requestLogging Whether or not to log requests
     * @see RestRequest#getSensitiveArgs()
     */
    public void setRequestLoggingEnabled(boolean requestLogging) {
        this.requestLogging = requestLogging;
    }

    /**
     * Gets a map of responses to LocalDateTimes, in which the LocalDateTime refers to the time that the response was
     * received. Will be empty unless changed using {@link #setSaveResponseHistory(boolean)}.
     * @return The response history
     */
    public LinkedHashMap<RestResponse, Date> getHistory() {
        return history;
    }

    /**
     * Gets the HttpLogger that will log the HTTP requests and responses that this class sends and receives.
     * @return This RestClient's HttpLogger
     */
    public HttpLogger getHttpLogger() {
        return logger;
    }

    @Override
    public HttpAdapter getHttpAdapter() {
        return httpAdapter;
    }
}
