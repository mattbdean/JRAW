package net.dean.jraw.http;

import org.apache.http.client.methods.CloseableHttpResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a way to send RESTful HTTP requests and return {@link net.dean.jraw.http.RestResponse} objects
 */
public class RestClient {
    private final String host;

    /** The HttpHelper that will do all the basic HTTP requests */
    protected HttpHelper http;

    /** A list of RestRequests sent in the past */
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
        HttpHelper.RequestBuilder builder = new HttpHelper.RequestBuilder(request.getVerb(), host, request.getPath());
        if (request.isJson()) {
            builder.json(request.getJson());
        } else {
            builder.args(request.getArgs());
        }

        CloseableHttpResponse response = http.execute(builder);

        if (response == null) {
            throw new NetworkException("Request timed out");
        }
        return new RestResponse(response);
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
