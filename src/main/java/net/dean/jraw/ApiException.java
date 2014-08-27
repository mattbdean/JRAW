package net.dean.jraw;

/**
 * This class provides to translate errors returned by the Reddit API into Java Exceptions
 */
public class ApiException extends Exception {
    protected final String constant;
    protected final String explanation;

    /**
     * Instantiates a new ApiException
     * @param constant The constant error string
     * @param explanation The localized explanation
     */
    public ApiException(String constant, String explanation) {
        super(String.format("API returned error: \"%s\" (\"%s\")", constant, explanation));
        this.constant = constant;
        this.explanation = explanation;
    }

    /**
     * Gets the constant error string
     * @return The constant
     */
    public String getConstant() {
        return constant;
    }

    /**
     * Gets the localized explanation
     * @return The explanation
     */
    public String getExplanation() {
        return explanation;
    }
}
