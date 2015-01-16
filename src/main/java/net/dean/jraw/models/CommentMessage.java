package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import org.codehaus.jackson.JsonNode;

/**
 * This class adds extra properties to {@link Message} that is only available in some inbox entries. This class is
 * necessary because the kind is not "t4" (message), but instead is "t1" (comment).
 */
@Model(kind = Model.Kind.COMMENT)
public class CommentMessage extends Message {
    /**
     * Instantiates a new Thing
     *
     * @param dataNode The node to parse data from
     */
    public CommentMessage(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * Gets the title of the link this comment was posted in
     * @return The parent link's title
     */
    @JsonProperty
    public String getLinkTitle() {
        return data("link_title");
    }

    /**
     * If this message represents a comment, then this method will return the way in which the logged in user voted.
     * If this message represents a private message, then this method will always return null.
     * @return The way in which the logged in user voted
     */
    @JsonProperty(nullable = true)
    public VoteDirection getVote() {
        // If "was_comment" == false then "likes" will not exist
        if (!isComment()) {
            return null;
        }

        JsonNode likes = getDataNode().get("likes");
        if (likes.isNull()) {
            return VoteDirection.NO_VOTE;
        }

        return likes.getBooleanValue() ? VoteDirection.UPVOTE : VoteDirection.DOWNVOTE;
    }
}
