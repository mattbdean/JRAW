package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

/**
 * Indicates this Thing can be posted by a redditor of elevated privilege
 */
public interface Distinguishable {
    /**
     * Gets the privilege of the poster of this Thing
     * @return The privilege of the poster of this Thing
     */
    @JsonInteraction
    default DistinguishedState getDistinguishedState() {
        String distinguished = getDataNode().get("distinguished").getTextValue();

        if (distinguished == null) {
            return DistinguishedState.NORMAL;
        }

        return DistinguishedState.getByJsonValue(distinguished);
    }

    /**
     * See {@link JsonModel#getDataNode()}
     * @return The JsonNode to use for methods annotated with @JsonInteraction
     */
    public JsonNode getDataNode();
}
