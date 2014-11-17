package net.dean.jraw;

/**
 * This class provides a way to translate errors returned by the Reddit API into Java Exceptions
 */
public class ApiException extends Exception {
    private final String reason;
    private final String explanation;

    /**
     * Instantiates a new ApiException
     *
     * @param reason The reason error string
     * @param explanation The localized explanation
     */
    public ApiException(String reason, String explanation) {
        super(String.format("API returned error: \"%s\" (\"%s\")", reason, explanation));
        this.reason = reason;
        this.explanation = explanation;
    }

    /**
     * Instantiates a new ApiException
     * @param message The reason this error was thrown
     * @param cause What caused the error
     */
    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.reason = null;
        this.explanation = null;
    }

    /**
     * Gets the reason error string. A full list can be found
     * <a href="https://github.com/reddit/reddit/blob/master/r2/r2/lib/errors.py">here</a>.
     *
     * @return The reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Gets the localized explanation
     * @return The explanation
     */
    public String getExplanation() {
        return explanation;
    }
}
