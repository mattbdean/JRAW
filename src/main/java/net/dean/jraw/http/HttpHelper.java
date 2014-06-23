package net.dean.jraw.http;

import net.dean.jraw.JrawUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is responsible for the lowest level of networking found in this library. It can execute RESTful HTTP requests
 * through the use of the {@code execute} methods
 */
public class HttpHelper {

	/** The HttpClient used to execute HTTP requests */
	private HttpClient client;

	private CookieStore cookieStore;

	/** The list of headers that will be sent with every HTTP request */
	private List<Header> defaultHeaders;

	/**
	 * Instantiates a new HttpClientHelper and adds a given string as the value for the User-Agent header for every request
	 *
	 * @param userAgent The User-Agent to use for the HTTP requests
	 */
	public HttpHelper(String userAgent) {
		this.cookieStore = new BasicCookieStore();
		this.defaultHeaders = new ArrayList<>();
		defaultHeaders.add(new BasicHeader("User-Agent", userAgent));
		this.client = HttpClientBuilder.create()
				.setDefaultCookieStore(cookieStore)
				.build();
	}

	/**
	 * Conducts an HTTP request with no arguments
	 *
	 * @param verb     The HTTP verb to use (GET, POST, etc)
	 * @param hostname The name of the URL's host. Do not include the scheme (ex: "http://")
	 * @param path     The path relative to the root directory of the host.
	 * @return A CloseableHttpResponse formed in the execution of the HTTP request
	 * @throws net.dean.jraw.http.NetworkException If there was a problem sending the HTTP request
	 */
	public CloseableHttpResponse execute(HttpVerb verb, String hostname, String path) throws NetworkException {
		return execute(verb, hostname, path, null);
	}

	/**
	 * Conducts an HTTP request
	 *
	 * @param verb     The HTTP verb to use (GET, POST, etc)
	 * @param hostname The name of the URL's host. Do not include the scheme (ex: the "http://")
	 * @param path     The path relative to the root directory of the host.
	 * @param args     The arguments to use. If the verb uses form data (POST, PATCH, PUT), then these will be put into the
	 *                 request body. If the verb is "GET", then a query string will be appended to the path.
	 * @return A CloseableHttpResponse formed in the execution of the HTTP request
	 * @throws NetworkException If the status code was not "200 OK"
	 */
	public CloseableHttpResponse execute(HttpVerb verb, String hostname, String path, Map<String, String> args) throws NetworkException {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		HttpRequestBase request = verb.getRequestObject(path);

		try {
			if (args != null) {
				// Construct a List of BasicNameValue pairs from a Map
				List<BasicNameValuePair> params = args.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(),
						entry.getValue())).collect(Collectors.toList());

				if (request instanceof HttpGet || request instanceof HttpDelete) {
					// GET or DELETE
					path += "?" + URLEncodedUtils.format(params, "UTF-8");

					if (request instanceof HttpGet) request = new HttpGet(path);
					if (request instanceof HttpDelete) request = new HttpDelete(path);
				} else {
					// POST, PATCH, or PUT
					((HttpEntityEnclosingRequestBase) request).setEntity(new UrlEncodedFormEntity(params));
				}
			}

			// Add the default headers to the request
			for (Header h : defaultHeaders) {
				request.addHeader(h);
			}

			JrawUtils.logger().info("{} {}{} {}", new Object[] {verb.name(), hostname, path, request.getProtocolVersion()});
			CloseableHttpResponse response = (CloseableHttpResponse) client.execute(new HttpHost(hostname), request);
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new NetworkException(response.getStatusLine().getStatusCode());
			}

			return response;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the list of headers that will be included with every request
	 *
	 * @return A list of default headers
	 */
	public List<Header> getDefaultHeaders() {
		return defaultHeaders;
	}

	/**
	 * Gets the cookies stored while sending HTTP requests
	 * @return The CookieStore for this HttpHelper
	 */
	public CookieStore getCookieStore() {
		return cookieStore;
	}

	/**
	 * Sets the value of the User-Agent header to send with every request
	 * @param userAgent The User-Agent to use for the HTTP requests
	 */
	public void setUserAgent(String userAgent) {
		for (Iterator<Header> it = defaultHeaders.iterator(); it.hasNext(); ) {
			Header currentHeader = it.next();
			if (currentHeader.getName().equals("User-Agent")) {
				it.remove();
				defaultHeaders.add(new BasicHeader("User-Agent", userAgent));
				return;
			}
		}

		// No User-Agent string was found
		defaultHeaders.add(new BasicHeader("User-Agent", userAgent));
	}

	/**
	 * Gets the User-Agent header
	 * @return The User-Agent to use for HTTP requests
	 */
	public String getUserAgent() {
		for (Header h : defaultHeaders) {
			if (h.getName().equals("User-Agent")) {
				return h.getValue();
			}
		}

		return null;
	}
}
