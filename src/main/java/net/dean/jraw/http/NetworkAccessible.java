package net.dean.jraw.http;

/**
 * This interface provides a way to distinguish classes that can make HTTP requests
 * @param <U> The type of response returned by the RestClient
 * @param <T> A RestClient
 */
public interface NetworkAccessible<U extends RestResponse, T extends RestClient<U>> {
    /**
     * Gets the RestClient that enables this class to send HTTP requests
     * @return The RestClient
     */
    public T getCreator();

    /**
     * Creates a new RequestBuilder
     * @return A new RequestBuilder, in which the host is {@link RestClient#getDefaultHost()} and HTTPS will be used if
     * {@link RestClient#isHttpsDefault()} is true.
     */
    public default RestRequest.Builder request() {
        return getCreator().request();
    }

    /**
     * Executes a RESTful HTTP request that contains sensitive information
     *
     * @param r The request to execute
     * @return A RestResponse from the resulting response
     * @throws NetworkException If the request was not successful
     */
    public default U execute(RestRequest r) throws NetworkException {
        return getCreator().execute(r);
    }
}
