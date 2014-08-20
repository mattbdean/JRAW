package net.dean.jraw.models;

/**
 * This class represents a String that has a raw, unformatted version in Markdown, and a formatted version in HTML.
 */
public class RenderStringPair {
	private final String markdown;
	private final String html;

	public RenderStringPair(String markdown, String html) {
		this.markdown = markdown;
		this.html = html;
	}

	/**
	 * Get the raw, unformatted text. Characters such as '*', '&lt;', '&gt;', and '&amp;' will likely be escaped.
	 * @return The raw, unformatted text.
	 */
	public String getMarkdown() {
		return markdown;
	}

	/**
	 * The formatted HTML that will be displayed on Reddit
	 * @return Formatted HTML
	 */
	public String getHtml() {
		return html;
	}
}
