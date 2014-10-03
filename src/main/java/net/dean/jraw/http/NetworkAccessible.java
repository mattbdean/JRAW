package net.dean.jraw.http;

import com.squareup.okhttp.Request;

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
     * Short for {@code getCreator().request(https)}
     * @param https Whether to execute the request with HTTPS. Can be changed later.
     * @return A new RequestBuilder
     * @see net.dean.jraw.http.RestClient#request(boolean)
     */
    public default RequestBuilder request(boolean https) {
        return getCreator().request(https);
    }

    /**
     * Short for {@code getCreator().request()}
     * @return A new RequestBuilder
     * @see net.dean.jraw.http.RestClient#request()
     */
    public default RequestBuilder request() {
        return getCreator().request();
    }

    /**
     * Short for {@code getCreator().execute(request)}
     * @param r The Request to execute
     * @return A RestResponse
     * @throws NetworkException If the request was not successful
     */
    public default U execute(Request r) throws NetworkException {
        return getCreator().execute(r);
    }
}
