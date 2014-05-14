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

@RunWith(JUnit4.class)
public class RestfulHttpTest {
	private static HttpClientHelper client;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@BeforeClass
	public static void init() {
		client = new HttpClientHelper();
	}

	@Test
	public void get() {
		try {
			CloseableHttpResponse response = client.get("httpbin.org" , "/get");
			EntityUtils.consume(response.getEntity());
			response.close();
		} catch (IOException | HttpException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void getWithArguments() {
		try {
			Map<String, String> args = new TreeMap<>();
			args.put("hello", "world");
			RestResponse response = new RestResponse(client.get("httpbin.org", "/get", args));

			JsonNode returnArgs = response.getRootNode().get("args");

			// Add the arguments of the return JSON file to a map
			Map<String, String> parsedArgs = new TreeMap<>();
			for (Iterator<Map.Entry<String, JsonNode>> it = returnArgs.getFields(); it.hasNext();) {
				Map.Entry<String, JsonNode> entry = it.next();
				parsedArgs.put(entry.getKey(), entry.getValue().getTextValue());
			}

			if (!mapSame(args, parsedArgs)) {
				Assert.fail("Sent arguments and returned arguments were not the same");
			}
		} catch (HttpException | IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void post() {
		httpFormDataRequest("POST");
	}

	@Test
	public void httpGetInvalidResponseCode() throws IOException, HttpException {
		exception.expect(HttpException.class);
		EntityUtils.consume(client.get("httpbin.org", "/status/418").getEntity());
	}

	/**
	 * Conducts a test for HTTP methods that uses form data (POST, PATCH, PUT)
	 * @param method The HTTP verb to use (POST, PATCH, or PUT)
	 */
	private void httpFormDataRequest(String method) {
		try {
			Map<String, String> clientArgs = new TreeMap<>();
			clientArgs.put("hello", "world");


			CloseableHttpResponse response;
			String host = "httpbin.org";

			switch (method.toUpperCase()) {
				case "POST":
					response = client.post(host, "/post", clientArgs);
					break;
				case "PATCH":
					response = client.patch(host, "/patch", clientArgs);
					break;
				case "PUT":
					response = client.put(host, "/put", clientArgs);
					break;
				default:
					throw new IllegalArgumentException("HTTP method not supported: " + method);
			}


			RestResponse rest = new RestResponse(response);
			JsonNode returnArgs = rest.getRootNode().get("form");

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

	private <K, V> boolean mapSame(Map<K, V> map1, Map<K, V> map2) {
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
