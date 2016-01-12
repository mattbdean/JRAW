package net.dean.jraw.models.attr;

import net.dean.jraw.models.meta.JsonProperty;

import java.util.Date;

/** Indicates that the object this model represents was created by a user. */
public interface Created extends JsonAttribute {
    /** Gets the date this model was created in UTC */
    @JsonProperty
    Date getCreated();
}
