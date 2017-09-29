package net.dean.jraw.http

import okhttp3.WebSocket
import okhttp3.WebSocketListener

/**
 * Standard interface for sending HTTP requests and opening WebSocket connections.
 *
 * Since this project is tightly bound to OkHttp, this interface exists primarily for testing.
 */
interface NetworkAdapter {
    var userAgent: UserAgent

    /** Executes the HTTP request represended by the given data */
    fun execute(r: HttpRequest): HttpResponse

    /** Attempts to open a connection to a WebSocket */
    fun connect(url: String, listener: WebSocketListener): WebSocket
}
