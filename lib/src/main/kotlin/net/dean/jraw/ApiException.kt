package net.dean.jraw

import com.fasterxml.jackson.databind.JsonNode

/**
 * Exception subclass for reddit's many errors
 *
 * See [here](https://github.com/reddit/reddit/blob/master/r2/r2/lib/errors.py) for a full list.
 */
class ApiException(val code: String, val explanation: String) : RuntimeException("API returned error: $code ($explanation)") {
    companion object {
        @JvmStatic fun from(node: JsonNode): ApiException {
            if (!node.isArray) throw IllegalArgumentException("JSON node must be an array")
            if (node.size() < 2) throw IllegalArgumentException("Expected at least 2 elements")
            return ApiException(node[0].asText(), node[1].asText())
        }
    }
}
