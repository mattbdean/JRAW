package net.dean.jraw.pagination;

/**
 * Used by UserPaginatorSubmission to fill in the "where" in /user/&lt;username&gt;/&lt;where&gt;
 */
public enum Where {
	// Both submissions and comments
	OVERVIEW(true, true),
	GILDED(true, true),

	// Only submissions
	SUBMITTED(true, false),
	LIKED(true, false),
	DISLIKED(true, false),
	HIDDEN(true, false),
	SAVED(true, false),

	// Only comments
	COMMENTS(false, true);

	private boolean hasSubmissions;
	private boolean hasComments;

	private Where(boolean hasSubmissions, boolean hasComments) {
		this.hasSubmissions = hasSubmissions;
		this.hasComments = hasComments;
	}

	/**
	 * Whether this sorting could contain submissions in it
	 * @return
	 */
	public boolean hasSubmissions() {
		return hasSubmissions;
	}

	/**
	 * Whether this sorting could contain comments in it
	 * @return
	 */
	public boolean hasComments() {
		return hasComments;
	}
}
