package net.dean.jraw.websocket

import com.fasterxml.jackson.module.kotlin.readValue
import net.dean.jraw.JrawUtils
import net.dean.jraw.models.LiveWebSocketUpdate
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

abstract class LiveThreadListener : WebSocketListener() {
    abstract fun onUpdate(update: LiveWebSocketUpdate)

    override final fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        onMessage(webSocket, bytes?.utf8())
    }

    override final fun onMessage(webSocket: WebSocket?, text: String?) {
        if (text == null) return
        val update = JrawUtils.jackson.readValue<LiveWebSocketUpdate>(text)
        onUpdate(update)
    }
}
