package net.dean.jraw;

public class ApiException extends Exception {
	protected final String constant;
	protected final String humanReadable;
	protected final String third;

	public ApiException(String constant, String humanReadable, String third) {
		super(String.format("API returned error: \"%s\" (\"%s\")", constant, humanReadable));
		this.humanReadable = humanReadable;
		this.constant = constant;
		this.third = third;
	}

	public String getConstant() {
		return constant;
	}

	public String getHumanReadable() {
		return humanReadable;
	}

	public String getThird() {
		return third;
	}
}
