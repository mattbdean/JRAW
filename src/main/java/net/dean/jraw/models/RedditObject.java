package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * The class that all models implement. Contains values that all data types returned from the Reddit API have, including
 * "more" and "Listing".
 * <p>
 * However, in this particular project, a RedditObject is simply a carrier of a JsonNode. All classes that extend RedditObject
 * are simply interfaces to grab data from this JsonNode.
 */
public abstract class RedditObject {
	protected JsonNode data;
	private Map<String, Object> nodeCache;

	/**
	 * Instantiates a new RedditObject
	 *
	 * @param dataNode The node to parse data from
	 */
	public RedditObject(JsonNode dataNode) {
		this.data = dataNode;
		this.nodeCache = new HashMap<>();
	}

	/**
	 * Gets the type of this Thing. Will always be constant for every class. For example, every
	 * ${@link net.dean.jraw.models.core.Account} class will always return ${@link net.dean.jraw.models.ThingType#ACCOUNT}.
	 */
	public abstract ThingType getType();

	@SuppressWarnings("unchecked")
	public <T> T data(String name, Class<T> type) {
		if (nodeCache.containsKey(name)) {
			Object cachedObject = nodeCache.get(name);
			if (!cachedObject.getClass().equals(type)) {
				System.err.printf("Cached object and return type did not match for \"%s\" (wanted %s, got %s)\n",
						name, type, cachedObject.getClass());
			}
			return (T) nodeCache.get(name);
		}

		if (!data.has(name)) {
			return null;
		}

		JsonNode node = data.get(name);

		T returnVal = null;

		if (type.equals(String.class))
			returnVal = (T) node.asText();
		else if (type.equals(Boolean.class))
			returnVal = (T) Boolean.valueOf(node.asBoolean());
		else if (type.equals(Double.class))
			returnVal = (T) Double.valueOf(node.asDouble());
		else if (type.equals(Integer.class))
			returnVal = (T) Integer.valueOf(node.asInt());
		else if (type.equals(Long.class))
			returnVal = (T) Long.valueOf(node.asLong());

		nodeCache.put(name, returnVal);

		return returnVal;
	}

	/**
	 * Gets this Thing's full identifier, e.g. "8xwlg"
	 */
	@JsonInteraction
	public String getId() {
		return data("id", String.class);
	}

	/**
	 * Gets the fullname of this Thing, e.g. "t1_c3v7f8u"
	 */
	@JsonInteraction
	public String getName() {
		return data("name", String.class);
	}

	public JsonNode getDataNode() {
		return data;
	}
}
