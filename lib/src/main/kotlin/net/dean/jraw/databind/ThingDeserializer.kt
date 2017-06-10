package net.dean.jraw.databind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import net.dean.jraw.JrawUtils
import net.dean.jraw.models.Submission
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
    // Keep a reference to an ObjectMapper with the default configuration. This is a little bit of a hack, if someone
    // can find a way to implement the same behavior without creating a new ObjectMapper, I'd be very happy
    private val defaultMapper = JrawUtils.defaultObjectMapper()

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): Thing {
        val mapper = p.codec as ObjectMapper
        val node = mapper.readTree<JsonNode>(p)

        val (dataNode, kind) = verifyRootStructure(node)
        val clazz = registry[kind] ?:
            throw IllegalArgumentException("Unknown kind '$kind'")

        val thing = defaultMapper.treeToValue(dataNode, clazz)
        thing.data = dataNode
        return thing
    }

    companion object {
        @JvmStatic private val registry: Map<String, Class<out Thing>> = mapOf(
            ThingType.SUBMISSION.prefix to Submission::class.java,
            ThingType.SUBREDDIT.prefix to Subreddit::class.java
        )

        /**
         * Asserts the following conditions:
         *
         * 1. `root.get("kind")` is a non-null node and its content is textual
         * 2. `root.get("data")` is a non-null node and represents a JSON object
         *
         * Returns the 'data' node and the value of the 'kind' node.
         */
        internal fun verifyRootStructure(root: JsonNode): Pair<JsonNode, String> {
            val kindNode = root["kind"] ?: throw IllegalArgumentException("Expecting a node 'kind' on $root")
            val kind = kindNode.textValue() ?: "Not a string: $kindNode"

            val data = root["data"] ?: throw IllegalArgumentException("Expecting a node 'data' on $root")
            if (!data.isObject) throw IllegalArgumentException("Expecting 'data' node to be a JSON object: $data")

            return data to kind
        }
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
