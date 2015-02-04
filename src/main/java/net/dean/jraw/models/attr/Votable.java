package net.dean.jraw.models.attr;

import net.dean.jraw.models.VoteDirection;
import net.dean.jraw.models.meta.JsonProperty;

/**
 * Provides a way to show that this Thing is able to be voted on.
 *
 * @see net.dean.jraw.models.VoteDirection
 */
public interface Votable extends JsonAttribute {
    /**
     * The net score of this Votable model
     *
     * @return The link's net score
     */
    @JsonProperty
    public Integer getScore();


    /**
     * Gets the way in which the logged in user voted. If there is none,
     * {@link net.dean.jraw.models.VoteDirection#NO_VOTE} will be returned.
     *
     * @return The way in which the logged in user voted
     */
    @JsonProperty
    public VoteDirection getVote();
}
