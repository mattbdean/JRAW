package net.dean.jraw.databind

/**
 * Class that models a typical JSON structure returned by the reddit API.
 */
internal data class RedditModelEnvelope<out T>(
    /**
     * Describes the type of data encapsulated in [data]. For example, "t1" for comments, "t2" for accounts. See
     * [net.dean.jraw.models.KindConstants] for more.
     */
    val kind: String,
    val data: T
)
