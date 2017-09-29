package net.dean.jraw.websocket

import okhttp3.WebSocket

class ReadOnlyWebSocketHelper(private val ws: WebSocket) {
    /**
     * Closes the connection where `code` is a status code defined by
     * [RFC 6455 section 7.4.1](https://tools.ietf.org/html/rfc6455#section-7.4) or 0.
     *
     * @see CLOSE_CODE_DEFAULT
     */
    fun close(code: Int = CLOSE_CODE_DEFAULT) = ws.close(code, null)

    /** From OkHttp docs: "Immediately and violently release resources held by this web socket" */
    fun forceClose() = ws.cancel()

    companion object {
        const val CLOSE_CODE_DEFAULT = 1000
    }
}
