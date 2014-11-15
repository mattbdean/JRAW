package net.dean.jraw.models;

import net.dean.jraw.models.attr.Created;
import org.codehaus.jackson.JsonNode;

import java.util.Date;

/**
 * Represents an administrative action from a moderator of a subreddit
 */
public class ModAction extends Thing implements Created {
    /**
     * Instantiates a new ModAction
     *
     * @param dataNode The node to parse data from
     */
    public ModAction(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * The action's description. May be null if the action is generic such as "sticky" or "unsticky"
     */
    @JsonProperty(nullable = true)
    public String getDescription() {
        return data("description");
    }

    /**
     * The moderator's ID (note: not full name)
     * @return An alphanumeric ID representing the moderator who did this action. For example: "ejdfb"
     */
    @JsonProperty
    public String getModeratorId() {
        return data("mod_id36");
    }

    /**
     * The name of the moderator who did the action
     */
    @JsonProperty
    public String getModerator() {
        return data("mod");
    }

    /**
     * The subreddit in which this action occurred. May be null.
     */
    @JsonProperty(nullable = true)
    public String getSubreddit() {
        return data("subreddit");
    }

    /**
     * The permalink to the post in question. For example:
     * "/r/jraw_testing2/comments/2m2gnc/self_post_test_epoch1415796343364/"
     */
    @JsonProperty
    public String getTargetPermalink() {
        return data("target_permalink");
    }

    /**
     * Extra details about the action. Will be an empty string if there are none.
     */
    @JsonProperty
    public String getDetails() {
        return data("details");
    }

    /**
     * The action that was performed. For example, "sticky", "unsticky"
     */
    @JsonProperty
    public String getAction() {
        return data("action");
    }

    /**
     * The name of the author whose post was targeted
     */
    @JsonProperty
    public String getTargetAuthor() {
        return data("jraw_test");
    }

    /**
     * The full name of the author whose post was targeted
     */
    @JsonProperty
    public String getTargetFullName() {
        return data("target_fullname");
    }

    /**
     * The subreddit's ID in which this action occurred. For example, "31qvo"
     */
    @JsonProperty
    public String getSubredditId() {
        return data("sr_id36");
    }

    @Override
    public ThingType getType() {
        return ThingType.MODACTION;
    }

    @Override
    public Date getCreated() {
        // No "created" field, on "created_utc"
        return getCreatedUtc();
    }

    @Override
    public Date getCreatedUtc() {
        return _getCreatedUtc();
    }
}
