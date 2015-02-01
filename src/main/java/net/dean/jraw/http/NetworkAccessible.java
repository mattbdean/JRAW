package net.dean.jraw.http;

/**
 * Denotes that this class has the ability to send HTTP requests
 */
public interface NetworkAccessible {
    /**
     * Gets the HttpClient that allows this class to send HTTP requests
     * @return An HttpClient
     */
    public HttpAdapter getHttpAdapter();
}
