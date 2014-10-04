package net.dean.jraw.models;

/**
 * This class represents a String that has a raw, unformatted version in Markdown, and a formatted version in HTML.
 */
public class RenderStringPair {
    private final String markdown;
    private final String html;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RenderStringPair that = (RenderStringPair) o;

        return !(html != null ? !html.equals(that.html) : that.html != null) &&
                !(markdown != null ? !markdown.equals(that.markdown) : that.markdown != null);
    }

    @Override
    public int hashCode() {
        int result = markdown != null ? markdown.hashCode() : 0;
        result = 31 * result + (html != null ? html.hashCode() : 0);
        return result;
    }
}
