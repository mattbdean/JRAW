package net.dean.jraw

import com.fasterxml.jackson.databind.JsonNode
import net.dean.jraw.http.*
import net.dean.jraw.http.oauth.AuthenticationMethod
import net.dean.jraw.http.oauth.OAuthData
import net.dean.jraw.models.Subreddit

/**
 * Specialized class for sending requests to [oauth.reddit.com](https://www.reddit.com/dev/api/oauth).
 *
 * By default, HTTP requests and responses created through this class are logged with [logger]. You can provide your own
 * [HttpLogger] implementation or turn it off entirely using [logHttp]
 */
class RedditClient(
    val http: HttpAdapter,
    val authMethod: AuthenticationMethod,
    val oauthData: OAuthData
) {
    var logger: HttpLogger = SimpleHttpLogger()
    var logHttp = true

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
     * val response = reddit.request(reddit.requestStub()
     *     .path("/api/v1/me")
     *     .build())
     * ```
     */
    fun request(r: HttpRequest): HttpResponse {
        return if (logHttp) {
            // Log the request and response, returning 'res'
            val tag = logger.request(r)
            val res = http.execute(r)
            logger.response(tag, res)
            res
        } else {
            http.execute(r)
        }
    }

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

    @EndpointImplementation(Endpoint.GET_ME)
    fun me(): JsonNode = request { it.path("/api/v1/me") }.json

    @EndpointImplementation(Endpoint.GET_SUBREDDIT_ABOUT)
    fun subreddit(name: String): Subreddit = request { it.path("/r/$name/about") }.deserialize()

    // TODO: This endpoint can also return a random submission: /r/{subreddit}/random
    @EndpointImplementation(Endpoint.GET_RANDOM)
    fun randomSubreddit(): Subreddit = request { it.path("/r/random/about") }.deserialize()
}
