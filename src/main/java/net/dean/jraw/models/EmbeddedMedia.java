package net.dean.jraw.models;

import net.dean.jraw.models.meta.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a simplified version of oEmbed data that includes embedded HTML
 */
public class EmbeddedMedia extends JsonModel {

    /**
     * Instantiates a new EmbeddedMedia
     *
     * @param mediaEmbedNode The node to parse data from
     */
    public EmbeddedMedia(JsonNode mediaEmbedNode) {
        super(mediaEmbedNode);
    }

    /**
     * Returns the HTML that will be embedded into the website
     *
     * @return Embedded HTML
     */
    @JsonProperty
    public String getContent() {
        return data("content");
    }

    /**
     * The width of the frame
     *
     * @return The width of the frame
     */
    @JsonProperty
    public Integer getWidth() {
        return data("width", Integer.class);
    }

    /**
     * The height of the frame
     *
     * @return The height of the frame
     */
    @JsonProperty
    public Integer getHeight() {
        return data("height", Integer.class);
    }

    /**
     * If scrolling is allowed while the mouse is over the embedded media.
     *
     * @return If scrolling is allowed
     */
    @JsonProperty
    public Boolean doesAllowScrolling() {
        return data("scrolling", Boolean.class);
    }
}
