package net.dean.jraw.models

/**
 * Common interface for models that can show the status of the user that created them.
 *
 * @see DistinguishedStatus
 */
interface Distinguishable {
    /**
     * The status of the person who created this Submission. Note that subreddit moderators, reddit admins, or other
     * "distinguished" users still post comments or submissions that look like any everybody else's. These users have to
     * explicitly mark their comments or submissions as "distinguished" in order for them to appear that way one the
     * website and API.
     */
    val distinguished: DistinguishedStatus
}
