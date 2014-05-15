package net.dean.jraw;

import net.dean.jraw.models.JsonAttribute;
import net.dean.jraw.models.Thing;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * This class is used to show the result of a request to a RESTful web service, such as Reddit's JSON API.
 */
public class RestResponse {
	/** The ObjectMapper used to map parse the response JSON */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	/**
	 * This map is a list that gets added to dynamically after ${@link #getJsonAttributeFields(Class)} is called.
	 * Instead of using reflection every time to get a list of Fields that have the JsonAttribute annotation, the resulting
	 * list is added to this map
	 */
	private static Map<Class, List<Field>> jsonAttributeFieldCache = new HashMap<>();

	/** A list of all thea headers received from the server */
	private List<Header> headers;

	/** The root node of the JSON */
	private JsonNode rootNode;

	/** The raw data of the response's content */
	private String raw;

	/**
	 * Instantiates a new RestResponse. This constructor also reads the contents of the input stream and parses it into
	 * the root JsonNode, and then consumes the response's entity.
	 *
	 * @param response The HttpResponse used to get the information
	 * @throws JsonProcessingException If the content received from the response's InputStream was not valid JSON
	 */
	public RestResponse(HttpResponse response) throws JsonProcessingException {
		this.headers = new ArrayList<>(Arrays.asList(response.getAllHeaders()));

		try {
			// http://stackoverflow.com/a/5445161
			Scanner s = new Scanner(response.getEntity().getContent()).useDelimiter("\\A");
			this.raw = s.hasNext() ? s.next() : "";

			this.rootNode = OBJECT_MAPPER.readTree(raw);
			EntityUtils.consume(response.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tries to map the data of this JSON response's "data" map to a class that extends Thing.
	 * @param thingClass The Thing to map the response data to
	 * @param <T> The type of thing to map the data to
	 * @return A new Thing with all of the fields assigned to values of the "data" map of this request's JSON
	 */
	public <T extends Thing> T to(Class<T> thingClass) {
		JsonNode dataNode = rootNode.get("data");

		try {
			T t = thingClass.newInstance();

			for (Field attributeField : getJsonAttributeFields(thingClass)) {
				JsonAttribute jsonAttribute = attributeField.getAnnotation(JsonAttribute.class);
				if (jsonAttribute.jsonName().equals("kind")) {
					// Ignore "kind", it is set above
					continue;
				}

				if (!dataNode.has(jsonAttribute.jsonName())) {
					System.err.printf("Warning: Unknown attribute \"%s\" while creating a \"%s\"\n", jsonAttribute.jsonName(),
							thingClass.getName());
					// Skip to the next attribute
					continue;
				}

				// Update the field's value
				set(t, attributeField, dataNode.get(jsonAttribute.jsonName()));
			}

			return t;
		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Sets a field in a given Thing to the value of a JsonNode based on the field's type.
	 *
	 * @param t The Thing set the value of its field to
	 * @param attributeField The field in the Thing
	 * @param node The JsonNode that is associated with the field
	 */
	private void set(Thing t, Field attributeField, JsonNode node) {
		if (node.isNull()) {
			return;
		}

		boolean originallyAccessible = attributeField.isAccessible();

		// Set it accessible to we can modify it even if the field is private
		if (!originallyAccessible) {
			attributeField.setAccessible(true);
		}

		Class type = attributeField.getType();

		try {
			// Let the ugliness ensue
			if (type.equals(Boolean.class)) {
				attributeField.set(t, node.getBooleanValue());
			} else if (type.equals(Character.class)) {
				attributeField.set(t, node.getTextValue().toCharArray()[0]);
			} else if (type.equals(Double.class)) {
				attributeField.set(t, node.getDoubleValue());
			} else if (type.equals(Float.class)) {
				attributeField.set(t, (float) node.getDoubleValue());
			} else if (type.equals(Integer.class)) {
				attributeField.set(t, node.getIntValue());
			} else if (type.equals(Long.class)) {
				attributeField.set(t, node.getLongValue());
			} else if (type.equals(Short.class)) {
				attributeField.set(t, (short) node.getIntValue());
			} else if (type.equals(String.class)) {
				attributeField.set(t, node.getTextValue());
			} else {
				System.err.printf("Warning: Unsupported data type on JsonAttribute field %s: %s\n",
						(attributeField.getDeclaringClass().getName() + "." + attributeField.getName()),
						attributeField.getType().getName());
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		// Set the value, now return it to its original state
		if (!originallyAccessible) {
			attributeField.setAccessible(false);
		}
	}


	/**
	 * Gets a list of fields that have the AttributeField annotation attached to them. Also searches the superclass up
	 * until ${@link net.dean.jraw.models.Thing} for fields.
	 *
	 * @param thingClass The class to search for
	 * @return A list of fields that have the JsonAttribute annotation
	 */
	private List<Field> getJsonAttributeFields(Class<? extends Thing> thingClass) {
		// If the results have already been found, use them
		if (jsonAttributeFieldCache.containsKey(thingClass)) {
			return jsonAttributeFieldCache.get(thingClass);
		}

		List<Field> attributes = new ArrayList<>();

		Class clazz = thingClass;

		while (clazz != null) {
			for (Field f : clazz.getDeclaredFields()) {
				if (f.isAnnotationPresent(JsonAttribute.class)) {
					attributes.add(f);
				}
			}

			if (clazz.equals(Thing.class)) {
				// Already at the highest level and we don't need to scan Object
				break;
			}

			// Can still go deeper...
			clazz = clazz.getSuperclass();
		}

		// Add the results to the cache so we don't have to use reflection again
		jsonAttributeFieldCache.put(thingClass, attributes);

		return attributes;
	}

	/**
	 * Gets a Header object by name from the list of headers
	 * @param name The name of the header, such as <code>Content-Length</code>
	 * @return A Header object with a given name
	 */
	public Header getHeader(String name) {
		for (Header h : headers) {
			if (h.getName().equalsIgnoreCase(name)) {
				return h;
			}
		}

		return null;
	}

	/**
	 * Gets all the headers sent by the server
	 * @return A List of Header objects
	 */
	public List<Header> getHeaders() {
		return headers;
	}

	/**
	 * Gets the root JsonNode
	 * @return The root JsonNode
	 */
	public JsonNode getRootNode() {
		return rootNode;
	}

	/**
	 * Gets the raw data returned from the request
	 * @return The raw data of the request
	 */
	public String getRaw() {
		return raw;
	}
}
