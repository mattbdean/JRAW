package net.dean.jraw;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a way to consolidate all the main attributes of a RESTful HTTP request into one object
 */
public class RestRequest {

	/**
	 * The path relative to the root of the host
	 */
	private String path;

	/**
	 * The arguments to be passed either by query string if the method is GET or DELETE, or by form if it is a different request
	 */
	private Map<String, String> args;

	/**
	 * The HTTP verb to use to execute the request
	 */
	private HttpVerb verb;

	/**
	 * The time this request was executed
	 */
	private LocalDateTime executed;

	/**
	 * Instantiates a new RestRequest
	 *
	 * @param verb The HTTP verb to use
	 * @param path The path of the request relative to the host "/{something}"
	 */
	public RestRequest(HttpVerb verb, String path) {
		this(verb, path, null);
	}

	/**
	 * Instantiates a new RestRequest
	 *
	 * @param verb The HTTP verb to use
	 * @param path The path of the request relative to the host "/{something}"
	 * @param args The arguments to pass in the query
	 */
	public RestRequest(HttpVerb verb, String path, Map<String, String> args) {
		this.verb = verb;
		this.path = path;
		this.args = args;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, String> getArgs() {
		return args;
	}

	public void setArgs(Map<String, String> args) {
		this.args = args;
	}

	public LocalDateTime getExecuted() {
		return executed;
	}

	public void setExecuted(LocalDateTime executed) {
		this.executed = executed;
	}

	public HttpVerb getVerb() {
		return verb;
	}

	public void setVerb(HttpVerb verb) {
		this.verb = verb;
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
	 * @throws java.lang.IllegalArgumentException If the length of the string array is not even
	 */
	public static Map<String, String> args(Object... keysAndValues) {
		if (keysAndValues.length % 2 != 0) {
			throw new IllegalArgumentException("Keys and values length must be even");
		}

		Map<String, String> args = new HashMap<>();
		for (int i = 0; i < keysAndValues.length; ) {
			args.put(String.valueOf(keysAndValues[i++]), String.valueOf(keysAndValues[i++]));
		}

		return args;
	}

	@Override
	public String toString() {
		return "RestRequest{" +
				"path='" + path + '\'' +
				", args=" + args +
				", verb=" + verb +
				", executed=" + executed +
				'}';
	}
}
