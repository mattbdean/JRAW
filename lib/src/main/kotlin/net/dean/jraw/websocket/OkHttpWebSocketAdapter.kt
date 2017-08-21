package net.dean.jraw.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class OkHttpWebSocketAdapter : WebSocketAdapter {
    override fun open(url: String, listener: WebSocketListener): WebSocket {
        val client = OkHttpClient()

        val ws = client.newWebSocket(Request.Builder()
            .get()
            .url(url)
            .build(), listener)

        // Shutdown the ExecutorService so this program can terminate normally
        client.dispatcher().executorService().shutdown()

        return ws
    }
}
