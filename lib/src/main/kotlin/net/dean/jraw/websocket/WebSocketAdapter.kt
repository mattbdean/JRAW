package net.dean.jraw.websocket

import okhttp3.WebSocket
import okhttp3.WebSocketListener

interface WebSocketAdapter {
    fun open(url: String, listener: WebSocketListener): WebSocket
}
