package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

/**
 * The class that all models implement. Contains values that all data types returned from the Reddit API have, including
 * "more" and "Listing".
 *
 * However, in this particular project, a RedditObject is simply a carrier of a JsonNode. All classes that extend RedditObject
 * are simply interfaces to grab data from this JsonNode.
 */
public abstract class RedditObject {
	protected JsonNode data;

	/**
	 * Instantiates a new RedditObject
	 * @param dataNode The node to parse data from
	 */
	public RedditObject(JsonNode dataNode) {
		this.data = dataNode;
	}

	/**
	 * Gets the type of this Thing. Will always be constant for every class. For example, every
	 * ${@link net.dean.jraw.models.core.Account} class will always return ${@link net.dean.jraw.models.ThingType#ACCOUNT}.
	 */
	public abstract ThingType getType();

	protected JsonNode data(String name) {
		return data.get(name);
	}

	/** Gets this Thing's full identifier, e.g. "8xwlg" */
	@JsonInteraction
	public String getId() {
		return data("id").getTextValue();
	}

	/** Gets the fullname of this Thing, e.g. "t1_c3v7f8u" */
	@JsonInteraction
	public String getName() {
		return data("name").getTextValue();
	}

	public JsonNode getDataNode() {
		return data;
	}
}
