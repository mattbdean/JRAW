package net.dean.jraw

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.dean.jraw.databind.ListingDeserializer
import net.dean.jraw.databind.RedditObjectDeserializer

object JrawUtils {
    @JvmStatic internal fun defaultObjectMapper(): ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        // Use snake case by default because that's what reddit uses
        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)

    @JvmStatic val jackson: ObjectMapper = defaultObjectMapper()
        .registerModule(RedditObjectDeserializer.Module)
        .registerModule(ListingDeserializer.Module)

    @JvmStatic fun parseJson(json: String): JsonNode = jackson.readTree(json)!!

    @JvmStatic fun parseUrlEncoded(str: String): Map<String, String> =
        // Neat little one-liner. This function splits the query string by '&', then maps each value to a Pair of
        // Strings, converts it to a typed array, and uses the spread operator ('*') to call mapOf()
        mapOf(*str.split("&").map { val parts = it.split("="); parts[0] to parts[1] }.toTypedArray())
}
