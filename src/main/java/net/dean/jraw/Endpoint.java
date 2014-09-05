package net.dean.jraw;

import net.dean.jraw.http.HttpVerb;

import java.lang.reflect.Method;

/**
 * This class represents a Reddit API endpoint such as "{@code POST /api/login}"
 */
public class Endpoint {
    private final String category;
    private boolean implemented;
    private Method method;

    protected final HttpVerb verb;
    protected final String uri;
    protected final String requestDescriptor;

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

        this.verb = HttpVerb.valueOf(parts[0].toUpperCase());
        this.uri = parts[1];
        this.category = category;
        this.implemented = false;
    }

    public String getCategory() {
        return category;
    }

    public boolean isImplemented() {
        return implemented;
    }

    public void setImplemented(boolean implemented) {
        this.implemented = implemented;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public HttpVerb getVerb() {
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
        return "MetaEndpoint {" +
                "uri='" + uri + '\'' +
                ", verb=" + verb + '\'' +
                ", category='" + category + '\'' +
                ", implemented=" + implemented +
                ", method=" + method +
                '}';
    }
}
