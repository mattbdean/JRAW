package net.dean.jraw.http;

import com.squareup.okhttp.Response;
import net.dean.jraw.ApiException;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.models.RedditObject;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import org.codehaus.jackson.JsonNode;

/**
 * This class provides automatic parsing of ApiExceptions, as well as quick RedditObject and Listing
 * creation. Note that constructing a RedditResponse will <em>not</em> throw an ApiException. This must be done by the
 * implementer. To see if the response has any errors, use {@link #hasErrors()} and {@link #getErrors()}
 */
public class RedditResponse extends RestResponse {

    private final ApiException[] apiExceptions;

    /**
     * Instantiates a new RedditResponse
     *
     * @param response The Response that will be encapsulated by this object
     */
    public RedditResponse(Response response) {
        super(response);

        if (JrawUtils.typeComparison(type, MediaTypes.HTML.type())) {
            JrawUtils.logger().warn("Received HTML from Reddit API instead of JSON. Are you sure you have access to this document?");
        }
        ApiException[] errors = new ApiException[0];
        if (JrawUtils.typeComparison(type, MediaTypes.JSON.type()) && !raw.isEmpty()) {
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
        return JrawUtils.parseJson(rootNode, thingClass);
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

}
