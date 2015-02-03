package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a list of Thing IDs that could not be downloaded because there were too many to list. This element
 * is most commonly found as the last element of a Listing in the JSON response.
 * See <a href="https://github.com/reddit/reddit/wiki/JSON#more">here</a> for more information.
 */
@Model(kind = Model.Kind.MORE)
public class More extends Thing {

    /**
     * Instantiates a new More
     *
     * @param dataNode The JsonNode to gather data from
     */
    public More(JsonNode dataNode) {
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

    @JsonProperty
    public String getParentId() {
        return data("parent_id");
    }

    /**
     * Gets a list of Thing IDs
     * @return a list of Thing IDs
     */
    @JsonProperty
    public List<String> getChildrenIds() {
        List<String> ids = new ArrayList<>();
        for (JsonNode child : data.get("children")) {
            ids.add(child.textValue());
        }

        return ids;
    }
}
