package net.dean.jraw.references

/**
 * A Reference is a cheap to create, conceptual link to a reddit resource.
 *
 * References have a subject that describes the name or ID of the resource they are referencing. For example, a
 * [SubredditReference] created for /r/pics would have the subject "pics".
 *
 * References are used to provide a fluent interface for describing the actions a user can take on an API. For example,
 * getting information about a subreddit" would look like this:
 *
 * ```kotlin
 * val pics: Subreddit = redditClient.subreddit("pics").about()
 * ```
 *
 * All References **must** have internal constructors and be instantiated by [net.dean.jraw.RedditClient], other
 * References, or models that inherit [Referenceable].
 */
interface Reference<out T> {
    /**
     * If the reddit API guarantees the name of a resource is unique, then this property is that name. Otherwise, it is
     * the unique ID provided by the API.
     */
    val subject: T
}
