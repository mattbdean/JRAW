package net.dean.jraw.models.meta;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import org.codehaus.jackson.JsonNode;

/**
 * This class is responsible for serializing Submissions, since they are presented differently than other models. When a
 * submission is requested with comments (with {@link RedditClient#getSubmission(String)} or such), then the root JSON
 * node is actually an array containing two elements. The first is a Listing of authors (always one Account instance),
 * and the second is a Listing of Comments.
 */
public class SubmissionSerializer implements JsonSerializer<Submission> {
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Submission> T parse(JsonNode node, Class<T> clazz, Model.Kind kind) {
        if (node.isArray()) {
            // Submission with comments requested
            JsonNode commentListingDataNode = node.get(1).get("data");
            Listing<Comment> comments = new Listing<>(commentListingDataNode, Comment.class);
            return (T) new Submission(node.get(0).get("data").get("children").get(0).get("data"), comments);
        }

        // Submission without comments
        return (T) new Submission(node.get("data"));
    }
}
