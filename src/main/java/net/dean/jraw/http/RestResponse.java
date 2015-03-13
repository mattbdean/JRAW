package net.dean.jraw.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.google.common.net.MediaType;
import com.squareup.okhttp.Headers;
import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.models.JsonModel;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.RedditObject;
import net.dean.jraw.models.meta.ModelManager;

import java.io.InputStream;
import java.util.Scanner;

/**
 * This class is used to show the result of a request to a RESTful web service, such as Reddit's JSON API.
 *
 * This class provides automatic parsing of ApiExceptions, as well as quick RedditObject and Listing
 * creation. Note that constructing a RestResponse will <em>not</em> throw an ApiException. This must be done by whomever
 * handles the exception. To see if the response has any errors, use {@link #hasErrors()} and {@link #getError()}
 */
public class RestResponse {
    private final HttpRequest origin;
    /** A list of all the headers received from the server */
    protected final Headers headers;
    /** The root node of the JSON */
    protected final JsonNode rootNode;
    /** The raw data of the response's content */
    protected final String raw;
    /** The Content-Type returned from the response */
    protected final MediaType type;
    protected final int statusCode;
    protected final String statusMessage;
    protected final String protocol;

    private final ApiException apiException;

    /**
     * Instantiates a new RedditResponse
     */
    @SuppressWarnings("ThrowableInstanceNeverThrown")
    RestResponse(HttpRequest origin, InputStream body, Headers headers, int statusCode, String statusMessage, String protocol) {
        this.origin = origin;
        this.headers = headers;
        String contentType = headers.get("Content-Type");
        if (contentType == null)
            throw new IllegalStateException("No Content-Type header was found");
        this.type = JrawUtils.parseMediaType(contentType);
        String charset = type.charset().or(Charsets.UTF_8).name();
        this.raw = readContent(body, charset);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.protocol = protocol;

        // Assume there aren't any exceptions
        ApiException error = null;

        if (JrawUtils.isEqual(type, MediaTypes.JSON.type()) && !raw.isEmpty()) {
            // Body is JSON, parse it and try to find ApiExceptions
            this.rootNode = JrawUtils.fromString(raw);
            if (JrawUtils.isEqual(type, MediaTypes.JSON.type()) && !raw.isEmpty()) {
                // Parse the errors into ApiExceptions
                JsonNode errorsNode = rootNode.get("json");
                if (errorsNode != null) {
                    errorsNode = errorsNode.get("errors");
                }

                if (errorsNode != null && errorsNode.size() > 0) {
                    JsonNode errorNode = errorsNode.get(0);
                    error = new ApiException(errorNode.get(0).asText(), errorNode.get(1).asText());
                }
            }
        } else {
            // Init JSON-related final variables
            this.rootNode = null;
        }

        this.apiException = error;
    }

    private String readContent(InputStream entity, String charset) {
        try {
            Scanner s = new Scanner(entity, charset).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } catch (Exception e) {
            JrawUtils.logger().error("Could not read the body of the given response");
            throw e;
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
     * @see #getError()
     */
    public boolean hasErrors() {
        return apiException != null;
    }

    /**
     * Gets the ApiExceptions returned from the Reddit API
     * @return An array of ApiExceptions
     */
    public ApiException getError() {
        return apiException;
    }

    /** Gets the value of the Content-Type header */
    public MediaType getType() {
        return type;
    }

    /** Gets the root JsonNode, or null if the content type was not {@code application/json}. */
    public JsonNode getJson() {
        return rootNode;
    }

    /** Gets the raw response body */
    public String getRaw() {
        return raw;
    }

    /** Gets the request that initiated this response */
    public HttpRequest getOrigin() {
        return origin;
    }

    public Headers getHeaders() {
        return headers;
    }

    /** Gets the HTTP status code, such as 200 or 404. */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Checks if this response returned a successful status code, which is greater than or equal to 200, but less than
     * 300.
     */
    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    /** Gets the HTTP status message, such as "Not Found" or "OK" */
    public String getStatusMessage() {
        return statusMessage;
    }

    /** Gets the protocol that was used to execute this HTTP request, such as "HTTP/1.1" or "SPDY/3.1" */
    public String getProtocol() {
        return protocol;
    }
}
