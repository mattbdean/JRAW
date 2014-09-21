package net.dean.jraw.http;

import net.dean.jraw.JrawUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is responsible for the lowest level of networking found in this library. It can execute RESTful HTTP requests
 * through the use of the {@code execute} methods
 */
public class HttpHelper {
    /** This cookie spec is used when executing a request that sets a "secure_session" cookie (aka /api/login) */
    public static final String COOKIE_SPEC_REDDIT = "redditMaxAge";
    /** This cookie spec is the default for all requests */
    public static final String COOKIE_SPEC_DEFAULT = CookieSpecs.BEST_MATCH;

    /** The HttpClient used to execute HTTP requests */
    private final HttpClient client;
    private final RequestConfig defaultConfig;

    private CookieStore cookieStore;

    /** The list of headers that will be sent with every HTTP request */
    private List<Header> defaultHeaders;

    /**
     * Instantiates a new HttpClientHelper and adds a given string as the value for the User-Agent header for every request
     *
     * @param userAgent The User-Agent to use for the HTTP requests
     */
    public HttpHelper(String userAgent) {
        this.cookieStore = new BasicCookieStore();
        this.defaultHeaders = new ArrayList<>();
        defaultHeaders.add(new BasicHeader("User-Agent", userAgent));

        // Register the RedditMaxAgeHandler
        CookieSpecProvider cookieSpecProvider = context -> {
            BrowserCompatSpec spec = new BrowserCompatSpec();
            spec.registerAttribHandler(ClientCookie.MAX_AGE_ATTR, new RedditMaxAgeHandler());
            return spec;
        };
        Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider>create()
                .register(COOKIE_SPEC_REDDIT, cookieSpecProvider)
                .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory())
                .build();

        this.defaultConfig = RequestConfig.custom()
                .setCookieSpec(COOKIE_SPEC_DEFAULT)
                .setConnectTimeout(10_000)
                .setConnectionRequestTimeout(10_000)
                .build();

        this.client = HttpClientBuilder.create()
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(defaultConfig)
                .setDefaultCookieSpecRegistry(r)
                .build();
    }

    /**
     * Conducts an HTTP request
     *
     * @param r The request to execute
     * @return A CloseableHttpResponse formed in the execution of the HTTP request
     * @throws NetworkException If the status code was not "200 OK"
     */
    public CloseableHttpResponse execute(HttpRequest r) throws NetworkException {
        String path = r.getPath();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        HttpRequestBase request = r.getVerb().getRequestObject(path);

        try {
            // Either not JSON data or no args at all
            if (r.getArgs() != null) {
                // Construct a List of BasicNameValue pairs from a Map
                List<BasicNameValuePair> params = r.getArgs().entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(),
                        entry.getValue())).collect(Collectors.toList());

                if (request instanceof HttpGet || request instanceof HttpDelete) {
                    // GET or DELETE
                    path += "?" + URLEncodedUtils.format(params, "UTF-8");

                    if (request instanceof HttpGet) request = new HttpGet(path);
                    if (request instanceof HttpDelete) request = new HttpDelete(path);
                } else {
                    // POST, PATCH, or PUT
                    ((HttpEntityEnclosingRequestBase) request).setEntity(new UrlEncodedFormEntity(params));
                }
            }

            // Add the default headers to the request
            for (Header h : defaultHeaders) {
                request.addHeader(h);
            }

            // Update the request config if not using the default cookie spec
            if (!r.getCookieSpec().equals(defaultConfig.getCookieSpec())) {
                RequestConfig requestConfig = RequestConfig.copy(defaultConfig)
                        .setCookieSpec(r.getCookieSpec())
                        .build();
                request.setConfig(requestConfig);
            }

            JrawUtils.logger().info("{} {}{} {}", r.getVerb().name(), r.getHostname(), path, request.getProtocolVersion());
            CloseableHttpResponse response = (CloseableHttpResponse) client.execute(new HttpHost(r.getHostname()), request);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new NetworkException(response.getStatusLine().getStatusCode());
            }

            return response;
        } catch (ConnectTimeoutException e) {
            throw new NetworkException("Connection timed out", e);
        } catch (IOException e) {
            JrawUtils.logger().error("Connection aborted", e);
            return null;
        }
    }

    /**
     * Gets the list of headers that will be included with every request
     *
     * @return A list of default headers
     */
    public List<Header> getDefaultHeaders() {
        return defaultHeaders;
    }

    /**
     * Gets the cookies stored while sending HTTP requests
     * @return The CookieStore for this HttpHelper
     */
    public CookieStore getCookieStore() {
        return cookieStore;
    }

    /**
     * Sets the value of the User-Agent header to send with every request
     * @param userAgent The User-Agent to use for the HTTP requests
     */
    public void setUserAgent(String userAgent) {
        for (Iterator<Header> it = defaultHeaders.iterator(); it.hasNext(); ) {
            Header currentHeader = it.next();
            if (currentHeader.getName().equals("User-Agent")) {
                it.remove();
                defaultHeaders.add(new BasicHeader("User-Agent", userAgent));
                return;
            }
        }

        // No User-Agent string was found
        defaultHeaders.add(new BasicHeader("User-Agent", userAgent));
    }

    /**
     * Gets the User-Agent header
     * @return The User-Agent to use for HTTP requests
     */
    public String getUserAgent() {
        for (Header h : defaultHeaders) {
            if (h.getName().equals("User-Agent")) {
                return h.getValue();
            }
        }

        return null;
    }
}
