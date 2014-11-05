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

    /**
     * Executes an HTTP request with basic authentication. See <a href="http://tools.ietf.org/html/rfc2617">RFC 2617</a>
     * for more information.
     *
     * @param request The request to send
     * @param username The username to use. Will be encoded into base 64
     * @param password The password to use. Will be encoded into base 64
     * @return A RestResponse from the resulting request
     * @throws NetworkException If the request was not successful
     */
    public U executeWithBasicAuth(RestRequest request, String username, String password)
            throws NetworkException;
}
