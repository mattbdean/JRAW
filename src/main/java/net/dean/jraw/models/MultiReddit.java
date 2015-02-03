package net.dean.jraw.models;

import net.dean.jraw.models.attr.Created;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a collection of subreddits. See <a href="http://www.reddit.com/r/multihub">here</a> for some examples.
 */
@Model(kind = Model.Kind.MULTIREDDIT)
public class MultiReddit extends Thing implements Created {

    /**
     * Instantiates a new Thing
     *
     * @param dataNode The node to parse data from
     */
    public MultiReddit(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * Checks if the logged in user can edit this MultiReddit
     * @return If the logged in user can edit this MultiReddit
     */
    @JsonProperty
    public boolean canEdit() {
        return data("can_edit", Boolean.class);
    }

    /**
     * Gets the name of the multireddit
     * @return The multireddit's name
     */
    @JsonProperty
    public String getFullName() {
        return data("name");
    }

    /**
     * Gets the subreddits that are a part of this multireddit
     * @return A list of subreddits
     */
    @JsonProperty
    public List<String> getSubreddits() {
        List<String> subreddits = new ArrayList<>();

        JsonNode node = data.get("subreddits");
        for (JsonNode subredditNode : node) {
            subreddits.add(subredditNode.get("name").asText());
        }

        return subreddits;
    }

    /**
     * Checks if this multireddit is restricted to its owner
     * @return If this mutlireddit is private
     */
    @JsonProperty
    public boolean isPrivate() {
        return data("visibility", Boolean.class);
    }

    /**
     * Gets the relative path to this multireddit. It will be in the format of {@code /user/{username}/m/{multiname}}
     * @return The relative path
     */
    @JsonProperty
    public String getPath() {
        return data("path");
    }

    @Override
    public Date getCreated() {
        return _getCreated();
    }

    @Override
    public Date getCreatedUtc() {
        return _getCreatedUtc();
    }
}
