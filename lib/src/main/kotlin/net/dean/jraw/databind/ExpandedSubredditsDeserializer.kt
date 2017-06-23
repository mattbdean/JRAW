package net.dean.jraw.databind

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import net.dean.jraw.JrawUtils
import net.dean.jraw.models.Subreddit

class ExpandedSubredditsDeserializer : StdDeserializer<List<String>>(TYPE) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): List<String> {
        val root = p.codec.readTree<JsonNode>(p)
        val list = mutableListOf<String>()
        if (!root.isArray) throw IllegalArgumentException("Expected root node to be array, was ${root.nodeType}")
        for (element in root.elements()) {
            // For some reason some endpoints don't pay attention to the expand_srs argument, so we may or may not have
            // a data node to serialize a Subreddit instance from
            list.add(element["name"].asText())
        }

        return list.filterNotNull()
    }

    companion object {
        private val TYPE = JrawUtils.jackson.typeFactory.constructCollectionLikeType(List::class.java, Subreddit::class.java)
    }
}
