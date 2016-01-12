package net.dean.jraw.models.attr;

import net.dean.jraw.models.DistinguishedStatus;
import net.dean.jraw.models.meta.JsonProperty;

/**
 * Indicates this model was posted by a user of elevated privilege (usually moderators or administrators) speaking
 * officially.
 *
 * See {@link net.dean.jraw.models.DistinguishedStatus} for a full list of roles.
 */
public interface Distinguishable extends JsonAttribute {
    /** Gets the status of this model */
    @JsonProperty
    DistinguishedStatus getDistinguishedStatus();
}
