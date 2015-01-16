package net.dean.jraw;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a Reddit API endpoint such as "{@code POST /api/login}"
 */
public class Endpoint implements Comparable<Endpoint> {
    /** This Pattern will match a URI parameter. For example, /api/{param1}/{param2} */
    public static final Pattern URI_PARAM_PATTERN = Pattern.compile("\\{([^\\}]+)\\}");

    private final String scope;
    private boolean implemented;
    private Method method;

    protected final String verb;
    protected final String uri;
    protected final String requestDescriptor;
    protected final List<String> urlParams;

    /**
     * Instantiates a new Endpoint. Used mostly for meta-programming in the
     * <a href="https://github.com/thatJavaNerd/JRAW/tree/master/endpoints">endpoints</a> subproject and in the
     * {@link net.dean.jraw.Endpoints} class.
     * @param requestDescriptor A string consisting of two parts: the HTTP verb, and the URI. For example:
     *                          "POST /api/login"
     */
    public Endpoint(String requestDescriptor) {
        this(requestDescriptor, null);
    }

    /**
     * Instantiates a new Endpoint
     * @param requestDescriptor A string consisting of two parts: the HTTP verb, and the URI. For example:
     *                          "POST /api/login"
     * @param scope This endpoint's scope, such as "accounts". Can be found
     *                 <a href="http://www.reddit.com/dev/api">here</a>
     */
    public Endpoint(String requestDescriptor, String scope) {
        this.requestDescriptor = requestDescriptor;
        String[] parts = requestDescriptor.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid number of parts for descriptor \"" + requestDescriptor + "\"");
        }

        this.verb = parts[0].toUpperCase();
        this.uri = parts[1];
        this.urlParams = parseUrlParams(uri);
        this.scope = scope;
        this.implemented = false;
    }

    private List<String> parseUrlParams(String uri) {
        List<String> params = new ArrayList<>();
        Matcher matcher = URI_PARAM_PATTERN.matcher(uri);
        while (matcher.find()) {
            params.add(matcher.group());
        }

        return params;
    }

    /**
     * Gets a list of parameters in this endpoint's URI. For example, the endpoint {@code /user/{username}/about.json}
     * would have one parameter: {@code {username}}.
     * @return The URI parameters
     */
    public List<String> getUrlParams() {
        return urlParams;
    }

    /**
     * Gets this endpoint's OAuth2 scope. Always null for normal library use. See
     * <a href="http://www.reddit.com/dev/api">here</a>
     * for examples.
     *
     * @return This endpoint's scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * Checks if a {@link net.dean.jraw.EndpointImplementation} annotation with the corresponding
     * {@link net.dean.jraw.Endpoints} enum has been registered. Always false for normal library use.
     *
     * @return If this endpoint has been implemented
     */
    public boolean isImplemented() {
        return implemented;
    }

    /**
     * Sets the method which implements the endpoint and sets the endpoint as implemented
     * @param implementer The method that implements this endpoint
     */
    public void implement(Method implementer) {
        this.method = implementer;
        this.implemented = true;
    }

    /**
     * Gets the method at which this endpoint is implemented. Always null for normal library use.
     * @return The method where this endpoint is implemented
     */
    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getVerb() {
        return verb;
    }

    public String getUri() {
        return uri;
    }

    public String getRequestDescriptor() {
        return requestDescriptor;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Endpoint endpoint = (Endpoint) object;

        return implemented == endpoint.implemented &&
                !(scope != null ? !scope.equals(endpoint.scope) : endpoint.scope != null) &&
                requestDescriptor.equals(endpoint.requestDescriptor) &&
                !(method != null ? !method.equals(endpoint.method) : endpoint.method != null);

    }

    @Override
    public int hashCode() {
        int result = scope != null ? scope.hashCode() : 0;
        result = 31 * result + (implemented ? 1 : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + requestDescriptor.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Endpoint {" +
                "scope='" + scope + '\'' +
                ", implemented=" + implemented +
                ", method=" + method +
                ", verb='" + verb + '\'' +
                ", uri='" + uri + '\'' +
                ", requestDescriptor='" + requestDescriptor + '\'' +
                ", urlParams=" + urlParams +
                '}';
    }

    @Override
    public int compareTo(Endpoint other) {
        // Android-compatible compare
        int implComp = Boolean.valueOf(implemented).compareTo(Boolean.valueOf(other.implemented));
        if (implComp != 0) {
            return implComp;
        } else {
            int nameComp = uri.compareTo(other.uri);
            if (nameComp != 0) {
                return nameComp;
            } else {
                return verb.compareTo(other.verb);
            }
        }
    }
}
