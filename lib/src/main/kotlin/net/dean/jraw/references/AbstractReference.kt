package net.dean.jraw.references

import net.dean.jraw.RedditClient

/**
 * Base class for all References.
 *
 * This class provides an internal constructor and default toString(), equals(), and hashCode() override.
 */
abstract class AbstractReference<out T> internal constructor(
    protected val reddit: RedditClient,
    override val subject: T
) : Reference<T> {
    override fun toString(): String {
        // "SubredditReference(name=pics)"
        return "${this::class.simpleName}(subject=$subject)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        if (subject != (other as AbstractReference<*>).subject) return false

        return true
    }

    override fun hashCode(): Int {
        return subject?.hashCode() ?: 0
    }
}
