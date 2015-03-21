package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents a single flair template on a subreddit
 */
public final class FlairTemplate extends JsonModel {
    /** Instantiates a new FlairTemplate */
    public FlairTemplate(JsonNode dataNode) {
        super(dataNode);
    }

    /**
     * Gets the CSS class that will be used to display this flair
     */
    @JsonProperty
    public String getCssClass() {
        return flairData("css_class");
    }

    /**
     * Gets the ID of this flair template
     */
    @JsonProperty(nullable = true)
    public String getId() {
        return flairData("template_id");
    }

    /** Checks if the template's text can be changed by the user. */
    @JsonProperty
    public Boolean isTextEditable() {
        if (!data.has("flair_text_editable")) {
            return false;
        }

        return data("flair_text_editable", Boolean.class);
    }

    /** Where the flair will appear relative to the title/username: Either "left" or "right" */
    @JsonProperty
    public String getPosition() {
        return flairData("position");
    }

    /** The value of the flair */
    @JsonProperty
    public String getText() {
        return flairData("text");
    }

    private String flairData(String key) {
        return data("flair_" + key);
    }
}
