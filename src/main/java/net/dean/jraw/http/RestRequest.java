package net.dean.jraw.http;

import org.codehaus.jackson.JsonNode;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * This class provides a way to consolidate all the main attributes of a RESTful HTTP request into one object
 */
public class RestRequest {

    /** The path relative to the root of the host */
    private String path;

    /**
     * The arguments to be passed either by query string if the method is GET or DELETE, or by form if it is a different request
     */
    private Map<String, String> args;

    private JsonNode json;

    private boolean isJson;

    /** The HTTP verb to use to execute the request */
    private HttpVerb verb;

    /** The time this request was executed */
    private LocalDateTime executed;

    /**
     * Instantiates a new RestRequest
     *
     * @param verb The HTTP verb to use
     * @param path The path of the request relative to the host (ex: "/{something}")
     */
    public RestRequest(HttpVerb verb, String path) {
        this(verb, path, (Map<String, String>)null);
    }

    /**
     * Instantiates a new RestRequest
     *
     * @param verb The HTTP verb to use
     * @param path The path of the request relative to the host "/{something}"
     * @param args The arguments to pass in the query
     */
    public RestRequest(HttpVerb verb, String path, Map<String, String> args) {
        this.verb = verb;
        this.path = path;
        this.args = args;
        this.isJson = false;
    }

    public RestRequest(HttpVerb verb, String path, JsonNode json) {
        this.verb = verb;
        this.path = path;
        this.json = json;
        this.isJson = true;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
        this.isJson = false;
    }

    public LocalDateTime getExecuted() {
        return executed;
    }

    public void setExecuted(LocalDateTime executed) {
        this.executed = executed;
    }

    public HttpVerb getVerb() {
        return verb;
    }

    public void setVerb(HttpVerb verb) {
        this.verb = verb;
    }

    public void setJson(JsonNode json) {
        this.json = json;
        this.isJson = true;
    }

    public JsonNode getJson() {
        return json;
    }

    public boolean isJson() {
        return isJson;
    }

    @Override
    public String toString() {
        return "RestRequest{" +
                "path='" + path + '\'' +
                ", args=" + args +
                ", verb=" + verb +
                ", executed=" + executed +
                '}';
    }
}
