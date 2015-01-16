package net.dean.jraw.models.attr;

import net.dean.jraw.models.DistinguishedStatus;
import net.dean.jraw.models.meta.JsonProperty;

/**
 * Indicates this Thing was posted by a redditor of elevated role in the website, such as an administrator or moderator.
 * See {@link net.dean.jraw.models.DistinguishedStatus} for a full list of roles.
 */
public interface Distinguishable extends JsonAttribute {
    /**
     * Gets the role of the poster of this Thing
     * @return The role of the poster of this Thing
     */
    @JsonProperty
    public DistinguishedStatus getDistinguishedStatus();
}
