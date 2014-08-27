package net.dean.jraw.models;

import org.codehaus.jackson.JsonNode;

/**
 * Provides a way to show that this Thing is able to be voted on
 *
 * @see net.dean.jraw.models.VoteDirection
 */
public interface Votable {
    /**
     * Gets the amount of upvotes the object has received
     *
     * @return The amount of upvotes the object has received
     */
    @JsonInteraction
    default Integer getUpvotes() {
        return getDataNode().get("ups").getIntValue();
    }

    /**
     * Gets the amount of downvotes the object has received. With recent changes in Reddit, this will always return 0.
     * See <a href="https://github.com/reddit/reddit/commit/8c9ad4e">this commit</a> for more information.
     *
     * @return The amount of downvotes the post has received
     */
    @JsonInteraction
    default Integer getDownvotes() {
        return getDataNode().get("downs").getIntValue();
    }

    /**
     * Gets the way in which the logged in user voted. If there is none, this method will always return
     * {@link net.dean.jraw.models.VoteDirection#NO_VOTE}.
     * @return The way in which the logged in user voted
     */
    @JsonInteraction
    default VoteDirection getVote() {
        JsonNode likes = getDataNode().get("likes");
        if (likes.isNull()) {
            return VoteDirection.NO_VOTE;
        }

        return likes.getBooleanValue() ? VoteDirection.UPVOTE : VoteDirection.DOWNVOTE;
    }

    public JsonNode getDataNode();
}
