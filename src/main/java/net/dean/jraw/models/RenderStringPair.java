package net.dean.jraw.models;

/**
 * This class represents a String that has a raw, unformatted version in Markdown, and a formatted version in HTML.
 */
public class RenderStringPair {
    private final String markdown;
    private final String html;

    /**
     * Instantiates a new RenderStringPair. Used most commonly by {@link net.dean.jraw.models.JsonModel#data(String, Class)}
     * when {@link net.dean.jraw.JrawConfig#loadRenderStringPairHtml} is false.
     * @param markdown The markdown version
     */
    public RenderStringPair(String markdown) {
        this.markdown = markdown;
        this.html = null;
    }

    /**
     * Instantiates a new RenderStringPair
     * @param markdown The markdown version
     * @param html The HTML version
     */
    public RenderStringPair(String markdown, String html) {
        this.markdown = markdown;
        this.html = html;
    }

    /**
     * Get the raw, unformatted text. Characters such as '*', '&lt;', '&gt;', and '&amp;' will likely be escaped.
     * @return The raw, unformatted text.
     */
    public String md() {
        return markdown;
    }

    /**
     * The formatted HTML that will be displayed on Reddit
     * @return Formatted HTML
     */
    public String html() {
        return html;
    }

    @Override
    public String toString() {
        return markdown;
    }
}
