package net.dean.jraw;

public class ApiException extends Exception {
    protected final String constant;
    protected final String explanation;

    public ApiException(String constant, String explanation) {
        super(String.format("API returned error: \"%s\" (\"%s\")", constant, explanation));
        this.constant = constant;
        this.explanation = explanation;
    }

    public String getConstant() {
        return constant;
    }

    public String getExplanation() {
        return explanation;
    }
}
