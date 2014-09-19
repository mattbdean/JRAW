package net.dean.jraw.http;

import net.dean.jraw.JrawUtils;
import org.codehaus.jackson.JsonNode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * This class provides a way to consolidate all the main attributes of a RESTful HTTP request into one object
 */
public class HttpRequest {
    /** The HTTP verb to use to execute the request */
    private final HttpVerb verb;
    /** The path relative to the root of the host */
    private final String path;
    /** The arguments to be passed either by query string if the method is GET or DELETE, or by form if it is a different request */
    private final Map<String, String> args;
    private final String hostname;
    /** The time this request was executed */
    private LocalDateTime executed;
    private final JsonNode json;
    private final boolean isJson;

    /**
     * Instantiates a simple RestRequest
     *
     * @param verb The HTTP verb to use
     * @param path The path of the request. For example, "/api/login".
     */
    public HttpRequest(HttpVerb verb, String hostname, String path) {
        this(new Builder(verb, hostname, path));
    }

    protected HttpRequest(Builder b) {
        this.verb = b.verb;
        this.hostname = b.hostname;
        this.path = b.path;
        this.args = b.args;
        this.json = b.json;
        this.isJson = b.json != null;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public LocalDateTime getExecuted() {
        return executed;
    }

    public HttpVerb getVerb() {
        return verb;
    }

    public String getHostname() {
        return hostname;
    }

    public JsonNode getJson() {
        return json;
    }

    public boolean isJson() {
        return isJson;
    }


    public void onExecuted() {
        if (executed != null) {
            throw new IllegalStateException("Already executed (" + executed + ")");
        }
        this.executed = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "HttpRequest {" +
                "verb=" + verb +
                ", path='" + path + '\'' +
                ", args=" + args +
                ", hostname='" + hostname + '\'' +
                ", executed=" + executed +
                '}';
    }

    public static class Builder<T extends HttpRequest, U extends Builder<T, U>> {
        protected final HttpVerb verb;
        protected final String hostname;
        protected final String path;
        protected Map<String, String> args;
        protected JsonNode json;

        public Builder(HttpVerb verb, String hostname, String path) {
            this.verb = verb;
            this.hostname = hostname;
            this.path = path;
        }

        public U args(Map<String, String> args) {
            this.args = args;
            return (U) this;
        }

        public U args(Object... args) {
            this.args = JrawUtils.args(args);
            return (U) this;
        }

        public Builder json(JsonNode json) {
            if (verb == HttpVerb.GET || verb == HttpVerb.DELETE) {
                throw new IllegalArgumentException("Can't have JSON in a query string (you tried to attach a JsonNode to an " +
                        "HTTP verb that doesn't support application/x-www-form-urlencoded data: " + verb + ")");
            }
            this.json = json;
            return this;
        }

        public T build() {
            return (T) new HttpRequest(this);
        }
    }
}
