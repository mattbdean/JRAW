package net.dean.jraw.http;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditObjectParser;
import net.dean.jraw.models.RedditObject;
import net.dean.jraw.models.core.Comment;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class is used to show the result of a request to a RESTful web service, such as Reddit's JSON API.
 */
public class RestResponse {
    /** The ObjectMapper used to read a JSON tree into a JsonNode */
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    protected static final RedditObjectParser REDDIT_OBJECT_PARSER = new RedditObjectParser();
    /** A list of all the headers received from the server */
    private final Header[] headers;
    /** The root node of the JSON */
    private final JsonNode rootNode;
    /** The raw data of the response's content */
    private final String raw;
    private final ApiException[] apiExceptions;
    private final String contentType;

    /**
     * Instantiates a new RestResponse. This constructor also reads the contents of the input stream and parses it into
     * the root JsonNode, and then consumes the response's entity.
     *
     * @param response The HttpResponse used to get the information
     */
    public RestResponse(HttpResponse response) {
        this.headers = response.getAllHeaders();

        // http://stackoverflow.com/a/5445161
        Scanner s = getContentScanner(response.getEntity());
        this.raw = s.hasNext() ? s.next() : "";

        String type = getHeader("Content-Type").getValue().toLowerCase();
        if (type.contains(";")) {
            // Remove any extra data. For example: "application/json; charset=UTF-8"
            type = type.substring(0, type.indexOf(";"));
        }
        type = type.trim();

        this.contentType = type;
        if (contentType.equals("application/json")) {
            this.rootNode = readTree(raw);

            JsonNode errorsNode = rootNode.get("json");
            if (errorsNode != null) {
                errorsNode = errorsNode.get("errors");
            }

            if (errorsNode != null) {
                apiExceptions = new net.dean.jraw.ApiException[errorsNode.size()];
                if (errorsNode.size() > 0) {

                    for (int i = 0; i < errorsNode.size(); i++) {
                        JsonNode error = errorsNode.get(i);
                        apiExceptions[i] = new ApiException(error.get(0).asText(), error.get(1).asText());
                    }
                }
            } else {
                // We still have to initialize it
                apiExceptions = new net.dean.jraw.ApiException[0];
            }
        } else {
            if (contentType.equals("text/html"))
                JrawUtils.logger().warn("Received HTML from Reddit API instead of JSON. Are you sure you have access to this document?");
            else
                JrawUtils.logger().warn("Unknown Content-Type received: \"{}\"", contentType);

            // Init JSON-related final variables
            this.apiExceptions = null;
            this.rootNode = null;
        }

        try {
            EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
            JrawUtils.logger().error("Unable to consume entity", e);
        }
    }

    private Scanner getContentScanner(HttpEntity entity) {
        try {
            return new Scanner(entity.getContent()).useDelimiter("\\A");
        } catch (IOException e) {
            JrawUtils.logger().error("Could not get the content of HttpEntity " + entity, e);
            return null;
        }
    }

    private JsonNode readTree(String raw) {
        try {
            return OBJECT_MAPPER.readTree(raw);
        } catch (IOException e) {
            JrawUtils.logger().error("Unable to parse JSON: {}", raw.replace("\n", "").replace("\r", ""));
            return null;
        }
    }


     /**
     * This method is a convenience method for turning the JsonNode associated with this data into a RedditObject. Make
     * sure that the appropriate class is used. No exception will be thrown if the "wrong" class is used, but you will
     * receive many NullPointerExceptions down the road.
     *
     * @param thingClass The class that will be used to instantiate the T
     * @param <T> The type of object to be created
     * @return A new RedditObject
     */
    @SuppressWarnings("unchecked")
    public <T extends RedditObject> T as(Class<T> thingClass) {
        if (thingClass.equals(Submission.class)) {
            // Special handling for submissions, not just submission data being returned, also its comments.
            // For example: http://www.reddit.com/92dd8.json

            // rootNode is an array where the first is a listing which contains one element in its "children": the submission.
            // The second element in the array is a listing of comments

            // Get the list of comments first
            JsonNode commentListingDataNode = rootNode.get(1).get("data");
            Listing<Comment> comments = new Listing<>(commentListingDataNode, Comment.class);
            return (T) new Submission(rootNode.get(0).get("data").get("children").get(0).get("data"), comments);
        }

        // Normal Thing
        return REDDIT_OBJECT_PARSER.parse(rootNode, thingClass);
    }

    /**
     * This method is essentially the same as {@link #as(Class)}, except for {@link Listing}s.
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
     * @see #getApiExceptions()
     */
    public boolean hasErrors() {
        return apiExceptions.length != 0;
    }

    /**
     * Gets the ApiExceptions returned from the Reddit API
     * @return An array of ApiExceptions
     */
    public ApiException[] getApiExceptions() {
        return apiExceptions;
    }

    /**
     * Gets a Header object by name from the list of headers
     *
     * @param name The name of the header, such as {@code Content-Length}
     * @return A Header object with a given name
     */
    public Header getHeader(String name) {
        for (Header h : headers) {
            if (h.getName().equalsIgnoreCase(name)) {
                return h;
            }
        }

        return null;
    }

    /**
     * Gets all the headers received from the server
     * @return An array of Header objects
     */
    public Header[] getHeaders() {
        return headers;
    }

    /**
     * Gets the value of the Content-Type header received from the server
     * @return The Content-Type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Gets the root JsonNode
     * @return The root JsonNode
     */
    public JsonNode getJson() {
        return rootNode;
    }

    /**
     * Gets the raw data returned from the request
     * @return The raw data of the request
     */
    public String getRaw() {
        return raw;
    }
}
