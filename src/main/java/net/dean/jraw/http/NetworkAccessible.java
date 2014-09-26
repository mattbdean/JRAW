package net.dean.jraw.http;

/**
 * This interface provides a way to distinguish classes that can make HTTP requests
 * @param <T> A RestClient
 */
public interface NetworkAccessible<T extends RestClient> {
    /**
     * Gets the RestClient that enables this class to send HTTP requests
     * @return The RestClient
     */
    public T getCreator();
}
