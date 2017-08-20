package net.dean.jraw.databind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import net.dean.jraw.ApiExceptionStub
import net.dean.jraw.JrawUtils
import net.dean.jraw.RateLimitExceptionStub
import net.dean.jraw.RedditExceptionStub

/**
 * Deserializes [RedditExceptionStub] and its derivatives
 */
internal class RedditExceptionStubDeserializer : StdDeserializer<RedditExceptionStub<*>?>(RedditExceptionStub::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): RedditExceptionStub<*>? {
        val root = p.codec.readTree<JsonNode>(p)

        if (root.isArray) {
            // Array-based ApiException format. Used only by the oldest of endpoints
            if (root.size() < 2) throw IllegalArgumentException("Expected at least 2 elements")
            return ApiExceptionStub(root[0].asText(), root[1].asText(), listOf())
        } else if (root.isObject) {
            if (root.has("json")) {
                val json = root["json"]

                // Ratelimit errors also specify data to construct an ApiException, but RatelimitException is more
                // specific, so prefer that
                if (json.has("ratelimit"))
                    return RateLimitExceptionStub(JrawUtils.navigateJson(root, "json", "ratelimit").asDouble(-1.0))
                if (json.has("errors") && json["errors"].isArray && json["errors"].has(0))
                    return fromArray(json["errors"][0])
            }

            // General API errors (e.g. trying to edit a multireddit that doesn't belong to you)
            if (root.has("explanation") && root.has("reason")) {
                val fields: List<String> = if (root.has("fields")) {
                    val node = root.get("fields")
                    if (!node.isArray) throw IllegalArgumentException("Expecting fields node to be an array")
                    val list = mutableListOf<String>()
                    for (element in node.elements())
                        list.add(element.asText())
                    list
                } else {
                    listOf()
                }
                return ApiExceptionStub(root["reason"].asText(), root["explanation"].asText(), fields)
            }

            // Commonly used in 403 Forbidden responses
            if (root.has("message") && root.has("error")) {
                return ApiExceptionStub(root["error"].asText(), root["message"].asText(), listOf())
            }
        }

        return null
    }

    companion object {
        private fun fromArray(node: JsonNode): RedditExceptionStub<*> {
            if (node.size() < 2) throw IllegalArgumentException("Expected at least 2 elements")
            val params = if (node.size() > 2) listOf(node[2].asText()) else listOf()
            return ApiExceptionStub(node[0].asText(), node[1].asText(), params)
        }
    }

    object Module : SimpleModule() {
        init {
            addDeserializer(RedditExceptionStub::class.java, RedditExceptionStubDeserializer())
        }
    }
}
