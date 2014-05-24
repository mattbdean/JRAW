package net.dean.jraw;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

	protected List<RestRequest> history;

	/**
	 * Instantiates a new RestClient
	 *
	 * @param host      The host on which to operate
	 * @param userAgent The User-Agent header which will be sent with all requests
	 */
	public RestClient(String host, String userAgent) {
		this.http = new HttpHelper(userAgent);
		this.host = host;
		this.history = new ArrayList<>();
	}


	/**
	 * Executes a RESTful HTTP request
	 *
	 * @param request The request to execute
	 * @return A RestResponse from the resulting response
	 * @throws NetworkException If the status code was not "200 OK"
	 */
	public RestResponse execute(RestRequest request) throws NetworkException {
		request.setExecuted(LocalDateTime.now());
		history.add(request);
		return new RestResponse(http.execute(request.getVerb(), host, request.getPath(), request.getArgs()));
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
