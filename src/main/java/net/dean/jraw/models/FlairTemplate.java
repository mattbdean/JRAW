package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

/**
 * Represents a single flair template on a subreddit
 */
public class FlairTemplate extends JsonModel {
    /**
     * Instantiates a new FlairTemplate
     *
     * @param dataNode The node to parse data from
     */
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

    /**
     * Checks if the template's text is editable
     * @return False if this template is the current flair, or if the template's text is editable if else
     */
    @JsonProperty
    public Boolean isTextEditable() {
        if (!data.has("flair_text_editable")) {
            return false;
        }

        return data("flair_text_editable", Boolean.class);
    }

    /**
     * Either "left" or "right"
     */
    @JsonProperty
    public String getPosition() {
        return flairData("position");
    }

    /**
     * The flair's text
     */
    @JsonProperty
    public String getText() {
        return flairData("text");
    }

    private String flairData(String key) {
        return data("flair_" + key);
    }
}
