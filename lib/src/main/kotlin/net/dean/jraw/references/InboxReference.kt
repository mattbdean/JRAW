package net.dean.jraw.references

import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.models.Message
import net.dean.jraw.pagination.BarebonesPaginator

class InboxReference internal constructor(reddit: RedditClient) : AbstractReference<Nothing?>(reddit, null) {
    /**
     * Creates a [BarebonesPaginator.Builder] to view messages.
     *
     * Possible `where` values:
     *
     * - `inbox` — all non-deleted messages
     * - `unread` — all unread messages
     * - `messages` — only private messages
     * - `comments` — only replies to comments the user created
     * - `selfreply` — only top-level replies to posts submitted by the user
     * - `mentions` — only comments that mention this user
     */
    fun iterate(where: String): BarebonesPaginator.Builder<Message> {
        return BarebonesPaginator.Builder(reddit, "/message/${JrawUtils.urlEncode(where)}")
    }
}
