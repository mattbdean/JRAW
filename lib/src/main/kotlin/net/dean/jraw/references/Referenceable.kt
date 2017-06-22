package net.dean.jraw.references

import net.dean.jraw.RedditClient

interface Referenceable<out T : Reference<*>> {
    fun toReference(reddit: RedditClient): T
}
