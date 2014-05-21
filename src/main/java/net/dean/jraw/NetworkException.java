package net.dean.jraw;

/**
 * This Exception is thrown when an error is returned by Reddit's JSON API
 */
public class NetworkException extends Exception {

	/**
	 * Instantiates a new RedditException
	 *
	 * @param message The message to print to the standard error
	 */
	public NetworkException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new RedditException
	 *
	 * @param message The message to print to the standard error
	 * @param cause   The cause of this exception
	 */
	public NetworkException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetworkException(int httpCodeDesired) {
		this(httpCodeDesired, 200);
	}

	public NetworkException(int httpCodeDesired, int httpCodeActual) {
		super(String.format("Status code not %s (was %s)", httpCodeDesired, httpCodeActual));
	}
}
