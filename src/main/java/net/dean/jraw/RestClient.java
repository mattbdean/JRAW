package net.dean.jraw;

import org.apache.http.HttpException;

import java.io.IOException;
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
	private HttpHelper http;

	/**
	 * Instantiates a new RestClient
	 *
	 * @param host The host on which to operate
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
	 * @throws IOException In case of a problem or the connection was aborted
	 * @throws HttpException If the status code was not "200 OK"
	 */
	public RedditResponse get(String path) throws IOException, HttpException {
		return get(path, null);
	}

	/**
	 * Executes a GET request
	 *
	 * @param path The path of the request
	 * @param args The arguments to be sent with the request. Will be in the query string.
	 * @return A RedditResponse from the resulting response
	 * @throws IOException In case of a problem or the connection was aborted
	 * @throws HttpException If the status code was not "200 OK"
	 */
	public RedditResponse get(String path, Map<String, String> args) throws IOException, HttpException {
		return new RedditResponse(http.execute(GET, host, path, args));
	}

	/**
	 * Executes a POST request
	 *
	 * @param path The path of the request
	 * @return A RedditResponse from the resulting response
	 * @throws IOException In case of a problem or the connection was aborted
	 * @throws HttpException If the status code was not "200 OK"
	 */
	public RedditResponse post(String path) throws IOException, HttpException {
		return post(path, null);
	}

	/**
	 * Executes a POST request
	 *
	 * @param path The path of the request
	 * @param args The arguments to be sent with the request
	 * @return A RedditResponse from the resulting response
	 * @throws IOException In case of a problem or the connection was aborted
	 * @throws HttpException If the status code was not "200 OK"
	 */
	public RedditResponse post(String path, Map<String, String> args) throws IOException, HttpException {
		return new RedditResponse(http.execute(POST, host, path, args));
	}

	/**
	 * Executes a PUT request
	 *
	 * @param path The path of the request
	 * @return A RedditResponse from the resulting response
	 * @throws IOException In case of a problem or the connection was aborted
	 * @throws HttpException If the status code was not "200 OK"
	 */
	public RedditResponse put(String path) throws IOException, HttpException {
		return put(path, null);
	}

	/**
	 * Executes a PUT request
	 *
	 * @param path The path of the request
	 * @param args The arguments to be sent with the request
	 * @return A RedditResponse from the resulting response
	 * @throws IOException In case of a problem or the connection was aborted
	 * @throws HttpException If the status code was not "200 OK"
	 */
	public RedditResponse put(String path, Map<String, String> args) throws IOException, HttpException {
		return new RedditResponse(http.execute(PUT, host, path, args));
	}

	/**
	 * Executes a PATCH request
	 *
	 * @param path The path of the request
	 * @return A RedditResponse from the resulting response
	 * @throws IOException In case of a problem or the connection was aborted
	 * @throws HttpException If the status code was not "200 OK"
	 */
	public RedditResponse patch(String path) throws IOException, HttpException {
		return patch(path, null);
	}

	/**
	 * Executes a PATCH request
	 *
	 * @param path The path of the request
	 * @param args The arguments to be sent with the request
	 * @return A RedditResponse from the resulting response
	 * @throws IOException In case of a problem or the connection was aborted
	 * @throws HttpException If the status code was not "200 OK"
	 */
	public RedditResponse patch(String path, Map<String, String> args) throws IOException, HttpException {
		return new RedditResponse(http.execute(PATCH, host, path, args));
	}

	/**
	 * Executes a DELETE request
	 *
	 * @param path The path of the request
	 * @return A RedditResponse from the resulting response
	 * @throws IOException In case of a problem or the connection was aborted
	 * @throws HttpException If the status code was not "200 OK"
	 */
	public RedditResponse delete(String path) throws IOException, HttpException {
		return new RedditResponse(http.execute(DELETE, host, path, null));
	}

	/**
	 * Executes a DELETE request
	 *
	 * @param path The path of the request
	 * @param args The arguments to be sent with the request. Will be in the query string.
	 * @return A RedditResponse from the resulting response
	 * @throws IOException In case of a problem or the connection was aborted
	 * @throws HttpException If the status code was not "200 OK"
	 */
	public RedditResponse delete(String path, Map<String, String> args) throws IOException, HttpException {
		return new RedditResponse(http.execute(DELETE, host, path, args));
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
