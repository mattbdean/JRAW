package net.dean.jraw.http;

import net.dean.jraw.JrawUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is responsible for the lowest level of networking found in this library. It can execute RESTful HTTP requests
 * through the use of the {@code execute} methods
 */
public class HttpHelper {

    /** The HttpClient used to execute HTTP requests */
    private HttpClient client;

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
        this.client = HttpClientBuilder.create()
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).build())
                .build();
    }

    /**
     * Conducts an HTTP request
     *
     * @param b The RequestBuilder to use to execute the request
     * @return A CloseableHttpResponse formed in the execution of the HTTP request
     * @throws NetworkException If the status code was not "200 OK"
     */
    public CloseableHttpResponse execute(RequestBuilder b) throws NetworkException {
        String path = b.path;
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        HttpRequestBase request = b.verb.getRequestObject(path);

        try {
            if (b.isJson) {
                if (b.verb != HttpVerb.PUT && b.verb != HttpVerb.PATCH && b.verb != HttpVerb.POST) {
                    // Being exclusive would be shorter, but exclusive cryptographic patterns almost always fail
                    // Use query string
                    path += "?" + URLEncoder.encode(b.json.toString(), "UTF-8");
                } else {
                    // Use POST data or similar
                    StringEntity params = new StringEntity(b.json.toString());
                    request.addHeader("Content-Type", ContentType.JSON);
                    // POST, PATCH, or PUT
                    ((HttpEntityEnclosingRequestBase) request).setEntity(params);
                }
            } else {
                // Either not JSON data or no args at all
                if (b.args != null) {
                    // Construct a List of BasicNameValue pairs from a Map
                    List<BasicNameValuePair> params = b.args.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(),
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
            }

            // Add the default headers to the request
            for (Header h : defaultHeaders) {
                request.addHeader(h);
            }

            JrawUtils.logger().info("{} {}{} {}", b.verb.name(), b.hostname, path, request.getProtocolVersion());
            CloseableHttpResponse response = (CloseableHttpResponse) client.execute(new HttpHost(b.hostname), request);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new NetworkException(response.getStatusLine().getStatusCode());
            }

            return response;
        } catch (ConnectTimeoutException e) {
            throw new NetworkException("Connection timed out", e);
        } catch (IOException e) {
            e.printStackTrace();
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

    public static class RequestBuilder {
        private final HttpVerb verb;
        private final String hostname;
        private final String path;
        private Map<String, String> args;
        private JsonNode json;
        private boolean isJson;

        /**
         * Instantiates a new RequestBuilder
         * @param verb The HTTP verb to use (GET, POST, etc)
         * @param hostname The name of the URL's host. Do not include the scheme (ex: "http://")
         * @param path The path relative to the root directory of the host.
         */
        public RequestBuilder(HttpVerb verb, String hostname, String path) {
            this.verb = verb;
            this.hostname = hostname;
            this.path = path;
        }

        /**
         * @param args The arguments to use. If the verb uses form data (POST, PATCH, PUT), then these will be put into the
         *             request body. If the verb is "GET", then a query string will be appended to the path.
         * @return This builder
         */
        public RequestBuilder args(Map<String, String> args) {
            this.args = args;
            this.isJson = false;
            return this;
        }

        /**
         * Sets the JSON data to send. The HTTP verb must be PUT, POST, or PATCH.
         *
         * @param json The JSON data to send
         * @return This builder
         */
        public RequestBuilder json(JsonNode json) {
            this.json = json;
            this.isJson = true;
            return this;
        }
    }
}
