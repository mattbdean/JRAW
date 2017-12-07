package net.dean.jraw.models

enum class DistinguishedStatus {
    /** Represents a normal user */
    NORMAL,

    /** Represents a moderator */
    MODERATOR,

    /** Represents an administrator */
    ADMIN,

    /**
     * Various other special distinguishes (most commonly seen as the darker red `[Δ]` "admin emeritus". See
     * [here](http://www.reddit.com/r/bestof/comments/175prt/alilarter_connects_with_a_user_who_has_a/c82tlns) for an
     * example.
     */
    SPECIAL,

    /** Represents an automated message notifying user that they have been gifted Reddit gold */
    GOLD;
}
