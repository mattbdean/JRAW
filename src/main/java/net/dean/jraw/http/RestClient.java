package net.dean.jraw.http;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import net.dean.jraw.JrawUtils;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class provides a way to send RESTful HTTP requests
 */
public abstract class RestClient<T extends RestResponse> {
    private final String host;
    private final String hostHttps;

    /** The OkHttpClient used to execute RESTful HTTP requests */
    protected OkHttpClient http;
    /** The CookieStore that will contain all the cookies saved by {@link #cookieJar} */
    protected CookieStore cookieJar;

    /** A list of Requests sent in the past */
    protected LinkedHashMap<T, LocalDateTime> history;
    /** A list of headers to be sent for request */
    protected Map<String, String> defaultHeaders;

    /**
     * Instantiates a new RestClient
     *
     * @param host      The host on which to operate
     * @param hostHttps The host on which to send HTTP requests secured with SSL on
     * @param userAgent The User-Agent header which will be sent with all requests
     */
    public RestClient(String host, String hostHttps, String userAgent) {
        this.host = host;
        this.hostHttps = hostHttps;
        this.http = new OkHttpClient();
        CookieManager manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        http.setCookieHandler(manager);
        this.cookieJar = manager.getCookieStore();
        this.history = new LinkedHashMap<>();
        this.defaultHeaders = new HashMap<>();
        defaultHeaders.put("User-Agent", userAgent);
    }

    public String getHost() {
        return host;
    }

    public String getHostHttps() {
        return hostHttps;
    }

    /**
     * Creates a new RequestBuilder whose host is {@link #getHost()}
     * @return A new RequestBuilder
     */
    public RequestBuilder request() {
        return request(false);
    }

    /**
     * Creates a new RequestBuilder
     * @param https If this request should be executed on {@link #getHostHttps()} with HTTPS
     * @return A new RequestBuilder
     */
    public RequestBuilder request(boolean https) {
        return addDefaultHeaders(new RequestBuilder(https ? hostHttps : host)
                .https(https));
    }

    private RequestBuilder addDefaultHeaders(RequestBuilder builder) {
        for (Map.Entry<String, String> entry : defaultHeaders.entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    /**
     * Executes a RESTful HTTP request
     *
     * @param r The request to execute
     * @return A RestResponse from the resulting response
     * @throws NetworkException If the status code was not "200 OK"
     */
    public T execute(Request r) throws NetworkException {
        try {
            Response response = http.newCall(r).execute();

            JrawUtils.logger().info("{} {}", r.method(), r.url());
            if (!response.isSuccessful()) {
                throw new NetworkException(String.format("Request not successful (got %s) : %s", response.code(), response));
            }

            T genericResponse = initResponse(http.newCall(r).execute());

            history.put(genericResponse, LocalDateTime.now());
            return genericResponse;
        } catch (IOException e) {
            throw new NetworkException("Could not execute the request: " + r);
        }
    }

    /**
     * Gets the User-Agent header for this RestClient
     * @return The value of the User-Agent header
     */
    public String getUserAgent() {
        return defaultHeaders.get("User-Agent");
    }

    /**
     * Sets the User-Agent header for this RestClient
     * @param userAgent The new User-Agent header
     */
    public void setUserAgent(String userAgent) {
        defaultHeaders.put("User-Agent", userAgent);
    }

    /**
     * This method is responsible for instantiating a new <T> (RestResponse or one of its subclasses)
     *
     * @param r The OkHttp response given
     * @return A new <T>
     */
    protected abstract T initResponse(Response r);
}
