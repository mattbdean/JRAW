package net.dean.jraw.http;

import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import net.dean.jraw.Endpoint;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * This class is responsible for representing a RESTful HTTP request
 */
public final class RestRequest {
    private final String url;
    private final String method;
    private final Map<String, String> formArgs;
    private final Map<String, String> query;
    private final String[] sensitiveArgs;
    private final Request request;
    private final Endpoints endpoint;
    private final boolean needsAuth;
    private final MediaType expected;

    /**
     * Creates a RestRequest from the given URL
     * @param method The HTTP verb to execute this RestRequest with
     * @param url A valid URL
     * @param formArgs An optional array of Strings to send as form data. See {@link JrawUtils#args(Object...)} for more
     *                 info.
     * @return A RestRequest that represents the given URL
     */
    public static RestRequest from(String method, URL url, Object... formArgs) {
        return Builder.from(method, url, formArgs).build();
    }

    private RestRequest(Builder b) {
        this.request = b.builder.build();
        this.url = request.urlString();
        this.method = request.method();
        this.sensitiveArgs = b.sensitiveArgs;
        this.endpoint = b.endpoint;
        this.needsAuth = b.auth;
        this.expected = b.expected;
        if (b.query != null) {
            this.query = b.query;
        } else {
            query = null;
        }
        if (b.formArgs != null) {
            this.formArgs = ImmutableMap.<String, String>builder().putAll(b.formArgs).build();
        } else {
            formArgs = null;
        }
    }

    /**
     * Checks if this request needs some sort of authentication to be sent successfully.
     * @return True if this request needs authentication, false if else.
     */
    public boolean needsAuth() {
        return needsAuth;
    }

    /**
     * Gets the OkHttp request being wrapped by this object.
     * @return The OkHttp request
     */
    public Request getRequest() {
        return request;
    }

    /**
     * The fully-qualified URL that this request will be sent to
     * @return The URL that this request will be sent to
     */
    public String getUrl() {
        return url;
    }

    /**
     * The HTTP method to use (GET, POST, PUT, DELETE, etc.)
     * @return The HTTP method to use
     */
    public String getMethod() {
        return method;
    }

    /**
     * The data to send as URL-encoded form data with a POST or similar request
     * @return The data to send in the form body
     */
    public Map<String, String> getFormArgs() {
        return formArgs;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    /**
     * An array of the names of sensitive form data arguments. These values of these arguments should be concealed.
     * @return An array of keys whose values are sensitive
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
     * The endpoint that this request is being sent to
     */
    public Endpoints getEndpoint() {
        return endpoint;
    }

    /**
     * Checks if the given key in the form data is sensitive
     * @param arg A key
     * @return True, if the key's value has been marked as sensitive. False if else
     */
    public boolean isSensitive(String arg) {
        if (sensitiveArgs == null || sensitiveArgs.length == 0) {
            return false;
        }
        for (String sensitive : sensitiveArgs) {
            if (sensitive.equals(arg)) {
                return true;
            }
        }

        return false;
    }

    public Request getOkHttpRequest() {
        return request;
    }

    /**
     * The expected Content-Type
     */
    public MediaType getExpectedType() {
        return expected;
    }

    /**
     * This class is responsible for creating new RestRequests
     */
    public static class Builder {
        private Request.Builder builder;

        private String host;
        private String path;
        private Map<String, String> query;
        private List<String> urlParams;
        private boolean https;
        private Endpoints endpoint;
        private Map<String, String> formArgs;
        private String[] sensitiveArgs;
        private boolean auth;
        private MediaType expected;

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
                try {
                    String[] queryKeysValues = URLDecoder.decode(url.getQuery(), "UTF-8").split("&");
                    for (String keyValuePair : queryKeysValues) {
                        String[] parts = keyValuePair.split("=");
                        query.put(parts[0], parts[1]);
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("UTF-8 not supported");
                }
            }

            Builder b = new Builder()
                        .https(url.getProtocol().equals("https"))
                        .host(url.getHost())
                        .path(url.getFile())
                        .query(query);
            if (formArgs.length != 0) {
                b.formMethod(method, JrawUtils.args(formArgs));
            }
            return b;
        }

        /**
         * Instantiates a new Builder
         */
        public Builder() {
            this.https = false;
            this.auth = false;
            this.expected = MediaTypes.JSON.type();
            this.builder = new Request.Builder();
        }

        /**
         * Enables or disables HTTP over SSL
         * @param https Whether this request should be executed securely
         * @return This Builder
         */
        public Builder https(boolean https) {
            this.https = https;
            return this;
        }

        /**
         * Sets the query string arguments. The given parameters are passed into {@link JrawUtils#args(Object...)} and then
         * given to {@link #query(java.util.Map)}.
         * @param args The parameters that will be found in the query string
         * @return This Builder
         */
        public Builder query(Object... args) {
            return query(JrawUtils.args(args));
        }

        /**
         * Sets the query string arguments
         * @param args The parameters that will be found in the query string
         * @return This Builder
         */
        public Builder query(Map<String, String> args) {
            this.query = args;
            return this;
        }

        /**
         * Sets the endpoint to use. If the given endpoint has arguments in the URI (such as {@code /user/{username}/about.json}),
         * then the given strings will be substituted in the order given. For example, if the endpoint was
         * {@code /api/multi/{multipath}/r/{srname}}, and the given strings were "myMulti" and "mySubreddit", then the
         * result would be {@code /api/multipath/myMulti/mySubreddit}. If {@link #path(String)} is also called, this method
         * will take priority, regardless of order.
         *
         * @param e The endpoint to use
         * @param positionalUrlParams The parameters to use. Must be equal to the size of the corresponding Endpoint's
         * {@link net.dean.jraw.Endpoint#getUrlParams()} list.
         * @return This Builder
         */
        public Builder endpoint(Endpoints e, String... positionalUrlParams) {
            this.endpoint = e;
            this.urlParams = Arrays.asList(positionalUrlParams);
            return this;
        }

        /**
         * Sets the path of the request. For example, "/stylesheet". If {@link #endpoint(net.dean.jraw.Endpoints, String...)}
         * is also called, that method will take priority, regardless of order.
         *
         * @param path The path to use
         * @return This Builder
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * Sets the request's host. For example, "www.reddit.com"
         * @param host The new host
         * @return This Builder
         */
        public Builder host(String host) {
            this.host = host;
            return this;
        }

        /**
         * Marks this Builder as being sent as a GET request
         * @return This Builder
         */
        public Builder get() {
            builder.get();
            return this;
        }

        /**
         * Marks this Builder as being sent as a DELETE request
         * @return This Builder
         */
        public Builder delete() {
            builder.delete();
            return this;
        }

        /**
         * Marks this Builder as being sent as a POST request
         * @param formArgs The arguments to send in the body. Will be URL encoded.
         * @return This Builder
         */
        public Builder post(Map<String, String> formArgs) {
            return formMethod("POST", formArgs);
        }

        /**
         * Marks this Builder as being sent as a PUT request
         * @param formArgs The arguments to send in the body. Will be URL encoded.
         * @return This Builder
         */
        public Builder put(Map<String, String> formArgs) {
            return formMethod("PUT", formArgs);
        }

        /**
         * Adds a header to the request
         * @param key The name of the header
         * @param value The header's value
         * @return This Builder
         */
        public Builder header(String key, String value) {
            builder.header(key, value);
            return this;
        }

        /**
         * Sets the keys of form data that have sensitive arguments
         * @param args The keys
         * @return This Builder
         */
        public Builder sensitiveArgs(String... args) {
            this.sensitiveArgs = args;
            return this;
        }

        /**
         * Sets whether or not this request will required authentication in order to complete successfully.
         * @param auth If this request needs authentication
         * @return This Builder
         */
        public Builder needsAuth(boolean auth) {
            this.auth = auth;
            return this;
        }

        /**
         * Sets the expected content type
         * @param type The expected content type
         * @return This Builder
         */
        public Builder expected(MediaType type) {
            this.expected = type;
            return this;
        }

        /**
         * Sets a custom request body
         * @param method The HTTP verb to execute this request with
         * @param mediaType The Content-Type of the body
         * @param content The content to send
         * @return This Builder
         */
        public Builder customBody(String method, MediaType mediaType, String content) {
            builder.method(method, RequestBody.create(mediaType, content));
            return this;
        }

        private Builder formMethod(String method, Map<String, String> formArgs) {
            RequestBody body = null;
            if (formArgs != null) {
                FormEncodingBuilder formBuilder = new FormEncodingBuilder();
                for (Map.Entry<String, String> entry : formArgs.entrySet()) {
                    formBuilder.add(entry.getKey(), entry.getValue());
                }
                body = formBuilder.build();
            }

            builder.method(method, body);

            this.formArgs = formArgs;
            return this;

        }

        /**
         * Generates a valid query string based on the given arguments
         * @param args The arguments for the query string
         * @return A query string
         */
        private String generateQueryString(Map<String, String> args) {
            if (args.size() == 0) {
                return "";
            }
            StringBuilder url = new StringBuilder();

            // Create the query string (?foo=bar&key1=val1
            url.append("?");
            for (Iterator<Map.Entry<String, String>> it = args.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, String> entry = it.next();

                url.append(urlEncode(entry.getKey()));
                url.append("=");
                url.append(urlEncode(entry.getValue()));
                if (it.hasNext()) {
                    // More parameters are coming, add a separator
                    url.append("&");
                }
            }

            return url.toString();
        }

        /**
         * This method will substitute the URI parameters with the given arguments, like
         * {@link #endpoint(net.dean.jraw.Endpoints, String...)} specifies.
         * @param e The endpoint to use
         * @param positionalArgs The arguments to use
         * @return A URI based on the given endpoint's URI
         */
        private String replaceUriParameters(Endpoint e, List<String> positionalArgs) {
            if (e.getUrlParams().size() != positionalArgs.size()) {
                // Different amount of parameters
                throw new IllegalArgumentException(String.format("URL parameter size mismatch. Expecting %s, got %s",
                        e.getUrlParams().size(), positionalArgs.size()));
            }

            // There are URL parameters, substitute them
            String updatedUri = e.getUri();
            Matcher m = null;
            for (String arg : positionalArgs) {
                if (m == null) {
                    // Create on first use
                    m = Endpoint.URI_PARAM_PATTERN.matcher(updatedUri);
                } else {
                    // Reuse the Matcher
                    m.reset(updatedUri);
                }
                updatedUri = m.replaceFirst(arg);
            }

            return updatedUri;
        }

        /**
         * Builds this Builder into a fully-fledged RestRequest that mirrors the given parameters exactly
         * @return A new RestRequest
         */
        public RestRequest build() {
            if (host == null || host.isEmpty()) {
                throw new IllegalStateException("No host given");
            }

            // Update the url using url()
            StringBuilder url = new StringBuilder(
                    String.format("http%s://%s", https ? "s" : "", host));

            // Add the endpoint URI
            if (endpoint != null) {
                Endpoint e = endpoint.getEndpoint();
                if (e.getUrlParams().isEmpty()) {
                    // There are no parameters for the endpoint or none have been given
                    url.append(e.getUri());
                } else {
                    url.append(replaceUriParameters(e, urlParams));
                }
            } else if (path != null) {
                // Endpoint takes priority over path
                if (!path.startsWith("/")) {
                    url.append("/");
                }
                url.append(path);
            }

            if (query != null) {
                url.append(generateQueryString(query));
            }

            builder.url(url.toString());

            return new RestRequest(this);
        }

        /**
         * Utility method to URL-encode a given string in UTF-8.
         * @param str The unencoded String
         * @return A URL-encoded string
         */
        private String urlEncode(String str) {
            String charset = StandardCharsets.UTF_8.name();
            try {
                return URLEncoder.encode(str, charset);
            } catch (UnsupportedEncodingException e) {
                JrawUtils.logger().error("Unsupported charset: " + charset);
                return null;
            }
        }
    }
}
