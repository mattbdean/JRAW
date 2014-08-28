package net.dean.jraw.endpoints;

import net.dean.jraw.http.HttpVerb;

import java.lang.reflect.Method;

/**
 * This class represents a Reddit API endpoint such as "{@code POST /api/login}"
 */
class Endpoint {
    private final String httpDescriptor;
    private final String uri;
    private final HttpVerb verb;
    private final String category;
    private boolean implemented;
    private Method method;

    /**
     * Instantiates a new Endpoint
     * @param httpDescriptor A string consisting of two parts: the HTTP verb, and the URI. For example: "@{code POST /api/login}"
     * @param category This endpoint's category, such as "accounts". Can be found <a href="http://www.reddit.com/dev/api">here</a>
     */
    public Endpoint(String httpDescriptor, String category) {
        this.httpDescriptor = httpDescriptor;
        String[] parts = httpDescriptor.split(" ");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid number of parts for descriptor \"" + httpDescriptor + "\"");
        }

        this.verb = HttpVerb.valueOf(parts[0].toUpperCase());
        this.uri = parts[1];
        this.category = category;
        this.implemented = false;
    }

    /**
     * Gets this endpoint's URI
     *
     * @return This endpoint's URI
     */
    String getUri() {
        return uri;
    }

    /**
     * Gets this endpoint's category
     *
     * @return This endpoint's category
     */
    String getCategory() {
        return category;
    }

    /**
     * Gets the HTTP verb used for this endpoint
     * @return The HTTP verb
     */
    HttpVerb getVerb() {
        return verb;
    }

    Method getMethod() {
        return method;
    }

    boolean isImplemented() {
        return implemented;
    }

    void setImplemented(boolean implemented) {
        this.implemented = implemented;
    }

    String getHttpDescriptor() {
        return httpDescriptor;
    }

    void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "uri='" + uri + '\'' +
                ", category='" + category + '\'' +
                ", implemented=" + implemented + '\'' +
                ", method=" + method +
                '}';
    }
}
