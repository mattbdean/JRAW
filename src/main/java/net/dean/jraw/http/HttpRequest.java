package net.dean.jraw.http;

import com.google.common.net.MediaType;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.internal.Util;
import com.squareup.okhttp.internal.http.HttpMethod;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This immutable class represents a HTTP request.
 */
public final class HttpRequest {
    private final String method;
    private final URL url;
    private final RequestBody body;
    private final Headers headers;
    private final BasicAuthData basicAuthData;
    private final MediaType expectedMediaType;
    private final String[] sensitiveArgs;

    /**
     * Creates a RestRequest from the given URL
     * @param method The HTTP verb to execute this RestRequest with
     * @param url A valid URL
     * @param formArgs An optional array of Strings to send as form data. See {@link JrawUtils#mapOf(Object...)} for more
     *                 info.
     * @return A RestRequest that represents the given URL
     */
    public static HttpRequest from(String method, URL url, Object... formArgs) {
        return Builder.from(method, url, formArgs).build();
    }

    private HttpRequest(Builder b) {
        this.method = b.method;
        this.url = b.url;
        this.body = b.body;
        this.headers = b.headers.build();
        this.basicAuthData = b.basicAuthData;
        this.expectedMediaType = b.expectedMediaType;
        this.sensitiveArgs = b.sensitiveArgs;
    }

    /** Get the HTTP verb (GET, POST, etc.) */
    public String getMethod() {
        return method;
    }

    public URL getUrl() {
        return url;
    }

    public RequestBody getBody() {
        return body;
    }

    public Headers getHeaders() {
        return headers;
    }

    public BasicAuthData getBasicAuthData() {
        return basicAuthData;
    }

    /**
     * Gets the MediaType that the response is expected to have
     */
    public MediaType getExpectedType() {
        return expectedMediaType;
    }

    /**
     * Returns true if the data is not null and it is valid
     * @see BasicAuthData#isValid()
     */
    public boolean isUsingBasicAuth() {
        return basicAuthData != null && basicAuthData.isValid();
    }

    /**
     * Gets an array of the names of sensitive form data arguments. The values of these arguments should be concealed.
     * Only applies to a {@code application/x-www-form-urlencoded} request body.
     *
     * @return A copy of array consisting of keys whose values are sensitive
     */
    public String[] getSensitiveArgs() {
        if (sensitiveArgs == null) {
            return new String[0];
        }
        String[] localCopy = new String[sensitiveArgs.length];
        System.arraycopy(sensitiveArgs, 0, localCopy, 0, sensitiveArgs.length);
        return localCopy;
    }

    @Override
    public String toString() {
        return "HttpRequest {" +
                "method='" + method + '\'' +
                ", url=" + url +
                ", expectedMediaType=" + expectedMediaType +
                ", headers=" + headers +
                ", body=" + body +
                '}';
    }

    /**
     * This class provides an interface to create objects that model HTTP requests. Each property method will return
     * itself for method chaining. Borrowed heavily from OkHttp's Request.Builder class.
     */
    public static final class Builder {
        /** This Pattern will match a URI parameter. For example, /api/{param1}/{param2} */
        private static final Pattern PATH_PARAM_PATTERN = Pattern.compile("\\{(.*?)\\}");
        private static final String DEFAULT_VERB = "GET";
        private static final String DEFAULT_PROTOCOL = "http";
        private static final String DEFAULT_PATH = "/";
        private static final MediaType DEFAULT_EXPECTED_TYPE = MediaTypes.JSON.type();

        private String method;

        // Created when build() is called
        private transient URL url;

        // URL properties
        private String protocol;
        private String host;
        private String path;
        private String[] pathParams;
        private Map<String, String> query;

        // Body
        private RequestBody body;

        // Extras
        private Headers.Builder headers;
        private BasicAuthData basicAuthData;
        private MediaType expectedMediaType;
        private String[] sensitiveArgs;

        /**
         * Creates a new Builder that will result in a RestRequest whose URL will match the one given
         * @param method The HTTP verb
         * @param url The URL to parse
         * @param formArgs An optional array of Strings to send as form data
         * @return A new Builder that represents the given URL
         */
        public static Builder from(String method, URL url, Object... formArgs) {
            if (!url.getProtocol().matches("http[s]?")) {
                throw new IllegalArgumentException("Only HTTP(S) supported");
            }

            Map<String, String> query = new HashMap<>();

            if (url.getQuery() != null) {
                String[] queryKeysValues = JrawUtils.urlDecode(url.getQuery()).split("&");
                for (String keyValuePair : queryKeysValues) {
                    String[] parts = keyValuePair.split("=");
                    query.put(parts[0], parts[1]);
                }
            }

            Builder b = new Builder()
                    .https(url.getProtocol().equals("https"))
                    .host(url.getHost())
                    .path(url.getPath())
                    .query(query);
            if (formArgs.length != 0) {
                b.method(method, JrawUtils.mapOf(formArgs));
            }
            return b;
        }

        public Builder() {
            this.headers = new Headers.Builder();
        }

        /** Makes this request a GET */
        public Builder get() { return method("GET", (RequestBody) null); }

        /** Makes this request a DELETE */
        public Builder delete() { return method("DELETE", (RequestBody) null); }

        /** Makes this request a HEAD */
        public Builder head() { return method("HEAD", (RequestBody) null); }

        /** Makes this request a POST with no body */
        public Builder post() { return method("POST", (RequestBody) null); }
        /** Makes this request a POST with a {@code x-www-form-urlencoded} body */
        public Builder post(Map<String, String> urlEncodedForm) { return method("POST", urlEncodedForm); }
        /** Makes this request a POST with a given body. */
        public Builder post(RequestBody body) { return method("POST", body); }

        /** Makes this request a PUT with no body */
        public Builder put() { return method("PUT", (RequestBody) null); }
        /** Makes this request a PUT with a {@code x-www-form-urlencoded} body */
        public Builder put(Map<String, String> urlEncodedForm) { return method("PUT", urlEncodedForm); }
        /** Makes this request a PUT with a given body. */
        public Builder put(RequestBody body) { return method("PUT", body); }

        /** Makes this request a PATCH with no body */
        public Builder patch() { return method("PATCH", (RequestBody) null); }
        /** Makes this request a PATCH with a {@code x-www-form-urlencoded} body */
        public Builder patch(Map<String, String> urlEncodedForm) { return method("PATCH", urlEncodedForm); }
        /** Makes this request a PATCH with a given body. */
        public Builder patch(RequestBody body) { return method("PATCH", body); }

        /**
         * Sets the HTTP verb to use for this request. A full list can be found in
         * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">RFC 2616</a>. The Content-Type for this
         * request will be {@code application/x-www-form-urlencoded}.
         *
         * @param method The name of the method (GET, POST, etc.)
         * @param urlEncodedForm A map that will be encoded using UTF-8. Can be null.
         * @return This Builder
         */
        public Builder method(String method, Map<String, String> urlEncodedForm) {
            RequestBody body = null;
            if (urlEncodedForm != null && HttpMethod.permitsRequestBody(method.toUpperCase())) {
                body = FormEncodedBodyBuilder.with(urlEncodedForm);
            }

            return method(method, body);
        }

        /**
         * Sets the HTTP verb to use for this request. A full list can be found in
         * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html">RFC 2616</a>.
         *
         * @param method The name of the method (GET, POST, etc.)
         * @param body The request body. Can be null.
         * @return This Bulider
         */
        public Builder method(String method, RequestBody body) {
            // Adapted from com.squareup.okhttp.Request.Builder.method(String, RequestBody)
            if (method == null || method.length() == 0) {
                throw new IllegalArgumentException("Missing HTTP method");
            }
            if (body != null && !HttpMethod.permitsRequestBody(method)) {
                throw new IllegalArgumentException("Request body not allowed for " + method);
            }
            if (body == null && HttpMethod.permitsRequestBody(method)) {
                body = RequestBody.create(null, Util.EMPTY_BYTE_ARRAY);
            }
            this.method = method;
            this.body = body;
            return this;
        }

        /** Enables or disables HTTPS */
        public Builder https(boolean https) {
            this.protocol = https ? "https" : "http";
            return this;
        }

        /** Sets the target URL's host. For example, "github.com." */
        public Builder host(String host) {
            this.host = host;
            return this;
        }

        /**
         * Sets the URL's path. For example, "/thatJavaNerd/JRAW." Positional path parameters are supported, so if
         * {@code path} was "/api/{resource}" and {@code params} was a one-element array consisting of "foo", then the
         * resulting path would be "/api/foo."
         *
         * @param path The path. If null, "/" will be used.
         * @param params Optional positional path parameters
         * @return This Builder
         */
        public Builder path(String path, String... params) {
            if (path == null)
                path = DEFAULT_PATH;
            this.path = path;
            this.pathParams = params;
            return this;
        }

        /** Calls {@link #path(String, String...)} with {@code e.getEndpoint().getUri()} */
        public Builder endpoint(Endpoints e, String... pathParams) {
            return path(e.getEndpoint().getUri(), pathParams);
        }

        /**
         * Sets the arguments to be used in the query string. This will be appended after the path in the format
         * {@code ?key=value&foo=bar}.
         *
         * @param keysAndValues Passed to {@link JrawUtils#mapOf(Object...)}
         * @return This Builder
         */
        public Builder query(String... keysAndValues) {
            return query(JrawUtils.mapOf((Object[]) keysAndValues));
        }

        /**
         * Sets the arguments to be used in the query string. This will be appended after the path in the format
         * {@code ?key=value&foo=bar}.
         * @return This Builder
         */
        public Builder query(Map<String, String> query) {
            this.query = query;
            return this;
        }

        /** Sets a header */
        public Builder header(String name, String value) {
            this.headers.set(name, value);
            return this;
        }

        /** Removes a header */
        public Builder removeHeader(String name) {
            this.headers.removeAll(name);
            return this;
        }

        /**
         * Sets how the server should go about finding a cached version of this request
         * @param cacheControl The value of the Cache-Control header. If null or empty, the the header is removed.
         */
        public Builder cacheControl(CacheControl cacheControl) {
            String value;
            if (cacheControl == null || (value = cacheControl.toString()).isEmpty()) {
                return removeHeader("Cache-Control");
            }

            return header("Cache-Control", value);
        }

        /**
         * Sets the username and password to be used with HTTP Basic Authentication. HTTPS must be enabled before
         * {@link #build()} is called in order for to avoid an exception.
         *
         * @see #https(boolean)
         */
        public Builder basicAuth(BasicAuthData data) {
            this.basicAuthData = data;
            return this;
        }

        /** Sets the expected value of the response's Content-Type header */
        public Builder expected(MediaType type) {
            this.expectedMediaType = type;
            return this;
        }

        /**
         * Notes that the the values of the given keys should be kept secret. Only applies if the body is
         * {@code x-www-form-urlencoded}. Note that this is honored by {@link HttpLogger}, third party classes might
         * not. If you are unsure about security, it is best to disable logging HTTP requests and enable HTTPS by
         * default.
         *
         * @see HttpLogger#log(HttpRequest)
         * @see HttpClient#setHttpsDefault(boolean)
         */
        public Builder sensitiveArgs(String... args) {
            this.sensitiveArgs = args == null ? new String[0] : args;
            return this;
        }

        /**
         * Compiles all the data given from other property methods into a {@link HttpRequest}
         *
         * @throws IllegalArgumentException If a malformed URL is created, HTTP Basic Authentication is enabled but
         *                                  HTTPS is not, or an essential piece of information is missing, like the
         *                                  host.
         */
        public HttpRequest build() {
            // Set defaults
            if (method == null)
                method = DEFAULT_VERB;
            if (protocol == null || protocol.isEmpty())
                protocol = DEFAULT_PROTOCOL;
            if (expectedMediaType == null)
                expectedMediaType = DEFAULT_EXPECTED_TYPE;
            if (path == null)
                path = DEFAULT_PATH;

            // Check for errors
            if (basicAuthData != null && !protocol.equals("https"))
                throw new IllegalArgumentException("Refusing to use HTTP Basic Auth without HTTPS");
            if (host == null || host.isEmpty())
                throw new IllegalArgumentException("Missing host");

            // Substitute path parameters
            String effectivePath = path;
            if (pathParams != null && pathParams.length != 0) {
                effectivePath = substitutePathParameters(path, pathParams);
            }
            // Append query string
            if (query != null && query.size() != 0) {
                effectivePath += buildQueryString(query);
            }

            try {
                this.url = new URL(protocol, host, effectivePath);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(String.format("Malformed URL by new java.net.URL(protocol=%s, host=%s, file=%s)",
                        protocol, host, effectivePath), e);
            }

            return new HttpRequest(this);
        }

        private static String buildQueryString(Map<String, String> query) {
            if (query.size() == 0) {
                return "";
            }
            StringBuilder url = new StringBuilder();

            // Create a query string, such as "?foo=bar&key1=val1"
            url.append("?");
            for (Iterator<Map.Entry<String, String>> it = query.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> entry = it.next();

                url.append(JrawUtils.urlEncode(entry.getKey()));
                url.append("=");
                url.append(JrawUtils.urlEncode(entry.getValue()));
                if (it.hasNext()) {
                    // More parameters are coming, add a separator
                    url.append("&");
                }
            }

            return url.toString();
        }

        /** Implements the functionality described in {@link #path(String, String...)}. */
        private static String substitutePathParameters(String path, String[] positionalArgs) {
            List<String> pathParams = parsePathParams(path);
            if (pathParams.size() != positionalArgs.length) {
                // Different amount of parameters
                throw new IllegalArgumentException(String.format("URL parameter size mismatch. Expecting %s, got %s",
                        pathParams.size(), positionalArgs.length));
            }

            String updatedUri = path;
            Matcher m = null;
            for (String arg : positionalArgs) {
                if (m == null) {
                    // Create on first use
                    m = PATH_PARAM_PATTERN.matcher(updatedUri);
                } else {
                    // Reuse the Matcher
                    m.reset(updatedUri);
                }
                updatedUri = m.replaceFirst(arg);
            }

            return updatedUri;
        }

        /** Finds all parameters in the given path */
        private static List<String> parsePathParams(String path) {
            List<String> params = new ArrayList<>();
            Matcher matcher = PATH_PARAM_PATTERN.matcher(path);
            while (matcher.find()) {
                params.add(matcher.group());
            }

            return params;
        }
    }
}
