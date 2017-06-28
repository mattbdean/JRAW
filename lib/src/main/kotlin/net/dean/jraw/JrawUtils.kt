package net.dean.jraw

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.dean.jraw.databind.ListingDeserializer
import net.dean.jraw.databind.RedditExceptionStubDeserializer
import net.dean.jraw.databind.RedditObjectDeserializer
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * A set of utility methods and properties used throughout the project
 */
object JrawUtils {
    /**
     * Creates a Jackson ObjectMapper with the Kotlin module registered and the property naming strategy sent to
     * snake_case.
     */
    @JvmStatic internal fun defaultObjectMapper(): ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        // Use snake case by default because that's what reddit uses
        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)

    /** The default ObjectMapper instance with all the modules in the `databind` package registered */
    @JvmStatic val jackson: ObjectMapper = defaultObjectMapper()
        .registerModule(RedditObjectDeserializer.Module)
        .registerModule(ListingDeserializer.Module)
        .registerModule(RedditExceptionStubDeserializer.Module)

    /**
     * Parses a URL-encoded string into a map. Most commonly used when parsing a URL's query or a
     * `application/x-www-form-urlencoded` HTTP body.
     *
     * ```kotlin
     * val map = parseUrlEncoded("foo=bar&baz=qux")
     * map.get("foo") // -> "bar"
     * map.get("baz") // -> "qux"
     * ```
     */
    @JvmStatic fun parseUrlEncoded(str: String): Map<String, String> {
        if (str.isBlank()) return emptyMap()
        val map: MutableMap<String, String> = HashMap()
        val pairs = str.split("&")
        for (pair in pairs) {
            val parts = pair.split("=")
            if (parts.size != 2)
                throw IllegalArgumentException("Invalid number of elements, expected 2, got $parts from input segment" +
                    " '$pair'")
            map[urlDecode(parts[0])] = urlDecode(parts[1])
        }

        return map
    }

    /**
     * Constructs a Map<String, String> based on the given keys and values. `keysAndValues[0]` is the key for
     * `keysAndValues[1]`, and so on.
     *
     * ```kotlin
     * val map = mapOf(
     *     "foo", "bar",
     *     "baz", "qux"
     * )
     *
     * map.get("foo") -> "bar"
     * map.get("baz") -> "qux"
     * ```
     */
    @JvmStatic fun mapOf(vararg keysAndValues: String): Map<String, String> {
        if (keysAndValues.isEmpty()) return emptyMap()
        if (keysAndValues.size % 2 == 1) throw IllegalArgumentException("Expecting an even amount of keys and values")

        val map: MutableMap<String, String> = HashMap()
        for (i in keysAndValues.indices step 2) {
            map[keysAndValues[i]] = keysAndValues[i + 1]
        }

        return map
    }

    /**
     * URL-encodes the given String in UTF-8. Equivalent to:
     *
     * ```kotlin
     * URLEncoder.encode(str, "UTF-8")
     * ```
     */
    @JvmStatic fun urlEncode(str: String): String = URLEncoder.encode(str, "UTF-8")

    /**
     * URL-decodes the given String in UTF-8. Equivalent to:
     *
     * ```kotlin
     * URLDecoder.decode(str, "UTF-8")
     * ```
     */
    @JvmStatic fun urlDecode(str: String): String = URLDecoder.decode(str, "UTF-8")

    /**
     * Attempts to navigate a JSON structure with the given paths. Only Strings and Ints are accepted as path elements
     * for object properties and array elements respectively. Take this example:
     *
     * ```json
     * {
     *   "foo": {
     *     "bar": [
     *       {
     *         "baz": "qux"
     *       }
     *     ]
     *   }
     * }
     * ```
     *
     * ```kotlin
     * navigateJson(node, "foo", "bar", 0, "baz").asText() // -> "qux"
     * ```
     *
     * @throws IllegalArgumentException If the entire path can't be resolved
     */
    @Throws(IllegalArgumentException::class)
    @JvmStatic fun navigateJson(json: JsonNode, vararg paths: Any): JsonNode {
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
