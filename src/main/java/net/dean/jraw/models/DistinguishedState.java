package net.dean.jraw.models;

/**
 * Represents the status of a link poster. A JSON value of "moderator" means the poster is a moderator, "admin" if an admin,
 * or null if the poster is a normal user.
 */
public enum DistinguishedState {
	/** Represents a normal user */
	NORMAL(null),
	/** Represents a moderator */
	MODERATOR("moderator"),
	/** Represents an administrator */
	ADMIN("admin");

	/** The value that will be found if the key of "distinguished" is looked up */
	private String jsonValue;

	private DistinguishedState(String jsonValue) {
		this.jsonValue = jsonValue;
	}

	/**
	 * Gets the value that would be found in the JSON response
	 */
	public String getJsonValue() {
		return jsonValue;
	}

	/**
	 * Searches for a DistinguishedState by its supposed JSON value
	 * @param jsonValue The value to look for
	 * @return A DistinguishedState that has the same JSON value as the one given
	 */
	public static DistinguishedState getByJsonValue(String jsonValue) {
		if (jsonValue == null) {
			return NORMAL;
		}

		for (DistinguishedState state : values()) {
			if (state.getJsonValue().equals(jsonValue)) {
				return state;
			}
		}

		return null;
	}
}
