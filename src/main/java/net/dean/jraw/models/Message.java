package net.dean.jraw.models;

import net.dean.jraw.models.attr.Distinguishable;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.MessageSerializer;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

@Model(kind = Model.Kind.ABSTRACT, serializer = MessageSerializer.class)
public abstract class Message extends Contribution implements Distinguishable {
    /**
     * Instantiates a new Thing
     *
     * @param dataNode The node to parse data from
     */
    public Message(JsonNode dataNode) {
        super(dataNode);
    }

    @JsonProperty
    public String getAuthor() {
        return data("author");
    }

    @JsonProperty
    public String getBody() {
        return data("body");
    }

    /**
     * Gets the full name of the first message's ID
     * @return The first message
     */
    @JsonProperty
    public String getFirstMessage() {
        return data("first_message_name");
    }

    /**
     * Checks if this message is unread
     * @return If this message is unread
     */
    @JsonProperty
    public Boolean isRead() {
        return data.has("new") && !data("new", Boolean.class);
    }

    /**
     * Gets the fullname of the submission/comment/message that this is a reply to, or null if this is a top-level comment
     * or private message.
     * @return The ID of the message's parent
     */
    @JsonProperty(nullable = true)
    public String getParentId() {
        return data("parent_id");
    }

    /**
     * Gets the subject of the message
     * @return The subject
     */
    @JsonProperty
    public String getSubject() {
        return data("subject");
    }

    /**
     * Gets the subreddit this was posted in, or null if this message is not a comment
     * @return The subreddit this was posted in
     */
    @JsonProperty(nullable = true)
    public String getSubreddit() {
        return data("subreddit");
    }

    /**
     * Checks if this message is a comment. If true, then one may assume that this Message is an instance of
     * {@link CommentMessage}.
     * @return True if this message is a comment, false if it is a private message
     */
    @JsonProperty
    public Boolean isComment() {
        return data("was_comment", Boolean.class);
    }

    @Override
    public DistinguishedStatus getDistinguishedStatus() {
        return _getDistinguishedStatus();
    }
}
