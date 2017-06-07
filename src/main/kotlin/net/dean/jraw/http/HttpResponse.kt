package net.dean.jraw.http

import com.fasterxml.jackson.databind.JsonNode
import net.dean.jraw.JrawUtils

/**
 * This class forms a bridge from an [AbstractHttpAdapter] implementation to the HTTP library's response class. You
 * shouldn't need to create instances of these yourself unless you're writing your own [AbstractHttpAdapter].
 */
data class HttpResponse(
    /** HTTP status code (200, 404, etc.) */
    val code: Int,
    /** A function that will read the body of a request. Used to lazy initialize the [body] property */
    private val readBody: () -> String,
    /** HTTP request's method ("GET", "POST", etc.) */
    val requestMethod: String,
    /** The URL that the request was targeted at */
    val requestUrl: String
) {
    /** If the status code is 2XX */
    val successful: Boolean = code in 200..299
    /** Lazily initialized response body */
    val body: String by lazy(readBody)
    /** Lazily initialized response body as a Jackson JsonNode */
    val json: JsonNode by lazy { JrawUtils.parseJson(body) }
}
