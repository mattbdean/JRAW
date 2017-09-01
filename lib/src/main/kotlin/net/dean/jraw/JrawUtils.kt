package net.dean.jraw

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import net.dean.jraw.databind.*
import net.dean.jraw.models.*
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*

/**
 * A set of utility methods and properties used throughout the project
 */
object JrawUtils {
    @JvmField val moshi: Moshi = Moshi.Builder()
        .add(Date::class.java, UnixDateAdapter())
        .add(EnvelopedListAdapterFactory())
        .add(RedditModelAdapterFactory(mapOf(
            KindConstants.COMMENT to Comment::class.java,
            KindConstants.SUBMISSION to Submission::class.java,
            KindConstants.SUBREDDIT to Subreddit::class.java
        )))
        .add(ModelAdapterFactory.create())
        .add(OAuthDataJsonAdapter())
        .add(DistinguishedStatus::class.java, DistinguishedStatusAdapter())
        .add(VoteDirection::class.java, VoteDirectionAdapter())
        .build()

    @JvmStatic inline fun <reified T> adapter(): JsonAdapter<T> {
        return moshi.adapter(T::class.java)
    }

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

//    /**
//     * Attempts to navigate a JSON structure with the given paths. Only Strings and Ints are accepted as path elements
//     * for object properties and array elements respectively. Take this example:
//     *
//     * ```json
//     * {
//     *   "foo": {
//     *     "bar": [
//     *       {
//     *         "baz": "qux"
//     *       }
//     *     ]
//     *   }
//     * }
//     * ```
//     *
//     * ```kotlin
//     * navigateJson(node, "foo", "bar", 0, "baz").asText() // -> "qux"
//     * ```
//     *
//     * @throws IllegalArgumentException If the entire path can't be resolved
//     */
//    @Throws(IllegalArgumentException::class)
//    @JvmStatic fun navigateJson(json: JsonNode, vararg paths: Any): JsonNode {
//        var node = json
//        for (i in paths.indices) {
//            if (paths[i] !is Int && paths[i] !is String)
//                throw IllegalArgumentException("paths may be composed of either Strings or Ints, found '${paths[i]}'")
//
//            if (paths[i] is Int && node.has(paths[i] as Int))
//                node = node[paths[i] as Int]
//            else if (paths[i] is String && node.has(paths[i] as String))
//                node = node[paths[i] as String]
//            else
//                throw IllegalArgumentException("Unexpected JSON structure: cannot find '${paths.slice(0..i).joinToString(" > ")}'")
//        }
//
//        return node
//    }
}
