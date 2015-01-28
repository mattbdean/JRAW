package net.dean.jraw.http;

/**
 * This Exception is thrown when an HTTP response is not successful
 *
 * @see com.squareup.okhttp.Response#isSuccessful()
 */
public class NetworkException extends Exception {
    private final int code;

    /**
     * Instantiates a new NetworkException
     *
     * @param message The detail message of this Exception
     */
    public NetworkException(String message) {
        super(message);
        this.code = -1;
    }

    /**
     * Instantiates a new NetworkException
     *
     * @param message The detail message of this Exception
     * @param cause   The cause of this Exception
     */
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
        this.code = -1;
    }

    /**
     * Instantiates a NetworkException
     *
     * @param httpCode The code that was returned from the request
     */
    public NetworkException(int httpCode) {
        super(String.format("Request returned non-successful status code (%s)", httpCode));
        this.code = httpCode;
    }
    /**
     * Gets the status code returned by the HTTP request. Will be -1 if a constructor other than
     * {@link #NetworkException(int)} was used.
     * @return The status code
     */
    public int getCode() {
        return code;
    }
}
