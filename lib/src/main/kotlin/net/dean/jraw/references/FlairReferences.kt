package net.dean.jraw.references

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.RedditClient
import net.dean.jraw.models.CurrentFlair
import net.dean.jraw.models.KindConstants
import net.dean.jraw.models.internal.FlairSelector

/**
 * A FlairReference is a reference to the flair for a user or submission on a particular subreddit.
 */
sealed class FlairReference(
    reddit: RedditClient,
    /** The subreddit the flair is located in */
    val subreddit: String,
    /** The name of a user or the full name of a submission */
    val subject: String
) : AbstractReference(reddit) {
    /** Returns a reference to the subreddit where this flair is being observed/modified */
    fun subreddit(): SubredditReference = reddit.subreddit(subreddit)

    /** Removes the current flair for the subject. Equivalent to `updateToTemplate("", null)`. */
    fun remove() = updateToTemplate(templateId = "")

    /**
     * Sets the flair to appear next to the username or submission in question. Pass an empty string for `templateId` or
     * use [remove] to remove the current flair.
     *
     * **User flair**
     *
     * In order to successfully update user flair,
     *
     * 1. User flair must be enabled on the subreddit
     * 2. The authenticated user must be the target user and the subreddit must allow users to assign their own flair.
     *    Alternatively, the authenticated user must be a moderator who can assign flair.
     *
     * **Submission flair**
     *
     * The requirements for a successful submission flair update are very similar to those of a user flair update.
     *
     * 1. Submission flair must be enabled on the subreddit
     * 2. The authenticated user must have posted the submission and the subreddit must allow users to assign their own
     *    submission flair. Alternatively, the authenticated user must be a moderator who can assign flair.
     *
     * @see SubredditReference.userFlairOptions
     */
    @EndpointImplementation(Endpoint.POST_SELECTFLAIR)
    abstract fun updateToTemplate(templateId: String, text: String = "")

    /**
     * Similar to [updateToTemplate] but sets the CSS class directly instead of using a template.
     * The authenticated user must be a moderator of the [subreddit].
     */
    @EndpointImplementation(Endpoint.POST_FLAIR)
    abstract fun updateToCssClass(cssClass: String, text: String = "")

    /** */
    override fun toString(): String {
        return "FlairReference(subreddit='$subreddit', subject='$subject')"
    }

    /** */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FlairReference) return false

        return subreddit == other.subreddit && subject == other.subject
    }

    /** */
    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + subreddit.hashCode()
        result = 31 * result + subject.hashCode()
        return result
    }
}

/** A reference to the flair of a submission in a particular subreddit */
class SubmissionFlairReference internal constructor(reddit: RedditClient, subreddit: String, subject: String) : FlairReference(reddit, subreddit, subject) {

    override fun updateToTemplate(templateId: String, text: String) {
        reddit.request {
            it.endpoint(Endpoint.POST_SELECTFLAIR, subreddit)
                .post(mapOf(
                    "api_type" to "json",
                    "flair_template_id" to templateId,
                    "link" to KindConstants.SUBMISSION + "_" + subject,
                    "text" to text
                ))
        }
    }

    override fun updateToCssClass(cssClass: String, text: String) {
        reddit.request {
            it.endpoint(Endpoint.POST_FLAIR, subreddit)
                .post(mapOf(
                    "api_type" to "json",
                    "css_class" to cssClass,
                    "link" to KindConstants.SUBMISSION + "_" + subject,
                    "text" to text
                ))
        }
    }

    /** Creates a Reference to the subject submission */
    fun submission() = reddit.submission(subject)
}

/**
 * A reference to the flair for one user on a particular subreddit. The user does not necessarily have to be affiliated
 * with the subreddit.
 */
sealed class UserFlairReference(
    reddit: RedditClient,
    subreddit: String,
    username: String
) : FlairReference(reddit, subreddit, username) {
    /** Returns a reference to the target user */
    abstract fun user(): UserReference<*>

    /**
     * Returns the subject user's current flair. If the authenticated user is not the subject user, the authenticated
     * user must be a moderator
     */
    @EndpointImplementation(Endpoint.POST_FLAIRSELECTOR)
    fun current(): CurrentFlair {
        val selector: FlairSelector = reddit.request {
            it.endpoint(Endpoint.POST_FLAIRSELECTOR, subreddit)
                .post(mapOf(
                    "name" to subject
                ))
        }.deserialize()

        return selector.current
    }

    override fun updateToTemplate(templateId: String, text: String) {
        reddit.request {
            it.endpoint(Endpoint.POST_SELECTFLAIR, subreddit)
                .post(mapOf(
                    "api_type" to "json",
                    "flair_template_id" to templateId,
                    "name" to subject,
                    "text" to text
                ))
        }
    }

    override fun updateToCssClass(cssClass: String, text: String) {
        reddit.request {
            it.endpoint(Endpoint.POST_FLAIR, subreddit)
                .post(mapOf(
                    "api_type" to "json",
                    "css_class" to cssClass,
                    "name" to subject,
                    "text" to text
                ))
        }
    }
}

/**
 * A reference to the flair of the currently authenticated user.
 */
class SelfUserFlairReference internal constructor(reddit: RedditClient, subreddit: String) : UserFlairReference(reddit, subreddit, reddit.requireAuthenticatedUser()) {
    /** Enables user flair for the logged in user on this subreddit */
    fun enableFlair() = setFlairEnabled(true)

    /** Disables user flair for the logged in user on this subreddit */
    fun disableFlair() = setFlairEnabled(false)

    /** Enables or disables user flair for the logged in user on this subreddit */
    @EndpointImplementation(Endpoint.POST_SETFLAIRENABLED)
    fun setFlairEnabled(enabled: Boolean) {
        reddit.request {
            it.endpoint(Endpoint.POST_SETFLAIRENABLED, subreddit)
                .post(mapOf(
                    "api_type" to "json",
                    "flair_enabled" to enabled.toString()
                ))
        }
    }

    override fun user(): UserReference<*> = reddit.me()
}

/**
 * A reference to the flair of a user that is NOT the currently authenticated user. This reference cannot interact with
 * the API meaningfully if the currently authenticated user is not a moderator of the given subreddit that can assign
 * flair.
 */
class OtherUserFlairReference internal constructor(reddit: RedditClient, subreddit: String, username: String) :
    UserFlairReference(reddit, subreddit, username) {

    override fun user(): UserReference<*> = reddit.user(subject)
}
