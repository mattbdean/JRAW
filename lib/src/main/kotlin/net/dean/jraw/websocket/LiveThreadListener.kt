package net.dean.jraw.websocket

//abstract class LiveThreadListener : WebSocketListener() {
//    abstract fun onUpdate(update: LiveWebSocketUpdate)
//
//    override final fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
//        onMessage(webSocket, bytes?.utf8())
//    }
//
//    override final fun onMessage(webSocket: WebSocket?, text: String?) {
//        if (text == null) return
//        val update = JrawUtils.jackson.readValue<LiveWebSocketUpdate>(text)
//        onUpdate(update)
//    }
//}
