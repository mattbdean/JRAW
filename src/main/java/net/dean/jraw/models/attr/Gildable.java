package net.dean.jraw.models.attr;

import net.dean.jraw.models.meta.JsonProperty;

/**
 * Specifies that this model is able to be given Reddit Gold
 */
public interface Gildable extends JsonAttribute {
    /**
     * Gets the number of times this comment has received Reddit Gold. If this model was retrieved from the inbox, then
     * this will return 0.
     *
     * @return The number of times this comment has received Reddit Gold
     */
    @JsonProperty
    public Integer getTimesGilded();
}
