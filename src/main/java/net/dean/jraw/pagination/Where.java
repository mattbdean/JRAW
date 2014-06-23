package net.dean.jraw.pagination;

/**
 * Used by UserPaginatorSubmission to fill in the "where" in {@code /user/<username>/<where>}
 */
public enum Where {
	// Both submissions and comments
	/** Represents the user overview. Contains both submissions and comments */
	OVERVIEW(true, true),
	/** Represents the user's gilded submissions and comments */
	GILDED(true, true),

	// Only submissions
	/** Represents the user's submitted links */
	SUBMITTED(true, false),
	/** Represents the user's liked (upvoted) submissions */
	LIKED(true, false),
	/** Represents the user's disliked (downvoted) submissions */
	DISLIKED(true, false),
	/** Represents the user's hidden submissions */
	HIDDEN(true, false),
	/** Represents the user's saved submissions */
	SAVED(true, false),

	// Only comments
	/** Represents the user's comments */
	COMMENTS(false, true);

	private boolean hasSubmissions;
	private boolean hasComments;

	private Where(boolean hasSubmissions, boolean hasComments) {
		this.hasSubmissions = hasSubmissions;
		this.hasComments = hasComments;
	}

	/**
	 * Whether this sorting could contain submissions in it
	 * @return If this Where contains submissions
	 */
	public boolean hasSubmissions() {
		return hasSubmissions;
	}

	/**
	 * Whether this sorting could contain comments in it
	 * @return If this Where contains submissions
	 */
	public boolean hasComments() {
		return hasComments;
	}
}
