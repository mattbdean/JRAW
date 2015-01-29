package net.dean.jraw.http;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.models.JsonModel;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.RedditObject;
import net.dean.jraw.models.meta.ModelManager;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * This class is used to show the result of a request to a RESTful web service, such as Reddit's JSON API.
 *
 * This class provides automatic parsing of ApiExceptions, as well as quick RedditObject and Listing
 * creation. Note that constructing a RedditResponse will <em>not</em> throw an ApiException. This must be done by the
 * implementer. To see if the response has any errors, use {@link #hasErrors()} and {@link #getErrors()}
 */
public class RestResponse {
    private final HttpRequest origin;
    /** The ObjectMapper used to read a JSON tree into a JsonNode */
    private static final ObjectMapper objectMapper = new ObjectMapper();
    /** A list of all the headers received from the server */
    protected final Headers headers;
    /** The root node of the JSON */
    protected final JsonNode rootNode;
    /** The raw data of the response's content */
    protected final String raw;
    /** The Content-Type returned from the response */
    protected final MediaType type;
    protected final int statusCode;
    protected final String message;
    protected final String protocol;

    private final ApiException[] apiExceptions;

    /**
     * Instantiates a new RedditResponse
     */
    RestResponse(HttpRequest origin, InputStream body, Headers headers, int statusCode, String message, String protocol) {
        this.origin = origin;
        this.headers = headers;
        this.raw = readContent(body);
        this.type = MediaType.parse(headers.get("Content-Type"));
        this.statusCode = statusCode;
        this.message = message;
        this.protocol = protocol;

        // Assume there aren't any exceptions
        ApiException[] errors = new ApiException[0];

        if (JrawUtils.isEqual(type, MediaTypes.JSON.type()) && !raw.isEmpty()) {
            // Body is JSON, parse it and try to find ApiExceptions
            this.rootNode = readTree(raw);
            if (JrawUtils.isEqual(type, MediaTypes.JSON.type()) && !raw.isEmpty()) {
                // Parse the errors into ApiExceptions
                JsonNode errorsNode = rootNode.get("json");
                if (errorsNode != null) {
                    errorsNode = errorsNode.get("errors");
                }

                if (errorsNode != null) {
                    errors = new ApiException[errorsNode.size()];
                    if (errorsNode.size() > 0) {
                        for (int i = 0; i < errorsNode.size(); i++) {
                            JsonNode error = errorsNode.get(i);
                            errors[i] = new ApiException(error.get(0).asText(), error.get(1).asText());
                        }
                    }
                }
            }
        } else {
            // Init JSON-related final variables
            this.rootNode = null;
        }


        this.apiExceptions = errors;
    }

    private String readContent(InputStream entity) {
        try {
            Scanner s = new Scanner(entity).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } catch (Exception e) {
            JrawUtils.logger().error("Could not read the body of the given response");
            throw e;
        }
    }

    private JsonNode readTree(String raw) {
        try {
            return objectMapper.readTree(raw);
        } catch (IOException e) {
            JrawUtils.logger().error("Unable to parse JSON: \"{}\"", raw.replace("\n", "").replace("\r", ""));
            throw new RuntimeException("Unable to parse JSON");
        }
    }

    /** Convenience method to call {@link ModelManager#create(JsonNode, Class)} */
    @SuppressWarnings("unchecked")
    public <T extends JsonModel> T as(Class<T> thingClass) {
        return ModelManager.create(rootNode, thingClass);
    }

    /**
     * This method will return a Listing that represents this JSON response
     *
     * @param thingClass The class of T
     * @param <T> The type of object that the listing will contain
     * @return A new Listing
     */
    public <T extends RedditObject> Listing<T> asListing(Class<T> thingClass) {
        return new Listing<>(rootNode.get("data"), thingClass);
    }

    /**
     * Checks if there were errors returned by the Reddit API
     * @return True if there were errors, false if else
     * @see #getErrors()
     */
    public boolean hasErrors() {
        return apiExceptions.length != 0;
    }

    /**
     * Gets the ApiExceptions returned from the Reddit API
     * @return An array of ApiExceptions
     */
    public ApiException[] getErrors() {
        ApiException[] localCopy = new ApiException[apiExceptions.length];
        for (int i = 0; i < apiExceptions.length; i++) {
            localCopy[i] = new ApiException(apiExceptions[i].getReason(), apiExceptions[i].getExplanation());
        }
        return localCopy;
    }


    /**
     * Gets the Content-Type of the response
     * @return The Content-Type of the response
     */
    public MediaType getType() {
        return type;
    }

    /**
     * Gets the root JsonNode
     * @return The root JsonNode
     */
    public JsonNode getJson() {
        return rootNode;
    }

    /**
     * Gets the raw response data returned from the request
     * @return The raw data of the request
     */
    public String getRaw() {
        return raw;
    }

    public HttpRequest getOrigin() {
        return origin;
    }

    public Headers getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    public String getMessage() {
        return message;
    }

    public String getProtocol() {
        return protocol;
    }
}
