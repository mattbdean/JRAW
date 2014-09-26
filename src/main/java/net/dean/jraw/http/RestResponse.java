package net.dean.jraw.http;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;
import net.dean.jraw.JrawUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * This class is used to show the result of a request to a RESTful web service, such as Reddit's JSON API.
 */
public class RestResponse {
    /** The ObjectMapper used to read a JSON tree into a JsonNode */
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    /** A list of all the headers received from the server */
    protected final Headers headers;
    /** The root node of the JSON */
    protected final JsonNode rootNode;
    /** The raw data of the response's content */
    protected final String raw;
    /** The Content-Type returned from the response */
    protected final MediaType type;

    /**
     * Instantiates a new RestResponse
     *
     * @param response The HttpResponse used to get the information
     */
    public RestResponse(Response response) {
        this(response, MediaType.parse("application/json"));
    }

    /**
     * Instantiates a new RestResponse. This constructor also reads the contents of the input stream and parses it into
     * the root JsonNode, and then consumes the response's entity.
     *
     * @param response The HttpResponse used to get the information
     * @param expected The expected ContentType
     */
    public RestResponse(Response response, MediaType expected) {
        this.headers = response.headers();
        this.raw = readContent(response);
        this.type = MediaType.parse(response.header("Content-Type"));

        if (!JrawUtils.typeComparison(expected, type)) {
            JrawUtils.logger().warn("Unknown Content-Type received: \"{}\"", type.toString());
        }
        if (JrawUtils.typeComparison(type, MediaTypes.JSON.type()) && !raw.isEmpty()) {
            this.rootNode = readTree(raw);
        } else {
            // Init JSON-related final variables
            this.rootNode = null;
        }
    }

    private String readContent(Response r) {
        try {
            return r.body().string();
        } catch (IOException e) {
            JrawUtils.logger().error("Could not read the body of the given response");
            return null;
        }
    }

    private JsonNode readTree(String raw) {
        try {
            return OBJECT_MAPPER.readTree(raw);
        } catch (IOException e) {
            JrawUtils.logger().error("Unable to parse JSON: \"{}\"", raw.replace("\n", "").replace("\r", ""));
            return null;
        }
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
     * Gets the raw data returned from the request
     * @return The raw data of the request
     */
    public String getRaw() {
        return raw;
    }
}
