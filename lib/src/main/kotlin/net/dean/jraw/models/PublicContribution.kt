package net.dean.jraw.models

/**
 * Represents any model that can be created for multiple other reddit users to see. In practice, this is the superclass
 * for [Comment] and [Submission].
 */
abstract class PublicContribution(type: ThingType) : Thing(type), Created, Distinguishable, Gildable, Votable {
    /** Username of the user that created this model */
    abstract val author: String

    /** The unique base 36 identifier given to this comment by reddit */
    abstract val id: String

    /**
     * The full name of the comment. Essentially equivalent to joining the kind prefix (e.g. "t1" for comments) with
     * [id]. For example, a comment with an ID of "abc123" would have a full name of "t1_abc123"
     */
    abstract val fullName: String

    /** The name of the subreddit (e.g. "pics") */
    abstract val subreddit: String

    /** The subreddit's full name */
    abstract val subredditFullName: String
}
