package net.dean.jraw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private JrawUtils() {
		// no instances
	}

	private static Logger logger = LoggerFactory.getLogger("JRAW");

	public static Logger logger() {
		return logger;
	}

	/**
	 * Creates a new URL and prints the stack trace if a MalformedURLException is caught
	 *
	 * @param href The data of the URL
	 * @return A new URL
	 */
	public static URL newUrl(String href) {
		try {
			return new URL(href);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Creates a new URI and prints the stack trace if a URISyntaxException is caught
	 *
	 * @param location The location of the URI
	 * @return A new URI
	 */
	public static URI newUri(String location) {
		try {
			return new URI(location);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Convenience method to combine a list of strings into a map. Sample usage:<br>
	 * <br>
	 * <code>
	 * Map&lt;String, String&gt; mapOfArguments = args("key1", "value1", "key2", "value2");
	 * </code><br><br>
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
	 * @return A map of the given keys and values array
	 * @throws IllegalArgumentException If the length of the string array is not even
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
}
