package net.dean.jraw.databind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.dean.jraw.models.Subreddit
import net.dean.jraw.models.Thing
import net.dean.jraw.models.ThingType
import kotlin.reflect.full.isSubclassOf

/**
 * This class parses specific data structures from the reddit API into Thing subclasses.
 *
 * This deserializer works by examining the structure of the two root nodes of the provided JSON. Any Thing structure
 * looks something like this:
 *
 * ```json
 * {
 *   "kind": "...",
 *   "data": { ... }
 * }
 * ```
 *
 * where `kind` is a type prefix defined in the [reddit docs](https://www.reddit.com/dev/api/oauth). This class keeps a
 * mapping of all known type prefixes to their corresponding classes, so when this deserializer encounters JSON with a
 * `kind` of "t5", it returns a [Subreddit].
 *
 * @see ThingType
 */
class ThingDeserializer : StdDeserializer<Thing>(Thing::class.java) {
    // Keep a reference to an ObjectMapper with no configuration besides the Kotlin module. This is a little bit of a
    // hack, if someone can find a way to implement the same behavior without creating a new ObjectMapper, I'd be very
    // happy
    private val defaultMapper = ObjectMapper().registerKotlinModule()

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): Thing {
        val mapper = p.codec as ObjectMapper
        val node = mapper.readTree<JsonNode>(p)

        val kind = node.get("kind").asText("<no kind property>")
        val clazz = registry[kind] ?:
            throw IllegalArgumentException("Unknown kind '$kind'")

        val dataNode = node.get("data") ?: throw IllegalArgumentException("no data node")
        val thing = defaultMapper.treeToValue(dataNode, clazz)
        thing.data = dataNode
        return thing
    }

    companion object {
        @JvmStatic private val registry: Map<String, Class<out Thing>> = mapOf(
            ThingType.SUBREDDIT.prefix to Subreddit::class.java
        )
    }

    /**
     * A Jackson module that enables the use of [ThingDeserializer].
     */
    object Module : SimpleModule() {
        init {
            setDeserializerModifier(object: BeanDeserializerModifier() {
                override fun modifyDeserializer(config: DeserializationConfig?, beanDesc: BeanDescription, deserializer: JsonDeserializer<*>): JsonDeserializer<*> {
                    return if (beanDesc.beanClass.kotlin.isSubclassOf(Thing::class)) ThingDeserializer() else deserializer
                }
            })
        }
    }
}
