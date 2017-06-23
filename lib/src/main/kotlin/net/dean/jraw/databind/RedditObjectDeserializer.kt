package net.dean.jraw.databind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import net.dean.jraw.JrawUtils
import net.dean.jraw.models.*
import kotlin.reflect.full.isSubclassOf

/**
 * This class parses specific data structures from the reddit API into RedditObject subclasses.
 *
 * This deserializer works by examining the structure of the two root nodes of the provided JSON. Any RedditObject
 * structure looks something like this:
 *
 * ```json
 * {
 *   "kind": "...",
 *   "data": { ... }
 * }
 * ```
 *
 * where `kind` is either a type prefix defined in the [reddit docs](https://www.reddit.com/dev/api/oauth) or something
 * like "Listing" or "more" (for Listings and MoreChildren objects respectively). This class keeps a mapping of all
 * known kinds to their corresponding classes, so when this deserializer encounters JSON with a `kind` of "t5", it
 * returns a [Subreddit].
 *
 * @see KindConstants
 */
class RedditObjectDeserializer : StdDeserializer<RedditObject>(RedditObject::class.java) {
    // Keep a reference to an ObjectMapper with the default configuration. This is a little bit of a hack, if someone
    // can find a way to implement the same behavior without creating a new ObjectMapper, I'd be very happy
    private val defaultMapper = JrawUtils.defaultObjectMapper()

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): RedditObject {
        val mapper = p.codec as ObjectMapper
        val node = mapper.readTree<JsonNode>(p)

        val (dataNode, kind) = verifyRootStructure(node)
        val clazz = registry[kind] ?:
            throw IllegalArgumentException("Unknown kind '$kind'")

        return defaultMapper.treeToValue(dataNode, clazz)
    }

    companion object {
        @JvmStatic private val registry: Map<String, Class<out RedditObject>> = mapOf(
            KindConstants.ACCOUNT to Account::class.java,
            KindConstants.TROPHY to Trophy::class.java,
            KindConstants.COMMENT to Comment::class.java,
            KindConstants.SUBMISSION to Submission::class.java,
            KindConstants.SUBREDDIT to Subreddit::class.java,

            KindConstants.MORE_CHILDREN to MoreChildren::class.java,
            KindConstants.MULTIREDDIT to Multireddit::class.java
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
     * A Jackson module that enables the use of [RedditObjectDeserializer].
     */
    object Module : SimpleModule() {
        init {
            setDeserializerModifier(object: BeanDeserializerModifier() {
                override fun modifyDeserializer(config: DeserializationConfig?, beanDesc: BeanDescription, deserializer: JsonDeserializer<*>): JsonDeserializer<*> {
                    return if (beanDesc.beanClass.kotlin.isSubclassOf(RedditObject::class)) RedditObjectDeserializer() else deserializer
                }
            })
        }
    }
}
