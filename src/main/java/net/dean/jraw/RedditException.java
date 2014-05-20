package net.dean.jraw;

import java.util.Optional;

/**
 * This Exception is thrown when an error is returned by Reddit's JSON API
 */
public class RedditException extends Exception {
	private Optional<RedditResponse.ApiError> apiError;

	/**
	 * Instantiates a new RedditException
	 * @param message The message to print to the standard error
	 */
	public RedditException(String message) {
		super(message);
		initError(null);
	}

	/**
	 * Instantiates a new RedditException
	 * @param message The message to print to the standard error
	 * @param cause The cause of this exception
	 */
	public RedditException(String message, Throwable cause) {
		super(message, cause);
		initError(null);
	}

	public RedditException(RedditResponse.ApiError apiError) {
		super(String.format("API returned error: \"%s\" (%s)", apiError.getConstant(), apiError.getHumanReadable()));
		initError(apiError);
	}

	private void initError(RedditResponse.ApiError error) {
		apiError = (error == null ? Optional.empty() : Optional.of(error));
	}

	public Optional<RedditResponse.ApiError> getApiError() {
		return apiError;
	}
}
