package net.dean.jraw;

public class ApiException extends Exception {
	protected final String constant;
	protected final String explanation;
	protected final String third;

	public ApiException(String constant, String explanation, String third) {
		super(String.format("API returned error: \"%s\" (\"%s\")", constant, explanation));
		this.explanation = explanation;
		this.constant = constant;
		this.third = third;
	}

	public String getConstant() {
		return constant;
	}

	public String getExplanation() {
		return explanation;
	}

	public String getThird() {
		return third;
	}
}
