package net.dean.jraw.http;

/**
 * This interface provides a way to mark classes that can make HTTP requests
 * @param <U> The type of response returned by the RestClient
 */
public interface HttpClient<U extends RestResponse> {

    /**
     * Creates a new builder object that can be used to model HTTP requests
     *
     * @return A new RestRequest.Builder
     */
    public RestRequest.Builder request();

    /**
     * Executes an HTTP request
     *
     * @param r The request to execute
     * @return A RestResponse from the resulting request
     * @throws NetworkException If the request was not successful
     */
    public U execute(RestRequest r) throws NetworkException;
}
