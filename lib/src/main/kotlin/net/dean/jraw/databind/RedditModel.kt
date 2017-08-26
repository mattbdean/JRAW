package net.dean.jraw.databind

/**
 * Marks a class as having the typical JSON structure returned by the reddit API.
 *
 * See [RedditModelJsonAdapterFactory] for more.
 */
annotation class RedditModel(val kind: String)
