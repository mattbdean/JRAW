package net.dean.jraw.http;

import com.squareup.okhttp.MediaType;

/**
 * A list of common MediaType objects used throughout the project
 */
public enum MediaTypes {
    /** Represents URL-encoded form data */
    FORM_ENCODED("application/x-www-form-urlencoded"),
    /** Represents a JavaScript Object Notation file with a MIME type of "application/json" */
    JSON("application/json"),
    /** Represents a HyperText Markup Language file with a MIME type of "text/html" */
    HTML("text/html"),
    /** Represents a Cascading Style Sheet file with a MIME type of "text/css" */
    CSS("text/css");

    private MediaType type;
    private MediaTypes(String types) {
        this.type = MediaType.parse(types);
    }

    /**
     * Gets the MediaType object associated with this enum entry
     * @return A MediaType
     */
    public MediaType type() {
        return type;
    }
}
