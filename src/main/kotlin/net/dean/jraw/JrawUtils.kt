package net.dean.jraw

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object JrawUtils {
    @JvmStatic val jackson = jacksonObjectMapper()
    @JvmStatic fun parseJson(json: String): JsonNode = jackson.readTree(json)!!
}
