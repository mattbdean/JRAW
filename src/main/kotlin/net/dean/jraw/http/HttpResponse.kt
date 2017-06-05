package net.dean.jraw.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.Response

/**
 * Simple wrapper around an [okhttp3.Response]
 */
class HttpResponse internal constructor(val raw: Response) {
    /** The JSON value of the response body. Lazy initialized. */
    val json: JsonNode by lazy { parse(raw.body()!!.string() ) }

    /** HTTP status code */
    val code = raw.code()

    companion object {
        private val mapper = jacksonObjectMapper()

        private fun parse(json: String): JsonNode {
            return mapper.readTree(json)
        }
    }
}
