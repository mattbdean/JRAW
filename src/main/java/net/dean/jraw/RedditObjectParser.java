package net.dean.jraw;

import net.dean.jraw.models.RedditObject;
import net.dean.jraw.models.core.Submission;
import org.codehaus.jackson.JsonNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This class is responsible for parsing JsonNodes into Things
 */
public class RedditObjectParser {

	/**
	 * Parses a JsonNode into a Thing
	 *
	 * @param rootNode   The root node of the Thing. Should only contain two elements: "kind", and "data".
	 * @param thingClass The type of Thing this JsonNode should be turned into
	 * @param <T>        The return type
	 * @return A new RedditObject
	 */
	public <T extends RedditObject> T parse(JsonNode rootNode, Class<T> thingClass) {
		try {
			// Instantiate a generic Thing
			Constructor<T> constructor = thingClass.getConstructor(JsonNode.class);
			return constructor.newInstance(rootNode.get("data"));
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			// Holy exceptions Batman!
			e.printStackTrace();
		}

		return null;
	}
}
