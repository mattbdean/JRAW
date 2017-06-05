package net.dean.jraw.http

import okhttp3.Request
import java.io.IOException

/**
 * Models a HTTP request. Create instances using the Builder model.
 *
 * ```
 * HttpRequest.Builder()
 *     .method("DELETE") // defaults to GET
 *     .url("https://httpbin.org/delete")
 *     .success({ println(it.json) })
 *     .failure({ println("Failed: ${raw.method()} ${raw.url()}) })
 *     .build()
 * ```
 */
class HttpRequest private constructor(
    val url: String,
    val method: String,
    internal val success: (response: HttpResponse) -> Unit,
    internal val failure: (response: HttpResponse) -> Unit,
    internal val internalFailure: (original: Request, e: IOException) -> Unit,
    val sync: Boolean
) {
    private constructor(b: Builder) : this(
        url = b.url!!,
        method = b.method,
        success = b.success,
        failure = b.failure,
        internalFailure = b.internalFailure,
        sync = b.sync
    )

    class Builder {
        internal var method: String = "GET"
        internal var url: String? = null
        internal var success: (response: HttpResponse) -> Unit = {}
        internal var failure: (response: HttpResponse) -> Unit = { res ->
            val req = res.raw.request()
            throw RuntimeException("Unhandled HTTP request with non-success status: ${req.method()} ${req.url()} -> ${res.code}")
        }
        internal var internalFailure: (original: Request, e: IOException) -> Unit = { r, e ->
            throw RuntimeException("HTTP request engine encountered an error: ${r.method()} ${r.url()}", e)
        }
        internal var sync: Boolean = false

        /** Sets the HTTP method (GET, POST, PUT, etc.). Defaults to GET. Case insensitive. */
        fun method(method: String): Builder { this.method = method.trim().toUpperCase(); return this }
        fun url(url: String): Builder { this.url = url; return this }

        /** Sets the function that gets called on a 2XX status code */
        fun success(success: (response: HttpResponse) -> Unit): Builder { this.success = success; return this }

        /** Sets the function that gets called on a non-2XX status code  */
        fun failure(failure: (response: HttpResponse) -> Unit): Builder { this.failure = failure; return this }

        /** Sets the callback to handle an internal HTTP engine failure */
        fun internalFailure(internalFailure: (original: Request, e: IOException) -> Unit): Builder {
            this.internalFailure = internalFailure
            return this
        }

        /** Enable/disable executing the request synchronously. Defaults to false (async) */
        fun sync(sync: Boolean = true): Builder { this.sync = sync; return this }

        fun build() = HttpRequest(this)
    }
}
