package net.dean.jraw.databind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import net.dean.jraw.models.Listing
import net.dean.jraw.models.Thing

/**
 * Enables the deserialization of Listings
 */
class ListingDeserializer private constructor() : JsonDeserializer<Listing<Thing>>(), ContextualDeserializer {
    private lateinit var type: JavaType
    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> {
        val deser = ListingDeserializer()
        deser.type = (if (property != null) property.type else ctxt.contextualType).containedType(0)
        return deser
    }

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Listing<Thing> {
        val mapper = p.codec as ObjectMapper
        val rootNode = mapper.readTree<JsonNode>(p)

        // Since the root structure of the Listing response is exactly the same as the root structure of a Thing
        // response (see RedditObjectDeserializer class documentation), we can reuse RedditObjectDeserializer's verification function
        val (dataNode, kind) = RedditObjectDeserializer.verifyRootStructure(rootNode)
        if (kind != "Listing") throw IllegalArgumentException("Expecting kind to be 'Listing', was '$kind'")

        // Validation
        val childrenNode = dataNode.get("children") ?:
            throw IllegalArgumentException("Expecting a 'children' node, was null")
        if (!childrenNode.isArray)
            throw IllegalArgumentException("Expecting 'children' to be an array node, was $childrenNode")

        childrenNode as ArrayNode
        val children: MutableList<Thing> = ArrayList(childrenNode.size())

        // Map each child node to a Thing
        (0..childrenNode.size() - 1).mapTo(children) { mapper.treeToValue(childrenNode[it], Thing::class.java) }

        return Listing(
            before = dataNode["before"]?.textValue(),
            after = dataNode["after"]?.textValue(),
            children = children
        )
    }

    /**
     * A Jackson module that enables the use of [RedditObjectDeserializer].
     */
    object Module : SimpleModule() {
        init {
            addDeserializer(Listing::class.java, ListingDeserializer())
        }
    }
}
