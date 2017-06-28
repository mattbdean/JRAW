package net.dean.jraw.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.readValue
import net.dean.jraw.JrawUtils
import okhttp3.Request
import okhttp3.Response

/**
 * This class wraps OkHttp's `Response` class to provide some convenience methods and properties
 */
data class HttpResponse(val raw: Response) {
    /** The request that was responsible for creating this response */
    val request: Request = raw.request()

    /** HTTP status code (200, 404, etc.) */
    val code: Int = raw.code()

    /** If the status code is in the range 200..299 */
    val successful: Boolean = raw.isSuccessful

    /** Lazily initialized response body, or an empty string if there was none */
    val body: String by lazy { raw.body()?.string() ?: "" }

    /** Lazily initialized response body as a Jackson JsonNode */
    val json: JsonNode by lazy { JrawUtils.jackson.readTree(body) }

    /**
     * Uses Jackson to deserialize the body of this response to a given type
     *
     * ```kotlin
     * val foo = response.deserialize<Foo>()
     * // OR
     * val foo: Foo = response.deserialize()
     * ```
     */
    inline fun <reified  T : Any> deserialize(): T {
        if (body.isEmpty()) throw IllegalStateException("Cannot deserialize a response with an empty body")
        return JrawUtils.jackson.readValue<T>(body)
    }
}
