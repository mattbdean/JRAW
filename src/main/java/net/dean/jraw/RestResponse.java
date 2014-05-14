package net.dean.jraw;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * This class is used to show the result of a request to a RESTful web service such as Reddit's JSON API.
 */
public class RestResponse {
	/** The ObjectMapper used to map parse the response JSON */
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
	 * Gets the raw data returned
	 * @return
	 */
	public String getRaw() {
		return raw;
	}
}
