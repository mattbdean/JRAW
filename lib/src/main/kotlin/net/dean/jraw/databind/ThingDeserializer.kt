package net.dean.jraw.databind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import net.dean.jraw.models.Subreddit
import net.dean.jraw.models.Thing
import net.dean.jraw.models.ThingType

class ThingDeserializer : StdDeserializer<Thing>(Thing::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): Thing {
        val mapper = p.codec as ObjectMapper
        val node = mapper.readTree<JsonNode>(p)

        val kind = node.get("kind").asText("<no kind property>")
        val type = registry.keys.firstOrNull { it.prefix == kind } ?:
            throw IllegalArgumentException("No registered type for kind '$kind'")
        val clazz = registry[type]

        val dataNode = node.get("data") ?: throw IllegalArgumentException("no data node")
        val thing = mapper.treeToValue(dataNode, clazz)
        thing.data = dataNode
        return thing
    }

    companion object {
        @JvmStatic private val registry: Map<ThingType, Class<out Thing>> = mapOf(
            ThingType.SUBREDDIT to Subreddit::class.java
        )
    }

    class Module : SimpleModule() {
        init {
            setDeserializerModifier(object: BeanDeserializerModifier() {
                override fun modifyDeserializer(config: DeserializationConfig?, beanDesc: BeanDescription, deserializer: JsonDeserializer<*>): JsonDeserializer<*> {
                    return if (beanDesc.beanClass == Thing::class.java) ThingDeserializer() else deserializer
                }
            })
        }
    }
}
