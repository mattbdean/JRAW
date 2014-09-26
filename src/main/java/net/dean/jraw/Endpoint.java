package net.dean.jraw;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents a Reddit API endpoint such as "{@code POST /api/login}"
 */
public class Endpoint {
    public static final Pattern URI_PARAM_PATTERN = Pattern.compile("\\{([^\\}]+)\\}");

    private final String category;
    private boolean implemented;
    private Method method;

    protected final String verb;
    protected final String uri;
    protected final String requestDescriptor;
    protected final List<String> urlParams;

    /**
     * Instantiates a new Endpoint. Used mostly for meta-programming in the <a href="https://github.com/thatJavaNerd/JRAW/tree/master/endpoints">endpoints</a>
     * subproject.
     * @param requestDescriptor A string consisting of two parts: the HTTP verb, and the URI. For example: "@{code POST /api/login}"
     */
    public Endpoint(String requestDescriptor) {
        this(requestDescriptor, null);
    }

    /**
     * Instantiates a new Endpoint
     * @param requestDescriptor A string consisting of two parts: the HTTP verb, and the URI. For example: "@{code POST /api/login}"
     * @param category This endpoint's category, such as "accounts". Can be found <a href="http://www.reddit.com/dev/api">here</a>
     */
    public Endpoint(String requestDescriptor, String category) {
        this.requestDescriptor = requestDescriptor;
        String[] parts = requestDescriptor.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid number of parts for descriptor \"" + requestDescriptor + "\"");
        }

        this.verb = parts[0].toUpperCase();
        this.uri = parts[1];
        this.urlParams = parseUrlParams(uri);
        this.category = category;
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
     * Gets this endpoint's category. Always null for normal library use. See <a href="http://www.reddit.com/dev/api">here</a>
     * for examples.
     *
     * @return This endpoint's category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Checks if a {@link net.dean.jraw.EndpointImplementation} annotation with the corresponding {@link net.dean.jraw.Endpoints}
     * enum has been registered. Always false for normal library use.
     *
     * @return If this endpoint has been implemented
     */
    public boolean isImplemented() {
        return implemented;
    }

    public void setImplemented(boolean implemented) {
        this.implemented = implemented;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Endpoint endpoint = (Endpoint) o;

        if (implemented != endpoint.implemented) return false;
        if (category != null ? !category.equals(endpoint.category) : endpoint.category != null) return false;
        if (!requestDescriptor.equals(endpoint.requestDescriptor)) return false;
        if (method != null ? !method.equals(endpoint.method) : endpoint.method != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (implemented ? 1 : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + requestDescriptor.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Endpoint {" +
                "category='" + category + '\'' +
                ", implemented=" + implemented +
                ", method=" + method +
                ", verb='" + verb + '\'' +
                ", uri='" + uri + '\'' +
                ", requestDescriptor='" + requestDescriptor + '\'' +
                ", urlParams=" + urlParams +
                '}';
    }
}
