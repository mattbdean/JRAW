package net.dean.jraw.http;

import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
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
 * This class represents a HTTP request. Its API is designed to be as simple as possible, and is therefore based off of
 * OkHttp's {@code Request} class.
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

    public MediaType getExpectedType() {
        return expectedMediaType;
    }

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

    /**
     * This class provides an interface to create objects that model HTTP requests. Borrowed heavily from OkHttp's
     * Request.Builder class.
     */
    public static final class Builder {
        /** This Pattern will match a URI parameter. For example, /api/{param1}/{param2} */
        private static final Pattern PATH_PARAM_PATTERN = Pattern.compile("\\{(.*?)\\}");

        private String method;

        // Created when build() is called
        private transient URL url;

        // URL properties
        private String protocol;
        private String host;
        private String path;
        private String[] pathParams;
        private Map<String, String> query;

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
            get();
            https(false);
            this.headers = new Headers.Builder();
            this.expectedMediaType = MediaTypes.JSON.type();
        }

        public Builder get() { return method("GET", (RequestBody) null); }

        public Builder delete() { return method("DELETE", (RequestBody) null); }

        public Builder head() { return method("HEAD", (RequestBody) null); }

        public Builder post() { return method("POST", (RequestBody) null); }
        public Builder post(Map<String, String> urlEncodedForm) { return method("POST", urlEncodedForm); }
        public Builder post(RequestBody body) { return method("POST", body); }

        public Builder put() { return method("PUT", (RequestBody) null); }
        public Builder put(Map<String, String> urlEncodedForm) { return method("PUT", urlEncodedForm); }
        public Builder put(RequestBody body) { return method("PUT", body); }

        public Builder patch() { return method("PATCH", (RequestBody) null); }
        public Builder patch(Map<String, String> urlEncodedForm) { return method("PATCH", urlEncodedForm); }
        public Builder patch(RequestBody body) { return method("PATCH", body); }

        public Builder method(String method, Map<String, String> urlEncodedForm) {
            RequestBody body = null;
            if (urlEncodedForm != null && HttpMethod.permitsRequestBody(method.toUpperCase())) {
                FormEncodingBuilder formBuilder = new FormEncodingBuilder();
                for (Map.Entry<String, String> entry : urlEncodedForm.entrySet()) {
                    formBuilder.add(entry.getKey(), entry.getValue());
                }
                body = formBuilder.build();
            }

            return method(method, body);
        }

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

        public Builder https(boolean https) {
            this.protocol = https ? "https" : "http";
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder path(String path, String... params) {
            this.path = path;
            return pathParams(params);
        }

        public Builder pathParams(String... params) {
            this.pathParams = params;
            return this;
        }

        public Builder endpoint(Endpoints e, String... pathParams) {
            if (e == null) {
                throw new NullPointerException("Endpoint cannot be null");
            }
            this.path = e.getEndpoint().getUri();
            pathParams(pathParams);
            return this;
        }

        public Builder query(String... keysAndValues) {
            return query(JrawUtils.mapOf((Object[]) keysAndValues));
        }

        public Builder query(Map<String, String> query) {
            this.query = query;
            return this;
        }

        /** Replaces all values of the given header with the given value */
        public Builder header(String name, String value) {
            this.headers.set(name, value);
            return this;
        }

        /** Removes all values of a header */
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

        public Builder basicAuth(BasicAuthData data) {
            this.basicAuthData = data;
            return this;
        }

        public Builder expected(MediaType type) {
            this.expectedMediaType = type;
            return this;
        }

        public Builder sensitiveArgs(String... args) {
            this.sensitiveArgs = args == null ? new String[0] : args;
            return this;
        }

        public HttpRequest build() {
            if (basicAuthData != null && !protocol.equals("https")) {
                throw new IllegalArgumentException("Refusing to send credentials unencrypted");
            }

            String effectivePath = path;
            if (pathParams != null && pathParams.length != 0) {
                effectivePath = substitutePathParameters(path, pathParams);
            }
            if (query != null && query.size() != 0) {
                effectivePath += buildQuery(query);
            }
            try {
                this.url = new URL(protocol, host, effectivePath);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(String.format("Malformed URL by new java.net.URL(protocol=%s, host=%s, file=%s)",
                        protocol, host, effectivePath), e);
            }

            return new HttpRequest(this);
        }

        private static String buildQuery(Map<String, String> query) {
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
