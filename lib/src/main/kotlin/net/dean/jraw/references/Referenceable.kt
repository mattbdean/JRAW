package net.dean.jraw.references

import net.dean.jraw.RedditClient

/**
 * Implemented by model classes to provide a method to easily create a Reference for it.
 */
interface Referenceable<out T : Reference> {
    /** Creates a Reference for this model */
    fun toReference(reddit: RedditClient): T
}
