package net.dean.jraw.test;

import net.dean.jraw.http.HttpHelper;
import net.dean.jraw.http.HttpVerb;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static net.dean.jraw.http.HttpVerb.*;

public class HttpTest {
	private static final String HOST = "httpbin.org";
	private static HttpHelper client;

	@BeforeTest
	public static void init() {
		client = new HttpHelper(TestUtils.getUserAgent(HttpTest.class));
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

	@Test(expectedExceptions = NetworkException.class)
	public void httpGetInvalidResponseCode() throws IOException, NetworkException {
		CloseableHttpResponse response = client.execute(new HttpHelper.RequestBuilder(GET, HOST, "/status/418"));
		if (response != null) {
			EntityUtils.consume(response.getEntity());
		}
	}

	/**
	 * Conducts a test for HTTP methods that uses form data (POST, PATCH, PUT)
	 *
	 * @param verb The HTTP verb to use (POST, PATCH, or PUT)
	 */
	private void request(HttpVerb verb) {
		try {
			Map<String, String> clientArgs = new TreeMap<>();
			clientArgs.put("hello", "world");

			CloseableHttpResponse response = client.execute(new HttpHelper.RequestBuilder(verb, HOST, "/" + verb.name().toLowerCase()).args(clientArgs));

			RestResponse rest = new RestResponse(response);

			// GET and DELETE use query string ("args"), the rest use form data ("form")
			String key = (verb == GET || verb == DELETE) ? "args" : "form";
			JsonNode returnArgs = rest.getJson().get(key);

			// Add the arguments of the return JSON file to a map
			Map<String, String> parsedArgs = new TreeMap<>();
			for (Iterator<Map.Entry<String, JsonNode>> it = returnArgs.getFields(); it.hasNext(); ) {
				Map.Entry<String, JsonNode> entry = it.next();
				parsedArgs.put(entry.getKey(), entry.getValue().getTextValue());
			}

			// Check that the two argument maps are the same size
			Assert.assertEquals(clientArgs.size(), parsedArgs.size(), "Argument map sizes were not the same");

			// Compare the two maps
			if (!mapSame(clientArgs, parsedArgs)) {
				Assert.fail("Sent arguments and returned arguments did not match");
			}

			response.close();
		} catch (IOException | NetworkException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Tests if two maps have the same keys and values
	 *
	 * @param map1 The first map
	 * @param map2 The second map
	 * @param <K>  The key type of the two maps
	 * @param <V>  The value type of the two maps
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
