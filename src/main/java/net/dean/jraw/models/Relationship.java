package net.dean.jraw.models;

public enum Relationship {
	FRIEND(true),
	ENEMY(true),
	MODERATOR,
	MODERATOR_INVITE,
	CONTRIBUTOR,
	BANNED,
	WIKIBANNED,
	WIKICONTRIBUTOR;


	private boolean userToUser;

	private Relationship() {
		this(false);
	}

	private Relationship(boolean userToUser) {
		this.userToUser = userToUser;
	}

	/**
	 * Returns true if this relationship models one that can be had from one user to the next, or false if this relationship
	 * is of a subreddit or another Thing.
	 *
	 * @return If this is a user-to-user relationship
	 */
	public boolean isUserToUser() {
		return userToUser;
	}
}
