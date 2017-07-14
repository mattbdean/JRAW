package net.dean.jraw.references

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
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
    @EndpointImplementation(Endpoint.GET_MESSAGE_WHERE)
    fun iterate(where: String): BarebonesPaginator.Builder<Message> {
        return BarebonesPaginator.Builder(reddit, "/message/${JrawUtils.urlEncode(where)}")
    }

    fun compose(dest: String, subject: String, body: String) = compose(null, dest, subject, body)

    @EndpointImplementation(Endpoint.POST_COMPOSE)
    fun compose(fromSubreddit: String?, dest: String, subject: String, body: String) {
        val args = mutableMapOf(
            "api_type" to "json",
            "subject" to subject,
            "text" to body,
            "to" to dest
        )
        if (fromSubreddit != null)
            args["from_sr"] = fromSubreddit

        reddit.request {
            it.endpoint(Endpoint.POST_COMPOSE)
                .post(args)
        }
    }

    @EndpointImplementation(Endpoint.POST_READ_MESSAGE, Endpoint.POST_UNREAD_MESSAGE)
    fun markRead(read: Boolean, firstFullName: String, vararg otherFullNames: String) {
        reddit.request {
            it.endpoint(if (read) Endpoint.POST_READ_MESSAGE else Endpoint.POST_UNREAD_MESSAGE)
                .post(mapOf("id" to listOf(firstFullName, *otherFullNames).joinToString(",")))
        }
    }

    @EndpointImplementation(Endpoint.POST_READ_ALL_MESSAGES)
    fun markAllRead() {
        reddit.request {
            it.endpoint(Endpoint.POST_READ_ALL_MESSAGES)
                .post(mapOf())
        }
    }

    /**
     * Removes a message from the user's inbox. Note that the message will remain in the inboxes of the other
     * participants and on reddit servers. Deleted messages will not appear in the user's inbox.
     *
     * @param fullName The full name of a message, something like `t4_xxxxx`
     *
     * @see Message.fullName
     */
    @EndpointImplementation(Endpoint.POST_DEL_MSG)
    fun delete(fullName: String) {
        reddit.request {
            it.endpoint(Endpoint.POST_DEL_MSG)
                .post(mapOf("id" to fullName))
        }
    }
}
