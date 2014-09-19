package net.dean.jraw.http;

import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditObjectParser;
import net.dean.jraw.models.RedditObject;
import net.dean.jraw.models.core.Comment;
import net.dean.jraw.models.core.Listing;
import net.dean.jraw.models.core.Submission;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonNode;

/**
 * This class provides automatic parsing and handling of ApiExceptions, as well as quick RedditObject and Listing creation
 */
public class RedditResponse extends RestResponse {

    protected static final RedditObjectParser REDDIT_OBJECT_PARSER = new RedditObjectParser();

    private final ApiException[] apiExceptions;

    /**
     * Instantiates a new RedditResponse.
     *
     * @param response The HttpResponse used to get the information
     */
    public RedditResponse(HttpResponse response) {
        this(response, ContentType.JSON);
    }

    /**
     * Instantiates a new RestResponse with an expected ContentType
     *
     * @param response The HttpResponse used to get the information
     * @param expected The expected ContentType
     */
    public RedditResponse(HttpResponse response, ContentType expected) {
        super(response, expected);

        if (contentType.equals(ContentType.HTML))
            JrawUtils.logger().warn("Received HTML from Reddit API instead of JSON. Are you sure you have access to this document?");

        ApiException[] errors = new ApiException[0];
        if (contentType.equals(ContentType.JSON)) {
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

        this.apiExceptions = errors;
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

}
