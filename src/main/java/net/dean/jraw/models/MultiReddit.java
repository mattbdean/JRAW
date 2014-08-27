package net.dean.jraw.models;

import net.dean.jraw.models.core.Thing;
import org.codehaus.jackson.JsonNode;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of subreddits. See <a href="http://www.reddit.com/r/multihub">here</a> for some examples.
 */
public class MultiReddit extends Thing implements Created {

    /**
     * Instantiates a new Thing
     *
     * @param dataNode The node to parse data from
     */
    public MultiReddit(JsonNode dataNode) {
        super(dataNode);
    }

    @Override
    public ThingType getType() {
        return ThingType.MULTI;
    }

    /**
     * Checks if the logged in user can edit this MultiReddit
     * @return If the logged in user can edit this MultiReddit
     */
    @JsonInteraction
    public boolean canEdit() {
        return data("can_edit", Boolean.class);
    }

    /**
     * Gets the name of the multireddit
     * @return The multireddit's name
     */
    @JsonInteraction
    public String getFullName() {
        return data("name");
    }

    /**
     * Gets the subreddits that are a part of this multireddit
     * @return A list of subreddits
     */
    @JsonInteraction
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
    @JsonInteraction
    public boolean isPrivate() {
        return data("visibility", Boolean.class);
    }

    /**
     * Gets the relative path to this multireddit
     * @return The relative path
     */
    @JsonInteraction
    public URI getPath() {
        return data("path", URI.class);
    }
}
