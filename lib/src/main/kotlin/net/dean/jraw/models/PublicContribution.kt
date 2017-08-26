package net.dean.jraw.models

import net.dean.jraw.references.PublicContributionReference
import net.dean.jraw.references.Referenceable
import java.util.*

interface PublicContribution<out T: PublicContributionReference> :
    Created, Distinguishable, Gildable, Identifiable, Votable, Referenceable<T> {

    /** Username of the user that created this model */
    val author: String

    /** The body of the post or comment. Null if the submission is not a self post */
    val body: String?

    /** The date at which the Submission was last edited, or null if it hasn't been. */
    val edited: Date?

    /** If the user has saved this model */
    val isSaved: Boolean

    /** Stickied submissions appear as the the very top when browsing a subreddit by 'hot' */
    val isStickied: Boolean

    /** If reddit is masking this Submission's score */
    val isScoreHidden: Boolean

    /** The name of the subreddit (e.g. "pics") */
    val subreddit: String

    /** The subreddit's full name */
    val subredditFullName: String
}
