package net.dean.jraw

import com.fasterxml.jackson.databind.JsonNode
import net.dean.jraw.http.HttpAdapter
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.oauth.AuthenticationMethod
import net.dean.jraw.http.oauth.OAuthData

class RedditClient(
    val http: HttpAdapter,
    val authMethod: AuthenticationMethod,
    val oauthData: OAuthData
) {
    /**
     * Creates a [HttpRequest.Builder], setting `secure(true)`, `host("oauth.reddit.com")`, and the Authorization header
     */
    fun requestStub() = HttpRequest.Builder()
        .secure(true)
        .host("oauth.reddit.com")
        .header("Authorization", "bearer ${oauthData.accessToken}")

    /**
     * Uses the [HttpAdapter] to execute a synchronous HTTP request and returns its JSON value
     *
     * ```
     * val json = reddit.request(reddit.requestStub()
     *     .path("/api/v1/me")
     *     .build())
     * ```
     */
    fun request(r: HttpRequest): JsonNode =
        http.execute(r).json

    /**
     * Adds a little syntactic sugar to the vanilla `request` method.
     *
     * The [configure] function will be passed the return value of [requestStub], and that [HttpRequest.Builder] will be
     * built and send to `request()`. While this may seem a little complex, it's probably easier to understand through
     * an example:
     *
     * ```
     * val json = reddit.request {
     *     it.path("/api/v1/foo")
     *         .header("X-Foo", "Bar")
     *         .post(mapOf(
     *             "baz" to "qux"
     *         ))
     * }
     * ```
     *
     * This will execute `POST https://oauth.reddit.com/api/v1/foo` with the header 'X-Foo: Bar' and a form body of
     * `baz=qux`.
     *
     * For reference, this same request can be executed like this:
     *
     * ```
     * val json = reddit.request(reddit.requestStub()
     *     .path("/api/v1/me")
     *     .header("X-Foo", "Bar")
     *     .post(mapOf(
     *         "baz" to "qux"
     *     )).build())
     * ```
     *
     * @see requestStub
     */
    fun request(configure: (stub: HttpRequest.Builder) -> HttpRequest.Builder) = request(configure(requestStub()).build())

    fun me(): JsonNode = request { it.path("/api/v1/me") }
}
