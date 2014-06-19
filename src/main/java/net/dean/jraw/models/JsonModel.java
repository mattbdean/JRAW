package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a way of retrieving data from a single JsonNode.
 */
public abstract class JsonModel {
	protected JsonNode data;
	private Map<String, Object> nodeCache;

	/**
	 * Instantiates a new JsonModel
	 *
	 * @param dataNode The node to parse data from
	 */
	public JsonModel(JsonNode dataNode) {
		this.data = dataNode;
		this.nodeCache = new HashMap<>();
	}

	public String data(String name) {
		return data(name, String.class);
	}

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

	public JsonNode getDataNode() {
		return data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		JsonModel that = (JsonModel) o;

		return data.equals(that.data);
	}


	@Override
	public int hashCode() {
		return data.hashCode();
	}
}
