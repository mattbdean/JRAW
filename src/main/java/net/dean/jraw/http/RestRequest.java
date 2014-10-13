package net.dean.jraw.http;

import com.google.common.collect.ImmutableMap;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import net.dean.jraw.Endpoint;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class RestRequest {
    private final String url;
    private final String method;
    private final Map<String, String> formArgs;
    private final String[] sensitiveArgs;
    private final Request request;

    private RestRequest(Builder b) {
        this.request = b.builder.build();
        this.url = request.urlString();
        this.method = request.method();
        this.sensitiveArgs = b.sensitiveArgs;
        if (b.formArgs != null) {
            this.formArgs = ImmutableMap.<String, String>builder().putAll(b.formArgs).build();
        } else {
            formArgs = null;
        }

    }

    public Request getRequest() {
        return request;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getFormArgs() {
        return formArgs;
    }

    public String[] getSensitiveArgs() {
        return sensitiveArgs;
    }

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

        /**
         * Instantiates a new RequestBuilder
         */
        public Builder() {
            this.https = false;
            this.builder = new Request.Builder();
        }

        /**
         * Enables or disables HTTP over SSL
         * @param https Whether this request should be executed securely
         * @return This RequestBuilder
         */
        public Builder https(boolean https) {
            this.https = https;
            return this;
        }

        /**
         * Sets the query string arguments. The given parameters are passed into {@link JrawUtils#args(Object...)} and then
         * given to {@link #query(java.util.Map)}.
         * @param args The parameters that will be found in the query string
         * @return This RequestBuilder
         */
        public Builder query(Object... args) {
            return query(JrawUtils.args(args));
        }

        /**
         * Sets the query string arguments
         * @param args The parameters that will be found in the query string
         * @return This RequestBuilder
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
         * @return This RequestBuilder
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
         * @return This RequestBuilder
         */
        public Builder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * Sets the request's host. For example, "www.reddit.com"
         * @param host The new host
         * @return This RequestBuilder
         */
        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder get() {
            builder.get();
            return this;
        }

        public Builder delete() {
            builder.delete();
            return this;
        }

        public Builder post(Map<String, String> formArgs) {
            return formMethod("POST", formArgs);
        }

        public Builder put(Map<String, String> formArgs) {
            return formMethod("PUT", formArgs);
        }

        public Builder header(String key, String value) {
            builder.header(key, value);
            return this;
        }

        public Builder sensitiveArgs(String... args) {
            this.sensitiveArgs = args;
            return this;
        }

        private Builder formMethod(String method, Map<String, String> formArgs) {
            FormEncodingBuilder formBuilder = new FormEncodingBuilder();
            for (Map.Entry<String, String> entry : formArgs.entrySet()) {
                formBuilder.add(entry.getKey(), entry.getValue());
            }

            builder.method(method, formBuilder.build());

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
