package net.dean.jraw.websocket

import net.dean.jraw.JrawUtils
import net.dean.jraw.models.LiveWebSocketUpdate
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

/**
 * Convenience abstraction that assumes each message from the WebSocket is a [LiveWebSocketUpdate].
 */
abstract class LiveThreadListener : WebSocketListener() {
    private val adapter = JrawUtils.adapter<LiveWebSocketUpdate>()

    abstract fun onUpdate(update: LiveWebSocketUpdate)

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        onMessage(webSocket, bytes?.utf8())
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        if (text == null) return
        val update = adapter.fromJson(text)!!
        onUpdate(update)
    }
}
