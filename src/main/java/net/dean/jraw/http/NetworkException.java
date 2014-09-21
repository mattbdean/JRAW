package net.dean.jraw.http;

/**
 * This Exception is thrown when an error is returned by Reddit's JSON API
 */
public class NetworkException extends Exception {
    private final int desired;
    private final int actual;

    /**
     * Instantiates a new NetworkException
     *
     * @param message The message to print to the standard error
     */
    public NetworkException(String message) {
        super(message);
        this.desired = -1;
        this.actual = -1;
    }

    /**
     * Instantiates a new NetworkException
     *
     * @param message The message to print to the standard error
     * @param cause   The cause of this exception
     */
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
        this.desired = -1;
        this.actual = -1;
    }

    /**
     * Instantiates a new NetworkException with a desired HTTP code of 200
     * @param httpCode The code that was returned from the request
     */
    public NetworkException(int httpCode) {
        this(200, httpCode);
    }

    /**
     * Instantiates a new NetworkException
     * @param httpCodeDesired The desired HTTP code
     * @param httpCodeActual The actual HTTP code
     */
    public NetworkException(int httpCodeDesired, int httpCodeActual) {
        super(String.format("Status code not %s (was %s)", httpCodeDesired, httpCodeActual));
        this.desired = httpCodeDesired;
        this.actual = httpCodeActual;

        if (httpCodeDesired == httpCodeActual) {
            throw new IllegalArgumentException(String.format("Desired HTTP code (%s) was equal to the actual code (%s)",
                    httpCodeDesired, httpCodeActual));
        }
    }

    /**
     * Gets the expected HTTP code
     * @return The expected HTTP code
     */
    public int getDesiredCode() {
        return desired;
    }

    /**
     * Gets the status code returned by the HTTP request
     * @return The actual status code
     */
    public int getActualCode() {
        return actual;
    }
}
