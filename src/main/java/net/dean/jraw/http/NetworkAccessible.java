package net.dean.jraw.http;

/**
 * This interface shows that the implementing class can send HTTP requests.
 * @param <T> The type of response to
 */
public interface NetworkAccessible<T extends RestResponse, U extends HttpClient<T>> {
    /**
     * Gets the HttpClient that allows this class to send HTTP requests
     * @return An HttpClient
     */
    public U getHttpClient();
}
