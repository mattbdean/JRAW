package net.dean.jraw.auth;

/**
 * Generalized interface for storing API tokens in a key-value fashion. Specifically, API tokens such as a refresh or
 * access token. Reading and writing actions have the ability to be blocking and should be treated as such.
 */
public interface TokenStore {
    /** Checks if a token is already stored */
    boolean isStored(String key);

    /**
     * Gets a token. If none is found, then a {@link NoSuchTokenException} is thrown
     * @throws NoSuchTokenException If the given key does not have a value
     */
    String readToken(String key) throws NoSuchTokenException;

    /** Writes a token. */
    void writeToken(String key, String token);
}
