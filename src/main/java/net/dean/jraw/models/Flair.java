package net.dean.jraw.models;

/**
 * Represents the flair of either an author or a link
 */
public class Flair {
	/**
	 * The CSS class that will applied to the author's name or the link
	 */
	private String cssClass;

	/**
	 * The text of the flair
	 */
	private String text;

	/**
	 * Instantiates a new Flair
	 *
	 * @param cssClass The CSS class
	 * @param text     The text
	 */
	public Flair(String cssClass, String text) {
		this.cssClass = cssClass;
		this.text = text;
	}

	/**
	 * Gets the CSS class which will be used on the Reddit site to style the author's name or the link
	 *
	 * @return The CSS class
	 */
	public String getCssClass() {
		return cssClass;
	}

	/**
	 * Gets the text of the flair
	 *
	 * @return The text of the flair
	 */
	public String getText() {
		return text;
	}
}
