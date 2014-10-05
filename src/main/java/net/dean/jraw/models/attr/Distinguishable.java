package net.dean.jraw.models.attr;

import net.dean.jraw.models.DistinguishedStatus;
import net.dean.jraw.models.JsonInteraction;
import org.codehaus.jackson.JsonNode;

/**
 * Indicates this Thing was posted by a redditor of elevated role in the website, such as an administrator or moderator.
 * See {@link net.dean.jraw.models.DistinguishedStatus} for a full list of roles.
 */
public interface Distinguishable {
    /**
     * Gets the role of the poster of this Thing
     * @return The role of the poster of this Thing
     */
    @JsonInteraction
    public default DistinguishedStatus getDistinguishedStatus() {
        String distinguished = getDataNode().get("distinguished").getTextValue();

        if (distinguished == null) {
            return DistinguishedStatus.NORMAL;
        }

        return DistinguishedStatus.getByJsonValue(distinguished);
    }

    /**
     * See {@link net.dean.jraw.models.JsonModel#getDataNode()}
     * @return The JsonNode to use for methods annotated with @JsonInteraction
     */
    public JsonNode getDataNode();
}
