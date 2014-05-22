package net.dean.jraw;

import java.util.Map;

import static net.dean.jraw.HttpVerb.*;

/**
 * Responsible for the simplification of the HttpHelper class' methods and returning RedditResponses
 */
public class RestClient {
	/**
	 * The host to execute the requests on
	 */
	private final String host;

	/**
	 * The HttpHelper that will do all the basic HTTP requests
	 */
	protected HttpHelper http;

	/**
	 * Instantiates a new RestClient
	 *
	 * @param host      The host on which to operate
	 * @param userAgent The User-Agent header which will be sent with all requests
	 */
	public RestClient(String host, String userAgent) {
		this.http = new HttpHelper(userAgent);
		this.host = host;
	}

	/**
	 * Executes a GET request
	 *
	 * @param path The path of the request
	 * @return A RedditResponse from the resulting response
	 * @throws NetworkException If the status code was not "200 OK"
	 */
	public RestResponse get(String path) throws NetworkException {
		return get(path, null);
	}

	/**
	 * Executes a GET request
	 *
	 * @param path The path of the request
	 * @param args The arguments to be sent with the request. Will be in the query string.
	 * @return A RedditResponse from the resulting response
	 * @throws NetworkException If the status code was not "200 OK"
	 */
	public RestResponse get(String path, Map<String, String> args) throws NetworkException {
		return new RestResponse(http.execute(GET, host, path, args));
	}

	/**
	 * Executes a POST request
	 *
	 * @param path The path of the request
	 * @return A RedditResponse from the resulting response
	 * @throws NetworkException If the status code was not "200 OK"
	 */
	public RestResponse post(String path) throws NetworkException {
		return post(path, null);
	}

	/**
	 * Executes a POST request
	 *
	 * @param path The path of the request
	 * @param args The arguments to be sent with the request
	 * @return A RedditResponse from the resulting response
	 * @throws NetworkException If the status code was not "200 OK"
	 */
	public RestResponse post(String path, Map<String, String> args) throws NetworkException {
		return new RestResponse(http.execute(POST, host, path, args));
	}

	/**
	 * Executes a PUT request
	 *
	 * @param path The path of the request
	 * @return A RedditResponse from the resulting response
	 * @throws NetworkException If the status code was not "200 OK"
	 */
	public RestResponse put(String path) throws NetworkException {
		return put(path, null);
	}

	/**
	 * Executes a PUT request
	 *
	 * @param path The path of the request
	 * @param args The arguments to be sent with the request
	 * @return A RedditResponse from the resulting response
	 * @throws NetworkException If the status code was not "200 OK"
	 */
	public RestResponse put(String path, Map<String, String> args) throws NetworkException {
		return new RestResponse(http.execute(PUT, host, path, args));
	}

	/**
	 * Executes a PATCH request
	 *
	 * @param path The path of the request
	 * @return A RedditResponse from the resulting response
	 * @throws NetworkException If the status code was not "200 OK"
	 */
	public RestResponse patch(String path) throws NetworkException {
		return patch(path, null);
	}

	/**
	 * Executes a PATCH request
	 *
	 * @param path The path of the request
	 * @param args The arguments to be sent with the request
	 * @return A RedditResponse from the resulting response
	 * @throws NetworkException If the status code was not "200 OK"
	 */
	public RestResponse patch(String path, Map<String, String> args) throws NetworkException {
		return new RestResponse(http.execute(PATCH, host, path, args));
	}

	/**
	 * Executes a DELETE request
	 *
	 * @param path The path of the request
	 * @return A RedditResponse from the resulting response
	 * @throws NetworkException If the status code was not "200 OK"
	 */
	public RestResponse delete(String path) throws NetworkException {
		return new RestResponse(http.execute(DELETE, host, path, null));
	}

	/**
	 * Executes a DELETE request
	 *
	 * @param path The path of the request
	 * @param args The arguments to be sent with the request. Will be in the query string.
	 * @return A RedditResponse from the resulting response
	 * @throws NetworkException If the status code was not "200 OK"
	 */
	public RestResponse delete(String path, Map<String, String> args) throws NetworkException {
		return new RestResponse(http.execute(DELETE, host, path, args));
	}

	/**
	 * Gets the HttpHelper used to execute HTTP requests
	 *
	 * @return The HttpHelper
	 */
	public HttpHelper getHttpHelper() {
		return http;
	}
}
