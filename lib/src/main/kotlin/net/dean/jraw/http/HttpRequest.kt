package net.dean.jraw.http

import net.dean.jraw.Endpoint
import net.dean.jraw.JrawUtils
import net.dean.jraw.http.HttpRequest.Builder
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.RequestBody
import java.net.URLEncoder
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * An immutable representation of an HTTP request. Instances are created using the [builder pattern][Builder]
 *
 * ```
 * val request = HttpRequest.Builder()
 *     .delete() // defaults to GET
 *     .url("https://httpbin.org/delete")
 *     .header("X-Foo", "Bar")
 *     .build()
 *
 * val response = httpClient.execute(request)
 * println(response.body)
 * ```
 */
class HttpRequest internal constructor(
    /** A fully-qualified URL */
    val url: String,

    /** Any additional/custom headers to send */
    val headers: Headers,

    /** HTTP method (GET, POST, etc.) */
    val method: String,

    /** Request payload */
    val body: RequestBody?,

    /** If present, the request will be sent using HTTP Basic Authentication */
    internal val basicAuth: BasicAuthData?,

    /** If being sent with a RedditClient, will request that reddit does not encode special HTML characters */
    val rawJson: Boolean = true
) {
    private constructor(b: Builder) : this(
        url = buildUrl(b),
        headers = b.headers.build(),
        method = b.method,
        body = b.body,
        basicAuth = b.basicAuth,
        rawJson = b.rawJson
    )



    /** */
    companion object {
        /** This Pattern will match a URI parameter. For example, /api/{param1}/{param2}  */
        // The second escape (for the '}') is required because Oracle's Pattern.compile implementation is different from
        // Android's. Not having this escape will cause crashes on Android apps.
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
                    if (it.hasNext())
                        append('&')
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
            for (arg in positionalArgs.map { JrawUtils.urlEncode(it) }) {
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

    /** Builder pattern for HttpRequest */
    class Builder {
        internal var method: String = "GET"
        internal var headers: Headers.Builder = Headers.Builder()
        internal var body: RequestBody? = null

        // URL-related variables
        internal var url: String? = null
        internal var secure = true
        internal var host = ""
        internal var path = ""
        internal var pathParams = listOf<String>()
        internal var query: Map<String, String> = mapOf()
        internal var basicAuth: BasicAuthData? = null

        internal var rawJson = true

        /** Sets the HTTP method (GET, POST, PUT, etc.). Defaults to GET. Case insensitive. */
        fun method(method: String, body: RequestBody? = null): Builder {
            this.method = method.trim().toUpperCase()
            this.body = body
            return this
        }

        /** Sets the HTTP method. The given map will be sent as `application/x-www-form-urlencoded` data. */
        fun method(method: String, body: Map<String, String>): Builder {
            this.method = method
            val formBodyBuilder = FormBody.Builder()
            for ((k, v) in body)
                formBodyBuilder.add(k, v)
            this.body = formBodyBuilder.build()
            return this
        }

        /** Send a GET */
        fun get() = method("GET")

        /** Send a DELETE */
        fun delete() = method("DELETE")

        /** Send a POST with the given form data */
        fun post(body: Map<String, String>) = method("POST", body)

        /** Send a POST with the given request body */
        fun post(body: RequestBody) = method("POST", body)

        /** Send a PUT with the given form data */
        fun put(body: Map<String, String>) = method("PUT", body)

        /** Send a PUT with the given request body */
        fun put(body: RequestBody) = method("PUT", body)

        /** Send a PATCH with the given form data */
        fun patch(body: Map<String, String>) = method("PATCH", body)

        /** Send a PATCH with the given request body */
        fun patch(body: RequestBody) = method("PATCH", body)

        /**
         * Set the URL. If specified, this will be used over any other property that specifies part of a URL
         * (e.g. [path] or [query])
         */
        fun url(url: String): Builder { this.url = url; return this }

        /** Sets a header */
        fun header(key: String, value: String): Builder { this.headers.set(key, value); return this }

        /** Convenience function for `basicAuth(BasicAuthData)` */
        fun basicAuth(creds: Pair<String, String>) = basicAuth(BasicAuthData(creds.first, creds.second))

        /** Executes the request with HTTP basic authentication */
        fun basicAuth(creds: BasicAuthData): Builder { this.basicAuth = creds; return this }

        /** Enables/disables HTTPS (enabled by default) */
        fun secure(flag: Boolean = true): Builder { this.secure = flag; return this }
        /** Sets the hostname (e.g. "google.com" or "oauth.reddit.com") */
        fun host(host: String): Builder { this.host = host; return this }

        /**
         * Sets this Builder's path to the given Endpoint's path and changes the host to `oauth.reddit.com`. This method
         * does not change the HTTP method.
         */
        fun endpoint(e: Endpoint, vararg pathParams: String?): Builder {
            this.host = "oauth.reddit.com"
            return if (e.path.startsWith(Endpoint.Constant.OPTIONAL_SUBREDDIT)) {
                if (pathParams.isEmpty())
                    throw IllegalArgumentException("Expected at least one path argument for endpoint with optional subreddit")
                val optionalPath = pathParams[0]?.let { "/r/${JrawUtils.urlEncode(it)}" } ?: ""
                val requiredPath = e.path.substringAfter(Endpoint.Constant.OPTIONAL_SUBREDDIT)
                val requiredPathParams = pathParams.drop(1).toTypedArray().requireNoNulls()
                path(optionalPath + requiredPath, *requiredPathParams)
            } else {
                path(e.path, *pathParams.requireNoNulls())
            }
        }

        /**
         * Sets the URL's path. For example, "/mattbdean/JRAW." Positional path parameters are supported, so if
         * `path` was "/api/{resource}" and `params` was a one-element array consisting of "foo", then the resulting
         * path would be "/api/foo."
         *
         * @param path The path. If null, "/" will be used.
         * @param pathParams Optional positional path parameters. These will be automatically URL-encoded.
         * @return This Builder
         */
        fun path(path: String, vararg pathParams: String): Builder {
            this.path = path
            this.pathParams = listOf(*pathParams)
            return this
        }

        /** Sets the query section of the URL. */
        fun query(query: Map<String, String>): Builder { this.query = query; return this }

        /** When used with a RedditClient, requests that reddit not escape HTML entities. True by default. */
        fun rawJson(rawJson: Boolean): Builder { this.rawJson = rawJson; return this }

        /** Creates an HttpRequest from this builder */
        fun build() = HttpRequest(this)
    }

    override fun toString(): String {
        return "HttpRequest(url='$url', headers=$headers, method='$method', body=$body, basicAuth=$basicAuth, rawJson=$rawJson)"
    }
}
