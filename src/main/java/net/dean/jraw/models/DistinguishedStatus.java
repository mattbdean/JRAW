package net.dean.jraw.models;

import net.dean.jraw.NoSuchEnumConstantException;

/**
 * Represents the status of a link or comment poster
 */
public enum DistinguishedStatus {
    /** Represents a normal user */
    NORMAL("null"),
    /** Represents a moderator */
    MODERATOR,
    /** Represents an administrator */
    ADMIN,
    /**
     * Various other special distinguishes (most commonly seen as the darker red [Δ] "admin emeritus"
     * (<a href="http://www.reddit.com/r/bestof/comments/175prt/alilarter_connects_with_a_user_who_has_a/c82tlns">example</a>
     */
    SPECIAL;

    /** The value of the "distinguished" JSON field */
    private String jsonValue;

    private DistinguishedStatus() {
        this.jsonValue = this.name().toLowerCase();
    }

    private DistinguishedStatus(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    /**
     * Searches for a UserRole by its supposed JSON value
     *
     * @param jsonValue The value to look for
     * @return A DistinguishedState that has the same JSON value as the one given
     */
    public static DistinguishedStatus getByJsonValue(String jsonValue) {
        if (jsonValue == null) {
            return NORMAL;
        }

        for (DistinguishedStatus state : values()) {
            if (state.getJsonValue().equals(jsonValue)) {
                return state;
            }
        }

        throw new NoSuchEnumConstantException(DistinguishedStatus.class, jsonValue);
    }

    /**
     * Gets the value that would be found in the JSON response
     * @return The value that would be found in the JSON response
     */
    public String getJsonValue() {
        return jsonValue;
    }
}
