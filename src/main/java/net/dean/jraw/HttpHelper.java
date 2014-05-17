package net.dean.jraw;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is responsible for the lowest level of networking found in this library. It conducts HTTP requests with
 * several verbs such as GET, POST, PUT, and PATCH, which all have their respective methods.
 *
 * @author Matthew Dean
 */
public class HttpHelper {
	/** The HttpClient used to execute HTTP requests */
	private HttpClient client;

	/** The list of headers that will be sent with every HTTP request */
	private List<Header> defaultHeaders;

	/**
	 * Instantiates a new HttpClientHelper
	 */
	public HttpHelper() {
		this(null);
	}

	/**
	 * Instantiates a new HttpClientHelper and adds a given string as the value for the User-Agent header for every request
	 * @param userAgent The User-Agent to use for the HTTP requests
	 */
	public HttpHelper(String userAgent) {
		this.client = HttpClients.createDefault();
		this.defaultHeaders = new ArrayList<>();

		if (userAgent != null) {
			defaultHeaders.add(new BasicHeader(HTTP.USER_AGENT, userAgent));
		}
	}

	/**
	 * Conducts an HTTP request with no arguments
	 *
	 * @param verb The HTTP verb to use (GET, POST, etc)
	 * @param hostname The name of the URL's host. Do not include the scheme (ex: "http://")
	 * @param path The path relative to the root directory of the host.
	 * @return A CloseableHttpResponse formed in the execution of the HTTP request
	 */
	public CloseableHttpResponse execute(HttpVerb verb, String hostname, String path) throws IOException, HttpException {
		return execute(verb, hostname, path, null);
	}

	/**
	 * Conducts an HTTP request
	 *
	 * @param verb The HTTP verb to use (GET, POST, etc)
	 * @param hostname The name of the URL's host. Do not include the scheme (ex: the "http://")
	 * @param path The path relative to the root directory of the host.
	 * @param args The arguments to use. If the verb uses form data (POST, PATCH, PUT), then these will be put into the
	 *             request body. If the verb is "GET", then a query string will be appended to the path.
	 * @return A CloseableHttpResponse formed in the execution of the HTTP request
	 * @throws IOException In case of a problem or the connection was aborted
	 * @throws HttpException If the status code was not "200 OK"
	 */
	public CloseableHttpResponse execute(HttpVerb verb, String hostname, String path, Map<String, String> args) throws IOException, HttpException {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		HttpRequestBase request = verb.getRequestObject(path);


		if (args != null) {
			// Construct a List of BasicNameValue pairs from a Map
			List<BasicNameValuePair> params = args.entrySet().stream().map(entry -> new BasicNameValuePair(entry.getKey(),
					entry.getValue())).collect(Collectors.toList());

			if (request instanceof HttpGet || request instanceof HttpDelete) {
				// GET or DELETE
				path += "?" + URLEncodedUtils.format(params, "UTF-8");

				if (request instanceof HttpGet)  request = new HttpGet(path);
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


		CloseableHttpResponse response = (CloseableHttpResponse) client.execute(new HttpHost(hostname), request);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new HttpException("Status code not 200 (was " + response.getStatusLine().getStatusCode() + ")");
		}

		return response;
	}

	/**
	 * Gets the list of headers that will be included with every request
	 *
	 * @return A list of default headers
	 */
	public List<Header> getDefaultHeaders() {
		return defaultHeaders;
	}
}
