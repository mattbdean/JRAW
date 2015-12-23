package net.dean.jraw.models;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;

/**
 * The Thing class is the base class for most data structures returned by the reddit API. Every Thing has an
 * ID and a fullname (referred to as an ID36 in the Reddit source code). To construct a fullname, simply prepend
 * {@code tX_}, where {@code X} is an integer. For a valid list of these prefixes, see
 * <a href="https://www.reddit.com/dev/api/oauth#fullnames">here</a>. However, this is not the same for all classes. The
 * {@link Account} class's fullname will return the username instead of {@code t2_<id>}.
 *
 * @author Matthew Dean
 */
@Model(kind = Model.Kind.ABSTRACT)
public abstract class Thing extends RedditObject {

    /** Instantiates a new Thing */
    public Thing(JsonNode dataNode) {
        super(dataNode);
    }

    /** Gets this Thing's unique identifier, e.g. "8xwlg" */
    @JsonProperty
    public String getId() {
        return data("id");
    }

    /** Gets this Thing's fullname, e.g. "t1_c3v7f8u" */
    @JsonProperty
    public String getFullName() {
        return data("name");
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        }

        if (!(otherObject instanceof Thing)) {
            return false;
        }

        // Now that we know that the object we are checking is a Thing, cast it as one.
        Thing thing = (Thing) otherObject;

        // Compare the ID String values and use that as the return value
        return this.getId().equals(thing.getId());
    }
}
