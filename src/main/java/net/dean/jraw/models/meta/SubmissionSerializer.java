package net.dean.jraw.models.meta;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

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
            // We cannot know what CommentSort is being used, so Submissions with comments should be parsed manually.
            throw new IllegalArgumentException("A Submission with comments should not be parsed by a JsonSerializer");
        }

        // Submission without comments
        return (T) new Submission(node.get("data"));
    }

    /**
     * Serializes a Submission with comments.
     * @param node The root node of the request. Should be an array node.
     * @param sort How the comments were sorted and how future comments in the tree will be requested
     */
    public static Submission withComments(JsonNode node, CommentSort sort) {
        // Submission with comments requested
        JsonNode commentListingDataNode = node.get(1).get("data");
        Listing<Comment> comments = new Listing<>(commentListingDataNode, Comment.class);
        // First element in array is a listing containing one submission: the one requested
        JsonNode submissionData = node.get(0).get("data").get("children").get(0).get("data");
        String submissionName = submissionData.get("name").asText();
        return new Submission(submissionData, new CommentNode(submissionName, comments, comments.getMoreChildren(), sort));
    }
}
