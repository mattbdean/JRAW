package net.dean.jraw.http

import com.squareup.moshi.JsonAdapter
import net.dean.jraw.JrawUtils
import net.dean.jraw.databind.Enveloped
import okhttp3.Request
import okhttp3.Response

/**
 * This class wraps OkHttp's `Response` class to provide some convenience methods and properties
 *
 * @property raw The OkHttp Response object
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

    /**
     * Uses Jackson to deserialize the body of this response to a given type
     *
     * ```kotlin
     * val foo = response.deserialize<Foo>()
     * // OR
     * val foo: Foo = response.deserialize()
     * ```
     */
    inline fun <reified T> deserialize(): T {
        return deserializeWith(JrawUtils.adapter())
    }

    /**
     * Does the same thing as [deserialize], but applies the [Enveloped] annotation.
     */
    inline fun <reified T> deserializeEnveloped(): T {
        return deserializeWith(JrawUtils.adapter(Enveloped::class.java))
    }

    /** Deserializes the response body with the given adapter */
    fun <T> deserializeWith(adapter: JsonAdapter<T>): T {
        return adapter.fromJson(body)!!
    }
}
