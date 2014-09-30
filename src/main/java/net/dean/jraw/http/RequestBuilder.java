package net.dean.jraw.http;

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

/**
 * This class extends {@link com.squareup.okhttp.Request.Builder} to provide some Reddit-specific features. Because of
 * the way {@link Request.Builder} was designed, methods from this class must be called before classes belonging to its
 * superclasses.
 */
public class RequestBuilder extends Request.Builder {
    private String host;
    private String path;
    private Map<String, String> query;
    private List<String> urlParams;
    private boolean https;
    private Endpoints endpoint;

    /**
     * Instantiates a new RequestBuilder
     */
    public RequestBuilder() {
        this.https = false;
    }

    /**
     * Enables or disables HTTP over SSL
     * @param https Whether this request should be executed securely
     * @return This RequestBuilder
     */
    public RequestBuilder https(boolean https) {
        this.https = https;
        return this;
    }

    /**
     * Sets the query string arguments. The given parameters are passed into {@link JrawUtils#args(Object...)} and then
     * given to {@link #query(java.util.Map)}.
     * @param args The parameters that will be found in the query string
     * @return This RequestBuilder
     */
    public RequestBuilder query(Object... args) {
        return query(JrawUtils.args(args));
    }

    /**
     * Sets the query string arguments
     * @param args The parameters that will be found in the query string
     * @return This RequestBuilder
     */
    public RequestBuilder query(Map<String, String> args) {
        this.query = args;
        return this;
    }

    /**
     * Sets the endpoint to use. If the given endpoint has arguments in the URI (such as {@code /user/{username}/about.json}),
     * then the given strings will be substituted in the order given. For example, if the endpoint was
     * {@code /api/multi/{multipath}/r/{srname}}, and the given strings were "myMulti" and "mySubreddit", then the
     * result would be {@code /api/multipath/myMulti/mySubreddit}. If {@link #path(String)} is also called, this method
     * will take priority.
     *
     * @param e The endpoint to use
     * @param positionalUrlParams The parameters to use. Must be equal to the size of the corresponding Endpoint's
     * {@link net.dean.jraw.Endpoint#getUrlParams()} list.
     * @return This RequestBuilder
     */
    public RequestBuilder endpoint(Endpoints e, String... positionalUrlParams) {
        this.endpoint = e;
        this.urlParams = Arrays.asList(positionalUrlParams);
        return this;
    }

    /**
     * Sets the path of the request. For example, "/stylesheet". If {@link #endpoint(net.dean.jraw.Endpoints, String...)}
     * is also called, that method will take priority.
     *
     * @param path The path to use
     * @return This RequestBuilder
     */
    public RequestBuilder path(String path) {
        this.path = path;
        return this;
    }

    public RequestBuilder host(String host) {
        this.host = host;
        return this;
    }

    /**
     * Generates a valid query string based on the given arguments
     * @param args The arguments for the query string
     * @return A query string
     */
    private String generateQueryString(Map<String, String> args) {
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

    @Override
    public Request build() {
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

        url(url.toString());
        return super.build();
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
