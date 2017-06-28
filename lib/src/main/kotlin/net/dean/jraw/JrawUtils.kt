package net.dean.jraw

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.dean.jraw.databind.ListingDeserializer
import net.dean.jraw.databind.RedditExceptionStubDeserializer
import net.dean.jraw.databind.RedditObjectDeserializer
import java.io.PrintStream
import java.net.URLEncoder

object JrawUtils {
    @JvmStatic internal fun defaultObjectMapper(): ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        // Use snake case by default because that's what reddit uses
        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)

    @JvmStatic val jackson: ObjectMapper = defaultObjectMapper()
        .registerModule(RedditObjectDeserializer.Module)
        .registerModule(ListingDeserializer.Module)
        .registerModule(RedditExceptionStubDeserializer.Module)

    private val printer = jackson.writerWithDefaultPrettyPrinter()

    @JvmStatic fun parseJson(json: String): JsonNode = jackson.readTree(json)!!

    @JvmStatic fun parseUrlEncoded(str: String): Map<String, String> {
        if (str.isBlank()) return emptyMap()
        // Neat little one-liner. This function splits the query string by '&', then maps each value to a Pair of
        // Strings, converts it to a typed array, and uses the spread operator ('*') to call mapOf()
        return mapOf(*str.split("&").map { val parts = it.split("="); parts[0] to parts[1] }.toTypedArray())
    }

    @JvmStatic fun mapOf(vararg keysAndValues: String): Map<String, String> {
        if (keysAndValues.isEmpty()) return emptyMap()
        if (keysAndValues.size % 2 == 1) throw IllegalArgumentException("Expecting an even amount of keys and values")

        val map: MutableMap<String, String> = HashMap()
        for (i in keysAndValues.indices step 2) {
            map[keysAndValues[i]] = keysAndValues[i + 1]
        }

        return map
    }

    @JvmOverloads
    @JvmStatic
    @Suppress("unused")
    fun prettyPrint(node: JsonNode, where: PrintStream = System.out) {
        where.println(printer.writeValueAsString(node))
    }

    @JvmStatic
    fun urlEncode(str: String): String = URLEncoder.encode(str, "UTF-8")

    fun navigateJson(json: JsonNode, vararg paths: Any): JsonNode {
        var node = json
        for (i in paths.indices) {
            if (paths[i] !is Int && paths[i] !is String)
                throw IllegalArgumentException("paths may be composed of either Strings or Ints, found '${paths[i]}'")

            if (paths[i] is Int && node.has(paths[i] as Int))
                node = node[paths[i] as Int]
            else if (paths[i] is String && node.has(paths[i] as String))
                node = node[paths[i] as String]
            else
                throw IllegalArgumentException("Unexpected JSON structure: cannot find '${paths.slice(0..i).joinToString(" > ")}'")
        }

        return node
    }
}
