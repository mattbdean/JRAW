package net.dean.jraw;

import com.squareup.okhttp.MediaType;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Contribution;
import net.dean.jraw.models.Message;
import net.dean.jraw.models.PublicContribution;
import net.dean.jraw.models.RedditObject;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.ThingType;
import net.dean.jraw.paginators.MultiHubPaginator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A collection of utility methods
 */
public final class JrawUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JrawUtils() {
        // no instances
    }

    private static Logger logger = LoggerFactory.getLogger("JRAW");

    /**
     * Gets the SLF4J logger used to log messages
     * @return The logger
     */
    public static Logger logger() {
        return logger;
    }

    /**
     * Creates a new URL and wraps the {@link java.net.MalformedURLException} around an IllegalArgumentException if one
     * was thrown.
     *
     * @param href The data of the URL
     * @return A new URL
     * @throws IllegalArgumentException If a MalformedURLException was thrown
     */
    public static URL newUrl(String href) {
        try {
            return new URL(href);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + href, e);
        }
    }

    /**
     * Creates a new URI and wraps the {@link java.net.URISyntaxException} around an IllegalArgumentException if one was
     * thrown.
     *
     * @param location The location of the URI
     * @return A new URI
     * @throws IllegalArgumentException If a URISyntaxException was thrown
     */
    public static URI newUri(String location) {
        try {
            return new URI(location);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Malformed URI: " + location, e);
        }
    }

    /**
     * Convenience method to combine a list of strings into a map. Sample usage:<br>
     * <pre>{@code
     * Map<String, String> mapOfArguments = args("key1", "value1", "key2", "value2");
     * }</pre>
     * would result in this:
     * <pre>
     * {@code
     * {
     *     "key1" => "value1",
     *     "key2" => "value2"
     * }
     * }
     * </pre>
     *
     * @param keysAndValues A list of objects to be turned into strings and condensed into a map. Must be of even length
     *                      and all values must be non-null
     * @return A map of the given keys and values array
     * @throws IllegalArgumentException If the amount of parameters is not even
     * @throws NullPointerException If an element in the array was null
     */
    public static Map<String, String> args(Object... keysAndValues) {
        if (keysAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Keys and values length must be even");
        }

        for (int i = 0; i < keysAndValues.length; i++) {
            Object o = keysAndValues[i];
            if (o == null)
                throw new NullPointerException("Object at index " + i + " was null");
        }

        Map<String, String> args = new HashMap<>();
        for (int i = 0; i < keysAndValues.length; ) {
            args.put(String.valueOf(keysAndValues[i++]), String.valueOf(keysAndValues[i++]));
        }

        return args;
    }

    /**
     * Tests if the given string could possibly be the full name of an Thing. In order to pass, the first character must
     * be "t", the second character must be a digit in the range of 1-8, the third character must be an underscore, and
     * the rest of the letters must be alphanumeric. See <a href="http://www.reddit.com/dev/api#fullnames">here</a> for
     * more information.
     *
     * @param name The String to test
     * @return If the name given could be a Thing's full name
     */
    public static boolean isFullName(String name) {
        if (name.length() < 3) {
            throw new IllegalArgumentException("Name must be at least three characters");
        }
        return name.matches("t[1-6|8]_[a-zA-Z].*");
    }

    /**
     * Parses a JsonNode into a RedditObject
     *
     * @param rootNode   The root node of the Thing. Should only contain two elements: "kind", and "data".
     * @param thingClass The type of Thing this JsonNode should be turned into
     * @param <T>        The return type
     * @return A new RedditObject
     */
    @SuppressWarnings("unchecked")
    public static <T extends RedditObject> T parseJson(JsonNode rootNode, Class<T> thingClass) {
        if (thingClass.equals(Contribution.class)) {
            switch (ThingType.getByPrefix(rootNode.get("kind").asText())) {
                case LINK:
                    return (T) new Submission(rootNode.get("data"));
                case COMMENT:
                    return (T) new Comment(rootNode.get("data"));
                case MESSAGE:
                    return (T) new Message(rootNode.get("data"));
                default:
                    throw new IllegalArgumentException("Class " + thingClass.getName() +
                            " is not applicable for Contribution");
            }
        } else if (thingClass.equals(MultiHubPaginator.MultiRedditId.class)) {
            return (T) new MultiHubPaginator.MultiRedditId(rootNode.get("owner").asText(),
                    rootNode.get("name").asText());
        } else if (thingClass.equals(PublicContribution.class)) {
            switch (ThingType.getByPrefix(rootNode.get("kind").asText())) {
                case LINK:
                    return (T) new Submission(rootNode.get("data"));
                case COMMENT:
                    return (T) new Comment(rootNode.get("data"));
                default:
                    throw new IllegalArgumentException("Class " + thingClass.getName() +
                            " is not applicable for Contribution");
            }
        }
        try {
            // Instantiate a generic Thing
            Constructor<T> constructor = thingClass.getConstructor(JsonNode.class);
            return constructor.newInstance(rootNode.get("data"));
        } catch (NoSuchMethodException |
                InstantiationException |
                IllegalAccessException |
                InvocationTargetException e) {
            // Holy exceptions Batman!
            logger().error("Could not create the Thing ({})", thingClass.getName(), e);
        }

        return null;
    }

    /**
     * Compares the type and subtype of two MediaTypes.
     * @param t1 The first MediaType
     * @param t2 The second MediaType
     * @return True, if {@code t1.type().equals(t2.type())} and {@code t1.subtype().equals(t2.subtype())}, false if
     *         else.
     */
    public static boolean typeComparison(MediaType t1, MediaType t2) {
        return t1.type().equals(t2.type()) && t1.subtype().equals(t2.subtype());
    }

    /**
     * Prepends "/r/{subreddit}" to {@code path} if {@code subreddit} is not null
     * @param subreddit The subreddit to use
     * @param path The path to use
     * @return "/r/{subreddit}/{path}" if {@code subreddit} is not null, otherwise "{path}"
     */
    public static String getSubredditPath(String subreddit, String path) {
        if (subreddit != null) {
            path = "/r/" + subreddit + path;
        }

        return path;
    }

    /**
     * Serializes an object to a JSON string
     * @return The JSON interpretation of the object
     */
    public static String toJson(Object o) {
        StringWriter out = new StringWriter();
        try {
            mapper.writeValue(out, o);
        } catch (IOException e) {
            JrawUtils.logger().error("Unable to create the data model", e);
            return null;
        }

        return out.toString();
    }

}
