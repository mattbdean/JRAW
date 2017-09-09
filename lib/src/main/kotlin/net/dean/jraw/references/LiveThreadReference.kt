package net.dean.jraw.references

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.models.LiveThread
import net.dean.jraw.models.LiveThreadPatch
import net.dean.jraw.models.LiveUpdate
import net.dean.jraw.pagination.BarebonesPaginator
import net.dean.jraw.websocket.ReadOnlyWebSocketHelper
import okhttp3.WebSocketListener

class LiveThreadReference internal constructor(reddit: RedditClient, id: String) : AbstractReference<String>(reddit, id) {
    @EndpointImplementation(Endpoint.GET_LIVE_THREAD_ABOUT)
    fun about(): LiveThread {
        return reddit.request {
            it.endpoint(Endpoint.GET_LIVE_THREAD_ABOUT, JrawUtils.urlEncode(subject))
        }.deserializeEnveloped()
    }

    /**
     * Creates a Paginator.Builder that will iterate the latest updates from the live thread.
     */
    @EndpointImplementation(Endpoint.GET_LIVE_THREAD)
    fun latestUpdates(): BarebonesPaginator.Builder<LiveUpdate> =
        BarebonesPaginator.Builder.create(reddit, "/live/${JrawUtils.urlEncode(subject)}")

    fun liveUpdates(listener: WebSocketListener): ReadOnlyWebSocketHelper {
        val url = about().websocketUrl ?: throw IllegalStateException("Live thread is not live")
        return ReadOnlyWebSocketHelper(reddit.websocket(url, listener))
    }

    /**
     * Edits this live thread's settings. Note that this endpoint does not work the same way as editing a multireddit.
     * Any values NOT specified in the LiveThreadPatch will be reset to their default values. For example, in order
     * to change only the thread's description, the thread's current resources, title, NSFW status, etc. must be sent
     * as well.
     *
     * The API will throw a tantrum if [LiveThreadPatch.title] is null.
     */
    @EndpointImplementation(Endpoint.POST_LIVE_THREAD_EDIT)
    fun edit(data: LiveThreadPatch) {
        reddit.request {
            it.endpoint(Endpoint.POST_LIVE_THREAD_EDIT, subject)
                .post(data.toRequestMap())
        }
    }

    /** Adds a new update to the live thread */
    @EndpointImplementation(Endpoint.POST_LIVE_THREAD_UPDATE)
    fun postUpdate(text: String) {
        reddit.request {
            it.endpoint(Endpoint.POST_LIVE_THREAD_UPDATE, subject)
                .post(mapOf(
                    "api_type" to "json",
                    "body" to text
                ))
        }
    }

    /** Marks an update as incorrect. Stricken updates appear on the website with the strikethrough text effect. */
    @EndpointImplementation(Endpoint.POST_LIVE_THREAD_STRIKE_UPDATE)
    fun strikeUpdate(id: String) = strikeOrDeleteUpdate(id, false)


    /** Deletes an update and completely removes it from the update history. */
    @EndpointImplementation(Endpoint.POST_LIVE_THREAD_DELETE_UPDATE)
    fun deleteUpdate(id: String) = strikeOrDeleteUpdate(id, true)

    private fun strikeOrDeleteUpdate(fullName: String, delete: Boolean) {
        val endpoint = if (delete) Endpoint.POST_LIVE_THREAD_DELETE_UPDATE else Endpoint.POST_LIVE_THREAD_STRIKE_UPDATE
        reddit.request {
            it.endpoint(endpoint, subject)
                .post(mapOf(
                    "api_type" to "json",
                    "id" to fullName
                ))
        }
    }

    /**
     * Permanently closes this thread off to future updates. Attempting to close an already closed thread will result in
     * a 403 Forbidden.
     */
    @EndpointImplementation(Endpoint.POST_LIVE_THREAD_CLOSE_THREAD)
    fun close() {
        reddit.request {
            it.endpoint(Endpoint.POST_LIVE_THREAD_CLOSE_THREAD, subject)
                .post(mapOf("api_type" to "json"))
        }
    }
}
