package net.dean.jraw.references

import net.dean.jraw.RedditClient

/**
 * Base class for all References.
 *
 * This class provides an internal constructor and default toString(), equals(), and hashCode() override.
 */
abstract class AbstractReference internal constructor(
    protected val reddit: RedditClient
) : Reference {
    override fun toString(): String {
        return "AbstractReference(reddit=$reddit)"
    }
}
