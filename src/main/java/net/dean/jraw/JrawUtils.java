package net.dean.jraw;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.net.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A collection of utility methods
 */
public final class JrawUtils {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String CHARSET = "UTF-8";

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
     * Convenience method to combine a list of objects into a map Objects will be turned into strings by calling their
     * {@code toString()} method. Sample usage:<br>
     * <pre>{@code
     * Map<String, String> mapOfArguments = mapOf("key1", "value1", "key2", "value2");
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
     *                      and all values must be non-null.
     * @return A map of the given keys and values array
     * @throws IllegalArgumentException If the amount of parameters is not even (there is not a value for every key)
     * @throws NullPointerException If an element in the array was null
     */
    public static Map<String, String> mapOf(Object... keysAndValues) {
        if (keysAndValues.length % 2 != 0) {
            throw new IllegalArgumentException("Keys and values length must be even");
        }

        Map<String, String> args = new HashMap<>();
        for (int i = 0; i < keysAndValues.length; ) {
            Object key = keysAndValues[i++];
            if (key == null)
                throw new NullPointerException("Object at index " + i + " was null");
            Object val = keysAndValues[i++];
            if (val == null)
                throw new NullPointerException("Object at index " + i + " was null");
            args.put(String.valueOf(key), String.valueOf(val));
        }

        return args;
    }

    /**
     * Tests if the given string could possibly be the fullname of an Thing. In order to pass, the first character must
     * be "t", the second character must be a digit in the range of 1-6 or 8, the third character must be an underscore,
     * and the rest of the letters must be alphanumeric. See <a href="http://www.reddit.com/dev/api#fullnames">here</a>
     * for more information.
     *
     * @param name The String to test
     * @return If the name given could be a Thing's fullname
     */
    public static boolean isFullname(String name) {
        if (name.length() < 3) {
            throw new IllegalArgumentException("Name must be at least three characters");
        }
        return name.matches("t[1-6|8]_[a-zA-Z0-9]+");
    }

    /** Compares the type and subtype of two MediaTypes. Will recognize the asterisk ('*') as a wildcard. */
    public static boolean isEqual(com.squareup.okhttp.MediaType t1, com.google.common.net.MediaType t2) {
        return isEqual(t1.type(), t1.subtype(), t2.type(), t2.subtype());
    }

    /** Compares the type and subtype of two MediaTypes. Will recognize the asterisk ('*') as a wildcard. */
    public static boolean isEqual(com.google.common.net.MediaType t1, com.google.common.net.MediaType t2) {
        return isEqual(t1.type(), t1.subtype(), t2.type(), t2.subtype());
    }

    private static boolean isEqual(String t1Main, String t1Sub, String t2Main, String t2Sub) {
        boolean mainType = t1Main.equals(t2Main) || (t1Main.equals("*") || t2Main.equals("*"));
        boolean subType = t1Sub.equals(t2Sub) || (t1Sub.equals("*") || t2Sub.equals("*"));
        return mainType && subType;
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
     * Serializes an object into a JSON string
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

    /**
     * Deserializes a JSON string into a Jackson JsonNode.
     */
    public static JsonNode fromString(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse JSON: " + json.replace("\r", " ").replace("\n", " "), e);
        }
    }

    /**
     * Joins the given strings together with a comma
     */
    public static String join(Iterable<String> args) {
        return Joiner.on(',').join(args);
    }

    /**
     * Joins the given Strings together with a comma
     */
    public static String join(String... args) {
        return join(',', args);
    }

    /**
     * Joins the given Strings together with a given character
     */
    public static String join(char separator, String... args) {
        switch (args.length) {
            case 0:
                return "";
            case 1:
                return args[0];
            default:
                return Joiner.on(separator).join(args);
        }
    }

    /**
     * Creates a new MediaType, removing any trailing semicolons if necessary.
     * @param header The value of the Content-Type header
     * @return A new MediaType
     */
    public static MediaType parseMediaType(String header) {
        if (header == null || header.trim().length() == 0) {
            throw new IllegalArgumentException("header cannot be null");
        }
        if ((header = header.trim()).endsWith(";")) {
            // MediaType.parse() doesn't like a trailing semicolon, remove it
            header = header.substring(0, header.length() - 1);
        }
        return MediaType.parse(header);
    }

    /**
     * Parses a URL-encoded string with keys and values into a map. The key-value pair separator is assumed to be '&amp;'
     * and the key-value separator is assumed to be '='
     */
    public static Map<String, String> parseUrlEncoded(String data) {
        Map<String, String> map = new HashMap<>();

        String[] keysAndValues = data.split("&");
        for (String pair : keysAndValues) {
            String[] parts = pair.split("=");
            // Avoid an ArrayIndexOutOfBoundsException if there was no value
            String value = parts.length == 1 ? "" : parts[1];
            map.put(parts[0], value);
        }

        return map;
    }

    /**
     * Returns a URL-encoded version of the given String in UTF-8
     */
    public static String urlEncode(String data) {
        try {
            return URLEncoder.encode(data, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Charset '" + CHARSET + "' not found", e);
        }
    }

    /**
     * Returns a decoded UTF-8 string
     */
    public static String urlDecode(String data) {
        try {
            return URLDecoder.decode(data, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Charset '" + CHARSET + "' not found", e);
        }
    }

    public static ObjectMapper objectMapper() {
        return mapper;
    }
}
