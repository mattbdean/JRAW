package net.dean.jraw.models.attr;

import net.dean.jraw.models.JsonInteraction;
import org.codehaus.jackson.JsonNode;

import java.util.Date;

/**
 * Indicates that this object was created
 */
public interface Created {
    /**
     * Gets the date this object was created in local time
     * @return Date created in local time
     */
    @JsonInteraction
    public default Date getCreated() {
        // created in seconds, Date constructor wants milliseconds
        return new Date(getDataNode().get("created").getLongValue() * 1000);
    }

    /**
     * Gets the date this object was created in UTC
     * @return Date created in UTC
     */
    @JsonInteraction
    public default Date getCreatedUtc() {
        // created in seconds, Date constructor wants milliseconds
        return new Date(getDataNode().get("created_utc").getLongValue() * 1000);
    }

    /**
     * See {@link net.dean.jraw.models.JsonModel#getDataNode()}
     * @return The JsonNode to use for methods annotated with the {@link JsonInteraction} annotation
     */
    public JsonNode getDataNode();
}
