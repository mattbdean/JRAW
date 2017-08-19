package net.dean.jraw.models

import net.dean.jraw.references.PublicContributionReference
import net.dean.jraw.references.Referenceable

/**
 * Represents any model that can be created for multiple other reddit users to see. In practice, this is the superclass
 * for [Comment] and [Submission].
 */
abstract class PublicContribution<out T : PublicContributionReference>(kind: String) :
    RedditObject(kind), Created, Distinguishable, Gildable, Identifiable, Votable, Referenceable<T> {

    /** Username of the user that created this model */
    abstract val author: String

    /** The body of the post or comment. Null if the submission is not a self post */
    abstract val body: String?

    /** If the user has saved this model */
    abstract val saved: Boolean

    /** The name of the subreddit (e.g. "pics") */
    abstract val subreddit: String

    /** The subreddit's full name */
    abstract val subredditFullName: String
}
