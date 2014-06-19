package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

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
	@JsonInteraction
	public String getContent() {
		return data("content");
	}

	/**
	 * The width of the frame
	 *
	 * @return The width of the frame
	 */
	@JsonInteraction
	public Integer getWidth() {
		return data("width", Integer.class);
	}

	/**
	 * The height of the frame
	 *
	 * @return The height of the frame
	 */
	@JsonInteraction
	public Integer getHeight() {
		return data("height", Integer.class);
	}

	/**
	 * If scrolling is allowed while the mouse is over the embedded media.
	 *
	 * @return If scrolling is allowed
	 */
	@JsonInteraction
	public Boolean doesAllowScrolling() {
		return data("scrolling", Boolean.class);
	}
}
