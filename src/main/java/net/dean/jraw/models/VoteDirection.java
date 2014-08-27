package net.dean.jraw.models;

/**
 * Represents the way a user can vote on a Submission or Comment
 */
public enum VoteDirection {
    UPVOTE(1),
    DOWNVOTE(-1),
    NO_VOTE(0);

    private int value;

    private VoteDirection(int value) {
        this.value = value;
    }

    /**
     * The value that the Reddit JSON API will be expecting for this vote direction
     * @return The value of this VoteDirection
     */
    public int getValue() {
        return value;
    }
}
