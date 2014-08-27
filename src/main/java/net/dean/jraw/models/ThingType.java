package net.dean.jraw.models;

/**
 * An enumeration of types of objects in Reddit's API. Each type has a prefix of "t[0-6,8]". When this prefix, an
 * underscore, and the ID of the Thing are combined in that order, you get that Thing's full name
 * ({@link net.dean.jraw.models.core.Thing#getFullName()}). For example, the subreddit /r/funny has a prefix of "t5", an ID of
 * "2qh33", and a full name of "t5_2qh33"
 *
 * @author Matthew Dean
 */
public enum ThingType {
    /** Represents a comment with the prefix "t1" */
    COMMENT(1),
    /** Represents an account with the prefix "t2" */
    ACCOUNT(2),
    /** Represents a submission with the prefix "t3" */
    LINK(3),
    /** Represents a message with the prefix "t4" */
    MESSAGE(4),
    /** Represents a subreddit with the prefix "t5" */
    SUBREDDIT(5),
    /** Represents an award with the prefix "t6" */
    AWARD(6),
    /** Represents a promo campaign with the prefix "t8" */
    PROMO_CAMPAIGN(8),
    /** Represents a listing */
    LISTING("Listing"),
    /** Represents a "more" object. See {@link net.dean.jraw.models.core.More} */
    MORE("more"),
    /** Represents a MultiReddit */
    MULTI("LabeledMulti");


    /**
     * The Thing's prefix
     */
    private String prefix;

    /**
     * Instantiates a new ThingType
     *
     * @param id The integer to append to "t" to get the prefix
     */
    private ThingType(int id) {
        this.prefix = "t" + id;
    }

    private ThingType(String custom) {
        this.prefix = custom;
    }

    /**
     * Gets the prefix of this type (ex: "t1", "t2", etc.)
     *
     * @return The prefix of this type
     */
    public String getPrefix() {
        return prefix;
    }
}
