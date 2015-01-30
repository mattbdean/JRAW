package net.dean.jraw.http;


import com.google.common.net.MediaType;

/**
 * A list of common MediaType objects used throughout the project
 */
public enum MediaTypes {
    /** Represents URL-encoded form data */
    FORM_ENCODED("application/x-www-form-urlencoded"),
    /** Represents plain text with a MIME type of "text/plain" */
    PLAIN("text/plain"),
    /** Represents a JavaScript Object Notation file with a MIME type of "application/json" */
    JSON("application/json"),
    /** Represents a HyperText Markup Language file with a MIME type of "text/html" */
    HTML("text/html"),
    /** Represents a Cascading Style Sheet file with a MIME type of "text/css" */
    CSS("text/css");

    private final MediaType type;
    private final String typeString;
    private MediaTypes(String typeString) {
        this.typeString = typeString;
        this.type = MediaType.parse(typeString);
    }

    /**
     * Gets the MediaType object associated with this enum entry
     * @return A MediaType
     */
    public MediaType type() {
        return type;
    }

    /**
     * Returns the string representation of this MediaType
     */
    public String string() {
        return typeString;
    }

    @Override
    public String toString() {
        return string();
    }
}
