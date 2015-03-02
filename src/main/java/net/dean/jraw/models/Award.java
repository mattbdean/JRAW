package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents an award (trophy) displayed in a user's trophy case on the website.
 */
@Model(kind = Model.Kind.AWARD)
public class Award extends Thing {
    /**
     * Instantiates a new Trophy
     */
    public Award(JsonNode dataNode) {
        super(dataNode);
    }

    /** The URL to the 70x70 version of the icon */
    @JsonProperty
    public String getIcon() {
        return data("icon_70");
    }

    /** The URL to the 40x40 version of the icon */
    @JsonProperty
    public String getIconSmall() {
        return data("icon_40");
    }

    /** Optional text that describes to what degree the award was achieved */
    @JsonProperty(nullable = true)
    public String getDescription() {
        return data("description");
    }

    /** The award's ID (different than the normal ID) */
    @JsonProperty
    public String getTrophyId() {
        return data("award_id");
    }

    /** An external link explaining this award */
    @JsonProperty
    public String getAboutUrl() {
        return data("url");
    }
}
