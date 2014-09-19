package net.dean.jraw.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a way to send RESTful HTTP requests and return {@link net.dean.jraw.http.RestResponse} objects
 * @param <U> The type of response object that will be returned by {@link #execute(HttpRequest)}
 */
public class RestClient<T extends RestRequest, U extends RestResponse> {
    private final String host;

    /** The HttpHelper that will do all the basic HTTP requests */
    protected HttpHelper http;

    /** A list of RestRequests sent in the past */
    protected List<HttpRequest> history;

    /**
     * Instantiates a new RestClient
     *
     * @param host          The host on which to operate
     * @param userAgent     The User-Agent header which will be sent with all requests
     */
    public RestClient(String host, String userAgent) {
        this.http = new HttpHelper(userAgent);
        this.host = host;
        this.history = new ArrayList<>();
    }

    public RestRequest.Builder requestBuilder(HttpVerb verb, String path) {
        return new RestRequest.Builder(verb, host, path);
    }

    public RestRequest request(HttpVerb verb, String path) {
        return new RestRequest(verb, host, path);
    }

    /**
     * Executes a RESTful HTTP request
     *
     * @param request The request to execute
     * @return A RestResponse from the resulting response
     * @throws NetworkException If the status code was not "200 OK"
     */
    public U execute(T request) throws NetworkException {
        request.onExecuted();
        history.add(request);
        CloseableHttpResponse response = http.execute(request);

        if (response == null) {
            throw new NetworkException("Request timed out");
        }

        return initResponse(request, response);
    }

    public U initResponse(RestRequest request, HttpResponse response) {
        return (U) new RestResponse(response, request.getExpected());
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
