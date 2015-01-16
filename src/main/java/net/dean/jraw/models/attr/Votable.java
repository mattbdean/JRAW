package net.dean.jraw.models.attr;

import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.VoteDirection;

/**
 * Provides a way to show that this Thing is able to be voted on
 *
 * @see net.dean.jraw.models.VoteDirection
 */
public interface Votable extends JsonAttribute {

    /**
     * Gets the amount of upvotes the object has received
     *
     * @return The amount of upvotes the object has received
     * @deprecated With recent changes in Reddit, this will always be equivalent to {@link #getScore()}. See
     *             <a href="https://github.com/reddit/reddit/commit/8c9ad4e">this commit</a> for more information.
     */
    @JsonProperty
    @Deprecated
    public Integer getUpvotes();

    /**
     * Gets the amount of downvotes the object has received.
     *
     * @return The amount of downvotes the post has received
     * @deprecated With recent changes in Reddit, this will always return 0. See
     *             <a href="https://github.com/reddit/reddit/commit/8c9ad4e">this commit</a> for more information.
     */
    @JsonProperty
    @Deprecated
    public Integer getDownvotes();

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
