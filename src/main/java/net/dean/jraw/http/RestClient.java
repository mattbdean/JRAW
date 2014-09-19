package net.dean.jraw.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a way to send RESTful HTTP requests and return {@link net.dean.jraw.http.RestResponse} objects
 * @param <U> The type of response object that will be returned by {@link #execute(RestRequest)}
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

    /**
     * Instantiates a new {@link RestRequest.Builder} with the host given to create this RestClient
     * @param verb The HTTP verb to use
     * @param path The path of the request. For example, "/api/login"
     * @return A new RestRequest Builder
     */
    public RestRequest.Builder requestBuilder(HttpVerb verb, String path) {
        return new RestRequest.Builder(verb, host, path);
    }

    /**
     * Instantiates a new simple RestRequest with the host given to create this RestClient
     * @param verb The HTTP verb to use
     * @param path The path of the request. For example, "/api/login"
     * @return A new RestRequest
     */
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

    /**
     * This method is responsible for instantiating a new RestResponse for {@link #execute(RestRequest)}.
     * This method must be overwritten if the extending class is using a subclass of RestResponse for type {@code <U>}.
     *
     * @param request The given RestRequest
     * @param response An Apache HttpComponents response for the given request
     * @return A new response object
     */
    @SuppressWarnings("unchecked")
    protected U initResponse(T request, HttpResponse response) {
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
