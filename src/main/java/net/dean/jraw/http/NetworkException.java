package net.dean.jraw.http;

/**
 * This Exception is thrown when an error is returned by Reddit's JSON API
 */
public class NetworkException extends Exception {
    private final int code;

    /**
     * Instantiates a new NetworkException
     *
     * @param message The message to print to the standard error
     */
    public NetworkException(String message) {
        super(message);
        this.code = -1;
    }

    /**
     * Instantiates a new NetworkException
     *
     * @param message The message to print to the standard error
     * @param cause   The cause of this exception
     */
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
        this.code = -1;
    }

    /**
     * Instantiates a new NetworkException with a desired HTTP code of 200
     * @param httpCode The code that was returned from the request
     */
    public NetworkException(int httpCode) {
        super(String.format("Request returned bad code (%s)", httpCode));
        this.code = httpCode;
    }
    /**
     * Gets the status code returned by the HTTP request
     * @return The status code
     */
    public int getCode() {
        return code;
    }
}
