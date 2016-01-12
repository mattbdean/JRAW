package net.dean.jraw.models.attr;

import net.dean.jraw.models.Thing;
import net.dean.jraw.models.VoteDirection;
import net.dean.jraw.models.meta.JsonProperty;

/**
 * Indicates that this model can be voted on
 *
 * @see net.dean.jraw.managers.AccountManager#vote(Thing, VoteDirection)
 */
public interface Votable extends JsonAttribute {
    /**
     * The net score of this Votable model
     *
     * @return The link's net score
     */
    @JsonProperty
    Integer getScore();


    /**
     * Gets the way in which the logged in user voted. If there is none,
     * {@link net.dean.jraw.models.VoteDirection#NO_VOTE} will be returned.
     *
     * @return The way in which the logged in user voted
     */
    @JsonProperty
    VoteDirection getVote();
}
