package net.dean.jraw.databind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import net.dean.jraw.models.LiveUpdate
import net.dean.jraw.models.LiveWebSocketUpdate

class LiveWebSocketUpdateDeserializer : StdDeserializer<LiveWebSocketUpdate>(LiveWebSocketUpdate::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): LiveWebSocketUpdate {
        val mapper = p.codec as ObjectMapper
        val node = mapper.readTree<JsonNode>(p)

        val type = node["type"]?.asText() ?:
            throw IllegalArgumentException("No 'type' node")

        val clazz = registry[type] ?:
            throw IllegalArgumentException("Unknown type '$type'")

        val payloadNode = node["payload"] ?:
            throw IllegalArgumentException("No payload node")

        val payload = mapper.treeToValue(payloadNode, clazz)
        return LiveWebSocketUpdate(type, payload)
    }

    companion object {
        @JvmStatic private val registry: Map<String, Class<*>> = mapOf(
            "update" to LiveUpdate::class.java,
            "activity" to LiveWebSocketUpdate.Activity::class.java,
            "settings" to LiveWebSocketUpdate.Settings::class.java,
            "delete" to String::class.java,
            "strike" to String::class.java,
            "embeds_ready" to LiveWebSocketUpdate.EmbedsReady::class.java,
            "complete" to Unit::class.java
        )
    }

    object Module : SimpleModule() {
        init {
            addDeserializer(LiveWebSocketUpdate::class.java, LiveWebSocketUpdateDeserializer())
        }
    }
}
