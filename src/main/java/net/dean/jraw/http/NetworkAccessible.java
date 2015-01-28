package net.dean.jraw.http;

/**
 * This interface shows that the implementing class has access to a class that can send HTTP requests
 */
public interface NetworkAccessible {
    /**
     * Gets the HttpClient that allows this class to send HTTP requests
     * @return An HttpClient
     */
    public HttpAdapter getHttpAdapter();
}
