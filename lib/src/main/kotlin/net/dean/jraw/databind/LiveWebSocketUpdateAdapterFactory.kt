package net.dean.jraw.databind

import com.squareup.moshi.*
import net.dean.jraw.JrawUtils
import net.dean.jraw.models.LiveUpdate
import net.dean.jraw.models.LiveWebSocketUpdate
import java.lang.reflect.Type

/**
 * Responsible for deserializing LiveWebSocketUpdates. These come in this form:
 *
 * ```json
 * {
 *   "type": "<something>",
 *   "payload": <something else>
 * }
 * ```
 *
 * The data encapsulated in `payload` depends entirely on `type`. See [LiveWebSocketUpdate.getType] for more.
 */
class LiveWebSocketUpdateAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type?, annotations: MutableSet<out Annotation>?, moshi: Moshi?): JsonAdapter<*>? {
        val raw = Types.getRawType(type)
        if (!LiveWebSocketUpdate::class.java.isAssignableFrom(raw)) return null

        val delegates: Map<String, JsonAdapter<*>> = registry.mapValues {
            // Make sure to use the @Enveloped annotation when necessary
            if (it.value.isAnnotationPresent(RedditModel::class.java))
                JrawUtils.moshi.adapter(it.value, Enveloped::class.java)
            else
                JrawUtils.moshi.adapter(it.value)
        }
        return Adapter(delegates)
    }

    private class Adapter(private val delegates: Map<String, JsonAdapter<*>>) : JsonAdapter<LiveWebSocketUpdate>() {
        override fun toJson(writer: JsonWriter?, value: LiveWebSocketUpdate?) {
            TODO("not implemented")
        }

        override fun fromJson(reader: JsonReader): LiveWebSocketUpdate? {
            if (reader.peek() != JsonReader.Token.BEGIN_OBJECT)
                throw IllegalArgumentException("Expected an object at ${reader.path}, was ${reader.peek()}")

            val json = reader.readJsonValue() as Map<*, *>
            val type = json["type"] as? String ?:
                throw IllegalArgumentException("Expected a 'type'")

            // Special handling for 'complete' since the data is an empty object
            val payload = if (type == "complete") {
                Any()
            } else {
                val adapter = delegates.getOrElse(type) {
                    throw IllegalArgumentException("No delegate adapter for type '$type'")
                }

                adapter.fromJsonValue(json["payload"])!!
            }

            return LiveWebSocketUpdate.create(type, payload)
        }
    }

    companion object {
        @JvmStatic private val registry: Map<String, Class<*>> = mapOf(
            "update" to LiveUpdate::class.java,
            "activity" to LiveWebSocketUpdate.Activity::class.java,
            "settings" to LiveWebSocketUpdate.Settings::class.java,
            "delete" to String::class.java,
            "strike" to String::class.java,
            "embeds_ready" to LiveWebSocketUpdate.EmbedsReady::class.java
        )
    }
}
