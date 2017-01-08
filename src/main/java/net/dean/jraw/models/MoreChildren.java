package net.dean.jraw.models;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a list of Thing IDs. This element, when present, is the last element of a Listing in the JSON
 * response. The purpose of this data structure is to notify the client that there are more replies to a comment than
 * what reddit is willing to initially show.
 *
 * @see CommentNode#getMoreChildren()
 * @see CommentNode#loadMoreComments(RedditClient)
 */
@Model(kind = Model.Kind.MORE)
public final class MoreChildren extends Thing {

    /**
     * Instantiates a new MoreChildren
     *
     * @param dataNode The JsonNode to gather data from
     */
    public MoreChildren(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * Gets the amount of IDs in this list
     * @return The amount of IDs in this list
     */
    @JsonProperty
    public Integer getCount() {
        return data("count", Integer.class);
    }

    /**
     * Gets the localized amount of IDs in this list
     * @return The localized amount of IDs in this list
     */
    public String getLocalizedCount() {
        try {
            return NumberFormat.getInstance().format(getCount());
        } catch (final IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Gets the fullname of the comment under which the new comments should be appended. This could also return the
     * submission's fullname.
     */
    @JsonProperty
    public String getParentId() {
        return data("parent_id");
    }

    /** Gets a list of comment IDs that were not included in the original request. */
    @JsonProperty
    public List<String> getChildrenIds() {
        List<String> ids = new ArrayList<>();
        for (JsonNode child : data.get("children")) {
            ids.add(child.textValue());
        }

        return ids;
    }
}
