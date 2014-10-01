package net.dean.jraw.models;

/**
 * Represents the flair of either an author or a submission
 */
public class Flair {
    /**
     * The CSS class that will applied to the author's name or the submission
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flair flair = (Flair) o;

        return !(cssClass != null ? !cssClass.equals(flair.cssClass) : flair.cssClass != null) &&
                !(text != null ? !text.equals(flair.text) : flair.text != null);

    }

    @Override
    public int hashCode() {
        int result = cssClass != null ? cssClass.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Flair {" +
                "cssClass='" + cssClass + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
