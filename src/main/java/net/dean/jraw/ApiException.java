package net.dean.jraw;

/**
 * This class provides a way to translate errors returned by the Reddit API into Java Exceptions
 */
public class ApiException extends Exception {
    private final String code;
    private final String explanation;

    /**
     * Instantiates a new ApiException from the Reddit API
     *
     * @param code The code error string
     * @param explanation The localized explanation
     */
    public ApiException(String code, String explanation) {
        super(String.format("API returned error: \"%s\" (\"%s\")", code, explanation));
        this.code = code;
        this.explanation = explanation;
    }

    /**
     * Instantiates a new ApiException
     *
     * @param msg The message
     */
    public ApiException(String msg) {
        super(msg);
        this.code = null;
        this.explanation = null;
    }

    /**
     * Instantiates a new ApiException
     * @param msg The message
     * @param cause The cause of this exception
     */
    public ApiException(String msg, Throwable cause) {
        super(msg, cause);
        this.code = null;
        this.explanation = null;
    }

    /**
     * Gets the code error string. A full list can be found
     * <a href="https://github.com/reddit/reddit/blob/master/r2/r2/lib/errors.py">here</a>.
     *
     * @return The code
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the localized explanation
     * @return The explanation
     */
    public String getExplanation() {
        return explanation;
    }
}
