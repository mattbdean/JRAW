package net.dean.jraw.http

import okhttp3.FormBody
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.net.URLEncoder
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern



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
    val body: RequestBody?,
    internal val success: (response: HttpResponse) -> Unit,
    internal val failure: (response: HttpResponse) -> Unit,
    internal val internalFailure: (original: Request, e: IOException) -> Unit,
    val sync: Boolean
) {
    private constructor(b: Builder) : this(
        url = buildUrl(b),
        method = b.method,
        body = b.body,
        success = b.success,
        failure = b.failure,
        internalFailure = b.internalFailure,
        sync = b.sync
    )

    companion object {
        /** This Pattern will match a URI parameter. For example, /api/{param1}/{param2}  */
        private val PATH_PARAM_PATTERN = Pattern.compile("\\{(.*?)\\}")

        private fun buildUrl(b: Builder): String {
            if (b.url != null) return b.url!!

            if (b.host.trim() == "") throw IllegalArgumentException("Expecting a non-empty host")
            var path = b.path.trim()
            if (!path.startsWith("/")) path = "/" + path
            if (!b.pathParams.isEmpty()) path = substitutePathParameters(b.path, b.pathParams)

            return "http${if (b.secure) "s" else ""}://${b.host}$path${buildQuery(b.query)}"
        }

        private fun buildQuery(q: Map<String, String>): String {
            if (q.isEmpty()) return ""
            val sb = StringBuilder("?")
            with (sb) {
                val it = q.entries.iterator()
                while (it.hasNext()) {
                    val (k, v) = it.next()
                    append(URLEncoder.encode(k, "UTF-8"))
                    append('=')
                    append(URLEncoder.encode(v, "UTF-8"))
                }
            }

            return sb.toString()
        }

        private fun substitutePathParameters(path: String, positionalArgs: List<String>): String {
            val pathParams = parsePathParams(path)
            if (pathParams.size != positionalArgs.size) {
                // Different amount of parameters
                throw IllegalArgumentException(
                    "URL parameter size mismatch. Expecting ${pathParams.size}, got ${positionalArgs.size}")
            }

            var updatedUri = path
            var m: Matcher? = null
            for (arg in positionalArgs) {
                if (m == null) {
                    // Create on first use
                    m = PATH_PARAM_PATTERN.matcher(updatedUri)
                } else {
                    // Reuse the Matcher
                    m.reset(updatedUri)
                }
                updatedUri = m!!.replaceFirst(arg)
            }

            return updatedUri
        }

        /** Finds all parameters in the given path  */
        private fun parsePathParams(path: String): List<String> {
            val params = ArrayList<String>()
            val matcher = PATH_PARAM_PATTERN.matcher(path)
            while (matcher.find()) {
                params.add(matcher.group())
            }

            return params
        }
    }

    class Builder {
        internal var method: String = "GET"
        internal var body: RequestBody? = null
        internal var success: (response: HttpResponse) -> Unit = {}
        internal var failure: (response: HttpResponse) -> Unit = { res ->
            val req = res.raw.request()
            throw RuntimeException("Unhandled HTTP request with non-success status: ${req.method()} ${req.url()} -> ${res.code}")
        }
        internal var internalFailure: (original: Request, e: IOException) -> Unit = { r, e ->
            throw RuntimeException("HTTP request engine encountered an error: ${r.method()} ${r.url()}", e)
        }
        internal var sync: Boolean = false

        // URL-related variables
        internal var url: String? = null
        internal var secure = true
        internal var host = ""
        internal var path = ""
        internal var pathParams = listOf<String>()
        internal var query: Map<String, String> = mapOf()

        /** Sets the HTTP method (GET, POST, PUT, etc.). Defaults to GET. Case insensitive. */
        fun method(method: String, body: RequestBody? = null): Builder {
            this.method = method.trim().toUpperCase()
            this.body = body
            return this
        }
        fun method(method: String, body: Map<String, String>): Builder {
            this.method = method
            val formBodyBuilder = FormBody.Builder()
            for ((k, v) in body)
                formBodyBuilder.addEncoded(k, v)
            this.body = formBodyBuilder.build()
            return this
        }

        fun get() = method("GET")
        fun delete() = method("DELETE")
        fun post(body: Map<String, String>) = method("POST", body)
        fun post(body: RequestBody) = method("POST", body)
        fun put(body: Map<String, String>) = method("PUT", body)
        fun put(body: RequestBody) = method("PUT", body)

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

        /**
         * Enable/disable executing the request synchronously. Defaults to false (async). Calling this method with no
         * arguments sets this request to execute synchronously.
         */
        fun sync(sync: Boolean = true): Builder { this.sync = sync; return this }

        /** Enables/disables HTTPS (enabled by default) */
        fun secure(flag: Boolean = true): Builder { this.secure = flag; return this }
        /** Sets the hostname (e.g. "google.com" or "oauth.reddit.com") */
        fun host(host: String): Builder { this.host = host; return this }

        /**
         * Sets the URL's path. For example, "/thatJavaNerd/JRAW." Positional path parameters are supported, so if
         * {@code path} was "/api/{resource}" and {@code params} was a one-element array consisting of "foo", then the
         * resulting path would be "/api/foo."
         *
         * @param path The path. If null, "/" will be used.
         * @param pathParams Optional positional path parameters
         * @return This Builder
         */
        fun path(path: String, vararg pathParams: String): Builder {
            this.path = path
            this.pathParams = listOf(*pathParams)
            return this
        }

        fun query(query: Map<String, String>): Builder { this.query = query; return this }

        fun build() = HttpRequest(this)
    }
}
