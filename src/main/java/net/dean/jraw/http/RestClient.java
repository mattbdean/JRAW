package net.dean.jraw.http;

import net.dean.jraw.JrawUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a way to send RESTful HTTP requests and return {@link net.dean.jraw.http.RestResponse} objects
 * @param <T> The type of response object that will be returned by {@link #execute(RestRequest)}
 */
public class RestClient<T extends RestResponse> {
    private final String host;

    /** The HttpHelper that will do all the basic HTTP requests */
    protected HttpHelper http;

    /** A list of RestRequests sent in the past */
    protected List<RestRequest> history;

    protected Class<T> responseClass;

    /**
     * Instantiates a new RestClient
     *
     * @param host          The host on which to operate
     * @param userAgent     The User-Agent header which will be sent with all requests
     * @param responseClass The class that will be used to create response objects
     */
    public RestClient(String host, String userAgent, Class<T> responseClass) {
        this.http = new HttpHelper(userAgent);
        this.host = host;
        this.history = new ArrayList<>();
        this.responseClass = responseClass;
    }

    /**
     * Executes a RESTful HTTP request
     *
     * @param request The request to execute
     * @return A RestResponse from the resulting response
     * @throws NetworkException If the status code was not "200 OK"
     */
    public T execute(RestRequest request) throws NetworkException {
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

        try {
            return responseClass.getConstructor(HttpResponse.class).newInstance(response);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            // Holy exceptions, Batman!
            JrawUtils.logger().error("Could not instantiate a new " + responseClass.getName(), e);
            return null;
        }
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
