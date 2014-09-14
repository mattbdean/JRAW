package net.dean.jraw;

import net.dean.jraw.models.RedditObject;
import net.dean.jraw.models.ThingType;
import net.dean.jraw.models.core.Comment;
import net.dean.jraw.models.core.Submission;
import net.dean.jraw.models.Contribution;
import org.codehaus.jackson.JsonNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This class is responsible for parsing JsonNodes into RedditObjects
 */
public class RedditObjectParser {

    /**
     * Parses a JsonNode into a RedditObject
     *
     * @param rootNode   The root node of the Thing. Should only contain two elements: "kind", and "data".
     * @param thingClass The type of Thing this JsonNode should be turned into
     * @param <T>        The return type
     * @return A new RedditObject
     */
    @SuppressWarnings("unchecked")
    public <T extends RedditObject> T parse(JsonNode rootNode, Class<T> thingClass) {
        if (thingClass.equals(Contribution.class)) {
            switch (ThingType.getByPrefix(rootNode.get("kind").asText())) {
                case LINK:
                    return (T) new Submission(rootNode.get("data"));
                case COMMENT:
                    return (T) new Comment(rootNode.get("data"));
                default:
                    throw new IllegalArgumentException("Class " + thingClass.getName() + " is not applicable for Contribution");
            }
        }
        try {
            // Instantiate a generic Thing
            Constructor<T> constructor = thingClass.getConstructor(JsonNode.class);
            return constructor.newInstance(rootNode.get("data"));
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            // Holy exceptions Batman!
            JrawUtils.logger().error("Could not create the Thing ({})", thingClass.getName(), e);
        }

        return null;
    }
}
