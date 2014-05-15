package net.dean.jraw;

/**
 * This Exception is thrown when an error is returned by Reddit's JSON API
 */
public class RedditException extends Exception {
	/**
	 * Instantiates a new RedditException
	 * @param message The message to print to the standard error
	 */
	public RedditException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new RedditException
	 * @param message The message to print to the standard error
	 * @param cause The cause of this exception
	 */
	public RedditException(String message, Throwable cause) {
		super(message, cause);
	}
}
