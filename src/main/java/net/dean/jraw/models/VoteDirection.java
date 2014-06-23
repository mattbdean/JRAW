package net.dean.jraw.models;

public enum VoteDirection {
	UPVOTE(1),
	DOWNVOTE(-1),
	NO_VOTE(0);

	private int value;

	private VoteDirection(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
