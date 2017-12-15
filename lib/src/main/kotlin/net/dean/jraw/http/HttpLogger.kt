package net.dean.jraw.http

import net.dean.jraw.http.HttpLogger.Tag
import java.util.*

/**
 * Standard interface for HTTP request logging.
 *
 * Immediately before a request is sent, use [request] to log it and get a [Tag]. When that request comes back,
 * pass the tag and response to [response] to log it.
 *
 * ```kotlin
 * val tag = myHttpLogger.request(request)
 * val response = myHttpAdapter.execute(request)
 * myHttpLogger.response(tag, response)
 * ```
 */
interface HttpLogger {

    /**
     * Logs an HttpRequest and creates a tag that uniquely identifies this request. This can be done by randomly
     * generating a ID string or using a simple counter. The tag will help the user know which request goes with which
     * response in the case that the each request/response pair do not immediately follow one another.
     *
     * Consider these events:
     *
     *  1. send request A
     *  2. send request B
     *  3. receive response B
     *  4. receive response A
     *
     * In this example, without tags, the developer might think that the first logged request corresponds with the
     * first logged response.
     */
    fun request(r: HttpRequest, sent: Date = Date()): Tag

    /** Logs an HttpResponse that corresponds to the request identified by the given tag */
    fun response(tag: Tag, res: HttpResponse)

    /** @see HttpLogger.request */
    data class Tag(
        /** The unique ID of the request */
        val requestId: Int,

        /** The moment that the request was sent */
        val sent: Date = Date()
    )
}
