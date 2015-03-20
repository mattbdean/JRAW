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
public abstract class RestClient implements HttpClient {
    /** The HttpAdapter used to send HTTP requests */
    protected final HttpAdapter httpAdapter;
    private final String defaultHost;
    /** The CookieStore that will contain all the cookies saved by {@link #httpAdapter} */
    protected final HttpLogger logger;
    /** A list of Requests sent in the past */
    protected final LinkedHashMap<RestResponse, Date> history;
    private RateLimiter rateLimiter;
    private boolean useHttpsDefault;
    private boolean saveResponseHistory;
    private LoggingMode loggingMode;

    /**
     * Instantiates a new RestClient
     *  @param defaultHost The host that will be applied to every {@link HttpRequest.Builder} returned by
     *                    {@link #request()}
     * @param userAgent The default value of the User-Agent header
     * @param requestsPerMinute The amount of HTTP requests that can be sent in one minute. A value greater than 0 will
     */
    public RestClient(HttpAdapter httpAdapter, String defaultHost, UserAgent userAgent, int requestsPerMinute) {
        this.httpAdapter = httpAdapter;
        this.defaultHost = defaultHost;
        this.saveResponseHistory = false;
        this.logger = new HttpLogger(JrawUtils.logger());
        this.history = new LinkedHashMap<>();
        this.useHttpsDefault = false;
        // Never log by default
        this.loggingMode = LoggingMode.NEVER;
        setUserAgent(userAgent);
        setRatelimit(requestsPerMinute);
    }

    /**
     * Gets the host that will be used by default when creating new RestRequest.Builders.
     * @return The default host
     */
    public String getDefaultHost() {
        return defaultHost;
    }

    /** Gets the current amount of times a request can be executed in one minute. */
    public double getCurrentRatelimit() {
        return rateLimiter.getRate() * 60;
    }

    /**
     * Sets the amount of requests per minute this RestClient will allow to be executed. If there has been more than a
     * certain amount of requests in the last minute, this class will wait to execute the next request in order to
     * minimize the chance of Reddit IP banning this client or simply returning a 403 Forbidden.
     *
     * @param requestsPerMinute The amount of HTTP requests that can be sent in one minute. Must not be less than 1
     */
    protected void setRatelimit(int requestsPerMinute) {
        if (requestsPerMinute < 1)
            throw new IllegalArgumentException("requestsPerMinute cannot be less than 1");
        double perSecond = requestsPerMinute / 60.0;
        if (rateLimiter == null)
            rateLimiter = RateLimiter.create(perSecond);
        else
            rateLimiter.setRate(perSecond);
    }

    @Override
    public boolean isHttpsDefault() {
        return useHttpsDefault;
    }

    @Override
    public void setHttpsDefault(boolean useHttpsDefault) {
        this.useHttpsDefault = useHttpsDefault;
    }

    @Override
    public HttpRequest.Builder request() {
        HttpRequest.Builder builder = new HttpRequest.Builder()
                .host(defaultHost)
                .https(useHttpsDefault);
        for (Map.Entry<String, String> entry: httpAdapter.getDefaultHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }

        return builder;
    }

    @Override
    public RestResponse execute(HttpRequest request) throws NetworkException {
        Headers.Builder builder = request.getHeaders().newBuilder();
        for (Map.Entry<String, String> defaultHeader : httpAdapter.getDefaultHeaders().entrySet()) {
            builder.add(defaultHeader.getKey(), defaultHeader.getValue());
        }

        // Try to get a ticket without waiting
        if (!rateLimiter.tryAcquire()) {
            // Could not get a ticket immediately, block until we can
            double time = rateLimiter.acquire();
            // We're not sure about whether this will fail, so be on the safe side
            if (loggingMode == LoggingMode.ALWAYS) {
                JrawUtils.logger().info("Slept for {} seconds", time);
            }
        }

        try {
            // We're always logging, so we can be proactive and log the request before it's executed
            if (loggingMode == LoggingMode.ALWAYS)
                logger.log(request);

            RestResponse response = httpAdapter.execute(request);
            // Log the response as well
            if (loggingMode == LoggingMode.ALWAYS)
                logger.log(response);

            // Log the request and response if it was not successful
            if (loggingMode == LoggingMode.ON_FAIL && !response.isSuccessful()) {
                logger.log(request, false);
                logger.log(response);
            }

            if (!response.isSuccessful())
                throw new NetworkException(response);

            if (!JrawUtils.isEqual(response.getType(), request.getExpectedType())) {
                throw new IllegalStateException(String.format("Expected Content-Type ('%s/%s') did not match actual Content-Type ('%s/%s')",
                        request.getExpectedType().type(), request.getExpectedType().subtype(),
                        response.getType().type(), response.getType().subtype()));
            }

            if (saveResponseHistory)
                history.put(response, new Date());
            return response;
        } catch (IOException e) {
            throw new RuntimeException("Could not execute the request: " + request, e);
        }
    }

    @Override
    public String getUserAgent() {
        return httpAdapter.getDefaultHeaders().get("User-Agent");
    }

    @Override
    public void setUserAgent(UserAgent userAgent) {
        httpAdapter.getDefaultHeaders().put("User-Agent", userAgent.toString());
    }

    @Override
    public boolean isSavingResponseHistory() {
        return saveResponseHistory;
    }

    @Override
    public void setSaveResponseHistory(boolean saveResponseHistory) {
        this.saveResponseHistory = saveResponseHistory;
    }

    @Override
    public LoggingMode getLoggingMode() {
        return loggingMode;
    }

    @Override
    public void setLoggingMode(LoggingMode mode) {
        this.loggingMode = mode;
    }

    @Override
    public LinkedHashMap<RestResponse, Date> getHistory() {
        return history;
    }

    @Override
    public HttpLogger getHttpLogger() {
        return logger;
    }

    @Override
    public HttpAdapter getHttpAdapter() {
        return httpAdapter;
    }
}
