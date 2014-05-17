package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

/**
 * Represents the base class of all objects defined in the Reddit API, except for "Listing" and "more". More information
 * is available <a href="https://github.com/reddit/reddit/wiki/JSON">here</a>.
 *
 *
 * @author Matthew Dean
 */
public abstract class Thing extends RedditObject {

	public Thing(JsonNode dataNode) {
		super(dataNode);
	}

}
