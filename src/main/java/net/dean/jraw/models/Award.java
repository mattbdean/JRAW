package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

/**
 * Represents an award
 */
public class Award extends Thing {
    /**
     * Instantiates a new Trophy
     *
     * @param dataNode The node to parse data from
     */
    public Award(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * The URL to the 70x70 version of the icon
     */
    @JsonProperty
    public String getIcon() {
        return data("icon_70");
    }

    /**
     * The URL to the 40x40 version of the icon
     */
    @JsonProperty
    public String getIconSmall() {
        return data("icon_40");
    }

    /**
     * Optional text that describes to what degree the award was achieved
     */
    @JsonProperty(nullable = true)
    public String getDescription() {
        return data("description");
    }

    /**
     * The award's ID (different than the normal ID)
     */
    @JsonProperty
    public String getTrophyId() {
        return data("award_id");
    }

    /**
     * An external link explaining this award
     */
    @JsonProperty
    public String getAboutUrl() {
        return data("url");
    }

    @Override
    public ThingType getType() {
        return ThingType.AWARD;
    }
}
