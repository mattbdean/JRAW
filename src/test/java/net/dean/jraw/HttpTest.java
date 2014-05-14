package net.dean.jraw;

import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static net.dean.jraw.HttpVerb.*;

@RunWith(JUnit4.class)
public class HttpTest {
	private static HttpHelper client;
	private static final String HOST = "httpbin.org";

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@BeforeClass
	public static void init() {
		client = new HttpHelper();
	}

	@Test
	public void get() {
		request(GET);
	}

	@Test
	public void post() {
		request(POST);
	}

	@Test
	public void put() {
		request(PUT);
	}

	@Test
	public void patch() {
		request(PATCH);
	}

	@Test
	public void delete() {
		request(DELETE);
	}

	@Test
	public void httpGetInvalidResponseCode() throws IOException, HttpException {
		exception.expect(HttpException.class);
		EntityUtils.consume(client.execute(GET, HOST, "/status/418").getEntity());
	}

	/**
	 * Conducts a test for HTTP methods that uses form data (POST, PATCH, PUT)
	 * @param verb The HTTP verb to use (POST, PATCH, or PUT)
	 */
	private void request(HttpVerb verb) {
		try {
			Map<String, String> clientArgs = new TreeMap<>();
			clientArgs.put("hello", "world");

			CloseableHttpResponse response = client.execute(verb, HOST, "/" + verb.name().toLowerCase(), clientArgs);

			RestResponse rest = new RestResponse(response);

			// GET and DELETE use query string ("args"), the rest use form data ("form")
			String key = (verb == GET || verb == DELETE) ? "args" : "form";
			JsonNode returnArgs = rest.getRootNode().get(key);

			// Add the arguments of the return JSON file to a map
			Map<String, String> parsedArgs = new TreeMap<>();
			for (Iterator<Map.Entry<String, JsonNode>> it = returnArgs.getFields(); it.hasNext();) {
				Map.Entry<String, JsonNode> entry = it.next();
				parsedArgs.put(entry.getKey(), entry.getValue().getTextValue());
			}

			// Check that the two argument maps are the same size
			Assert.assertEquals("Argument map sizes were not the same", clientArgs.size(), parsedArgs.size());

			// Compare the two maps
			if (!mapSame(clientArgs, parsedArgs)) {
				Assert.fail("Sent arguments and returned arguments did not match");
			}

			response.close();
		} catch (IOException | HttpException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Tests if two maps have the same keys and values
	 *
	 * @param map1 The first map
	 * @param map2 The second map
	 * @param <K> The key type of the two maps
	 * @param <V> The value type of the two maps
	 * @return True, if the two maps have the same keys and values, false if else
	 */
	private <K, V> boolean mapSame(Map<K, V> map1, Map<K, V> map2) {
		if (map1.size() != map2.size()) {
			// Not same size, impossible to be equal
			return false;
		}

		map1 = new TreeMap<>(map1);
		map2 = new TreeMap<>(map2);

		for (Map.Entry<K, V> entry : map1.entrySet()) {
			K key = entry.getKey();
			if (!entry.getValue().equals(map2.get(key))) {
				return false;
			}
		}

		return true;
	}
}
