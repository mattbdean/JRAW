package net.dean.jraw;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for the lowest level of networking found in this library. It conducts HTTP requests with
 * several verbs such as GET, POST, PUT, and PATCH, which all have their respective methods.
 *
 * @author Matthew Dean
 */
public class HttpClientHelper {
	/** The HttpClient used to execute HTTP requests */
	private HttpClient client;

	/**
	 * Instantiates a new HttpClientHelper
	 */
	public HttpClientHelper() {
		this.client = HttpClients.createDefault();
	}

	/**
	 * Performs a GET request with no parameters
	 *
	 * @param hostname The name of the URL's host. Do not include the scheme (aka the "http://")
	 * @param path The path relative to the root directory of the host.
	 * @return A CloseableHttpRequest which is the result of an HTTP request with the given parameters
	 * @throws IOException In case the connection encountered a problem or was aborted
	 * @throws HttpException If the request's status code was anything other than "200 OK"
	 */
	public CloseableHttpResponse get(String hostname, String path) throws IOException, HttpException {
		return doRequest("GET", hostname, path, null);
	}

	/**
	 * Performs a GET request with parameters
	 *
	 * @param hostname The name of the URL's host. Do not include the scheme (aka the "http://")
	 * @param path The path relative to the root directory of the host.
	 * @param args The arguments to use. If the verb uses form data (POST, PATCH, PUT), then these will be put into the
	 *             request body. If the verb is "GET", then a query string will be appended to the path.
	 * @return A CloseableHttpRequest which is the result of an HTTP request with the given parameters
	 * @throws IOException In case the connection encountered a problem or was aborted
	 * @throws HttpException If the request's status code was anything other than "200 OK"
	 */
	public CloseableHttpResponse get(String hostname, String path, Map<String, String> args) throws IOException, HttpException {
		return doRequest("GET", hostname, path, args);
	}

	/**
	 * Performs a POST request with parameters
	 *
	 * @param hostname The name of the URL's host. Do not include the scheme (aka the "http://")
	 * @param path The path relative to the root directory of the host.
	 * @return A CloseableHttpRequest which is the result of an HTTP request with the given parameters
	 * @throws IOException In case the connection encountered a problem or was aborted
	 * @throws HttpException If the request's status code was anything other than "200 OK"
	 */
	public CloseableHttpResponse post(String hostname, String path) throws IOException, HttpException {
		return doRequest("POST", hostname, path, null);
	}

	/**
	 * Performs a POST request with no parameters
	 *
	 * @param hostname The name of the URL's host. Do not include the scheme (aka the "http://")
	 * @param path The path relative to the root directory of the host.
	 * @param args The arguments to use. If the verb uses form data (POST, PATCH, PUT), then these will be put into the
	 *             request body. If the verb is "GET", then a query string will be appended to the path.
	 * @return A CloseableHttpRequest which is the result of an HTTP request with the given parameters
	 * @throws IOException In case the connection encountered a problem or was aborted
	 * @throws HttpException If the request's status code was anything other than "200 OK"
	 */
	public CloseableHttpResponse post(String hostname, String path, Map<String, String> args) throws IOException, HttpException {
		return doRequest("POST", hostname, path, args);
	}

	/**
	 * Performs a PATCH request with no parameters
	 *
	 * @param hostname The name of the URL's host. Do not include the scheme (aka the "http://")
	 * @param path The path relative to the root directory of the host.
	 * @return A CloseableHttpRequest which is the result of an HTTP request with the given parameters
	 * @throws IOException In case the connection encountered a problem or was aborted
	 * @throws HttpException If the request's status code was anything other than "200 OK"
	 */
	public CloseableHttpResponse patch(String hostname, String path) throws IOException, HttpException {
		return doRequest("PATCH", hostname, path, null);
	}

	/**
	 * Performs a PATCH request with parameters
	 *
	 * @param hostname The name of the URL's host. Do not include the scheme (aka the "http://")
	 * @param path The path relative to the root directory of the host.
	 * @param args The arguments to use. If the verb uses form data (POST, PATCH, PUT), then these will be put into the
	 *             request body. If the verb is "GET", then a query string will be appended to the path.
	 * @return A CloseableHttpRequest which is the result of an HTTP request with the given parameters
	 * @throws IOException In case the connection encountered a problem or was aborted
	 * @throws HttpException If the request's status code was anything other than "200 OK"
	 */
	public CloseableHttpResponse patch(String hostname, String path, Map<String, String> args) throws IOException, HttpException {
		return doRequest("PATCH", hostname, path, args);
	}

	/**
	 * Performs a PUT request with no parameters
	 *
	 * @param hostname The name of the URL's host. Do not include the scheme (aka the "http://")
	 * @param path The path relative to the root directory of the host.
	 * @return A CloseableHttpRequest which is the result of an HTTP request with the given parameters
	 * @throws IOException In case the connection encountered a problem or was aborted
	 * @throws HttpException If the request's status code was anything other than "200 OK"
	 */
	public CloseableHttpResponse put(String hostname, String path) throws IOException, HttpException {
		return doRequest("PUT", hostname, path, null);
	}

	/**
	 * Performs a PUT request with parameters
	 *
	 * @param hostname The name of the URL's host. Do not include the scheme (aka the "http://")
	 * @param path The path relative to the root directory of the host.
	 * @param args The arguments to use. If the verb uses form data (POST, PATCH, PUT), then these will be put into the
	 *             request body. If the verb is "GET", then a query string will be appended to the path.
	 * @return A CloseableHttpRequest which is the result of an HTTP request with the given parameters
	 * @throws IOException In case the connection encountered a problem or was aborted
	 * @throws HttpException If the request's status code was anything other than "200 OK"
	 */
	public CloseableHttpResponse put(String hostname, String path, Map<String, String> args) throws IOException, HttpException {
		return doRequest("PUT", hostname, path, args);
	}

	/**
	 * Conducts an HTTP request
	 *
	 * @param httpVerb The HTTP verb to use (GET, POST, PATCH, or PUT)
	 * @param hostname The name of the URL's host. Do not include the scheme (aka the "http://")
	 * @param path The path relative to the root directory of the host.
	 * @param args The arguments to use. If the verb uses form data (POST, PATCH, PUT), then these will be put into the
	 *             request body. If the verb is "GET", then a query string will be appended to the path.
	 * @return A CloseableHttpResponse formed in the execution of the HTTP request
	 * @throws IOException In case the connection encountered a problem or was aborted
	 * @throws HttpException If the request's status code was anything other than "200 OK"
	 */
	private CloseableHttpResponse doRequest(String httpVerb, String hostname, String path, Map<String, String> args) throws IOException, HttpException {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		HttpRequestBase request;
		switch (httpVerb.toUpperCase()) {
			case "GET":
				request = new HttpGet(path);
				break;
			case "POST":
				request = new HttpPost(path);
				break;
			case "PATCH":
				request = new HttpPatch(path);
				break;
			case "PUT":
				request = new HttpPut(path);
				break;
			default:
				throw new IllegalArgumentException("Invalid HTTP verb: " + httpVerb);
		}

		if (args != null) {
			List<BasicNameValuePair> params = new ArrayList<>();

			for (Map.Entry<String, String> entry : args.entrySet()) {
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}

			if (request instanceof HttpGet) {
				path += "?" + URLEncodedUtils.format(params, "UTF-8");
				request = new HttpGet(path);
			} else {
				// POST, PATCH, or PUT
				((HttpEntityEnclosingRequestBase) request).setEntity(new UrlEncodedFormEntity(params));
			}
		}

		CloseableHttpResponse response = (CloseableHttpResponse) client.execute(new HttpHost(hostname), request);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new HttpException("Status code not 200 (was " + response.getStatusLine().getStatusCode() + ")");
		}

		return response;
	}
}
