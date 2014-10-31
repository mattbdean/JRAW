package net.dean.jraw.models.attr;

import net.dean.jraw.models.JsonProperty;

import java.util.Date;

/**
 * Indicates that this object was created
 */
public interface Created extends JsonAttribute {
    /**
     * Gets the date this object was created in local time
     * @return Date created in local time
     */
    @JsonProperty
    public default Date getCreated() {
        // created in seconds, Date constructor wants milliseconds
        return new Date(getDataNode().get("created").getLongValue() * 1000);
    }

    /**
     * Gets the date this object was created in UTC
     * @return Date created in UTC
     */
    @JsonProperty
    public default Date getCreatedUtc() {
        // created in seconds, Date constructor wants milliseconds
        return new Date(getDataNode().get("created_utc").getLongValue() * 1000);
    }
}
