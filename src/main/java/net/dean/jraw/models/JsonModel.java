package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides an abstract model for retrieving data from a JSON node, although not necessarily relating to the
 * Reddit API.
 */
public abstract class JsonModel {
	protected final JsonNode data;
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

	/**
	 * Retrieves a String value from the JSON node.
	 *
	 * @param name The key to look up in the JSON node.
	 * @return An String in the JSON node
	 */
	public String data(String name) {
		return data(name, String.class);
	}

	/**
	 * Retrieves a value from the JSON node (provided in the constructor) of type T. The resulting object is cached for
	 * later use.
	 *
	 * @param name The key to look up in the JSON node.
	 * @param type The wanted return value. Supported values are any class representing a primitive data type, such as
	 *             {@link Integer} or {@link Boolean}.
	 * @param <T> The desired return data type
	 * @return An object of type T in the JSON node
	 */
	@SuppressWarnings("unchecked")
	public <T> T data(String name, Class<T> type) {
		// Check the node cache first
		if (nodeCache.containsKey(name)) {
			Object cachedObject = nodeCache.get(name);
			if (!cachedObject.getClass().equals(type)) {
				System.err.printf("Cached object and return type did not match for \"%s\" (wanted %s, got %s)\n",
						name, type, cachedObject.getClass());
				// Show a warning and "rediscover" the variable using the rest of the method
			} else {
				return (T) nodeCache.get(name);
			}
		}

		// Make sure the key is actually there
		if (!data.has(name)) {
			return null;
		}

		JsonNode node = data.get(name);

		if (node.isNull()) {
			return null;
		}

		T returnVal;

		// Try to return the desired value
		if (type.equals(Boolean.class))
			returnVal = (T) Boolean.valueOf(node.asBoolean());
		else if (type.equals(Double.class))
			returnVal = (T) Double.valueOf(node.asDouble());
		else if (type.equals(Integer.class))
			returnVal = (T) Integer.valueOf(node.asInt());
		else if (type.equals(Long.class))
			returnVal = (T) Long.valueOf(node.asLong());
		else if (type.equals(Float.class))
			returnVal = (T) Float.valueOf(node.asText());
		else
			// Assume String
			returnVal = (T) String.valueOf(node.asText());

		// Put the object in the cache
		nodeCache.put(name, returnVal);

		return returnVal;
	}

	public JsonNode getDataNode() {
		return data;
	}

	/**
	 * Convenience method to be used in toString() methods that returns the String literal "null" if the value is null.
	 * If the object's toString() method throws a NullPointerException, then the String literal "(NullPointerException)
	 * is returned. If no exceptions were thrown, then this method returns {@code val.toString()}.
	 *
	 * @param val The object to evaluate
	 * @return A string representation of the object
	 */
	protected String asString(Object val) {
		if (val == null) {
			return "null";
		}

		try {
			return val.toString();
		} catch (NullPointerException e) {
			return "(NullPointerException)";
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		JsonModel that = (JsonModel) o;

		return (data != null ? !data.equals(that.data) : that.data != null);
	}

	@Override
	public int hashCode() {
		return data != null ? data.hashCode() : 0;
	}

	@Override
	public String toString() {
		// Since JsonModel subclasses don't have many meaningful fields (except for data), a dynamic toString() is
		// more suited for better representing the JsonModel

		Class<? extends JsonModel> clazz = getClass();
		StringBuilder sb = new StringBuilder(clazz.getSimpleName() + " {");

		getJsonInteractionMethods(clazz).stream().filter(AccessibleObject::isAccessible).forEach(m -> {
			try {
				// "methodName()=>returnVal"
				sb.append(m.getName()).append("()=>").append(asString(m.invoke(this)));
			} catch (IllegalAccessException e) {
				System.err.println("IllegalAccessException. This really shouldn't happen.");
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		});

		return sb.toString();
	}

	/**
	 * Gets a list of fields that have the JsonInteraction annotation attached to them. Also searches the superclass up
	 * until ${@link net.dean.jraw.models.core.Thing} for fields.
	 *
	 * @param thingClass The class to search in
	 * @return A list of fields that have the JsonInteraction annotation
	 */
	public static List<Method> getJsonInteractionMethods(Class<? extends JsonModel> thingClass) {
		List<Method> methods = new ArrayList<>();

		Class clazz = thingClass;
		List<Method> toObserve = new ArrayList<>();

		while (clazz != null) {
			toObserve.addAll(Arrays.asList(clazz.getDeclaredMethods()));
			for (Class<?> interf : clazz.getInterfaces()) {
				toObserve.addAll(Arrays.asList(interf.getDeclaredMethods()));
			}

			if (clazz.equals(RedditObject.class)) {
				// Already at the highest level and we don't need to scan Object
				break;
			}

			// Can still go deeper...
			clazz = clazz.getSuperclass();
		}

		methods.addAll(toObserve.stream().filter(m -> m.isAnnotationPresent(JsonInteraction.class)).collect(Collectors.toList()));

		return methods;
	}
}
