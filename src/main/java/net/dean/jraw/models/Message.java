package net.dean.jraw.models;

import net.dean.jraw.managers.InboxManager;
import net.dean.jraw.models.attr.Distinguishable;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.MessageSerializer;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents any data that can appear in a user's inbox. The two main subclasses of this class are
 * {@link PrivateMessage} (for when a user contacts another user directly) and {@link CommentMessage} (for when a user
 * replies to another user's comment).
 */
@Model(kind = Model.Kind.ABSTRACT, serializer = MessageSerializer.class)
public abstract class Message extends Contribution implements Distinguishable {
    /**
     * Instantiates a new Message
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

    /** Gets the fullname of the first message's ID */
    @JsonProperty
    public String getFirstMessage() {
        return data("first_message_name");
    }

    /**
     * Checks if this message has been read
     *
     * @see InboxManager#setRead(Message, boolean)
     */
    @JsonProperty
    public Boolean isRead() {
        return data.has("new") && !data("new", Boolean.class);
    }

    /**
     * Gets the fullname of the submission/comment/message that this is a reply to
     * @return The fullname of the host, or null if this is a top-level comment or private message.
     */
    @JsonProperty(nullable = true)
    public String getParentId() {
        return data("parent_id");
    }

    /** Gets the subject of the message */
    @JsonProperty
    public String getSubject() {
        return data("subject");
    }

    /**
     * Gets the subreddit this was posted in
     * @return The subreddit this was posted in, or null if this message is not a comment
     */
    @JsonProperty(nullable = true)
    public String getSubreddit() {
        return data("subreddit");
    }

    /**
     * Checks if this message is a comment. If true, then one may assume that this Message is an instance of
     * {@link CommentMessage}, else {@link PrivateMessage}.
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
