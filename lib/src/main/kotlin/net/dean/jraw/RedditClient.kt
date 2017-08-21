package net.dean.jraw

import net.dean.jraw.JrawUtils.jackson
import net.dean.jraw.http.*
import net.dean.jraw.models.Comment
import net.dean.jraw.models.Listing
import net.dean.jraw.models.RedditObject
import net.dean.jraw.models.Submission
import net.dean.jraw.oauth.*
import net.dean.jraw.pagination.BarebonesPaginator
import net.dean.jraw.pagination.DefaultPaginator
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.ratelimit.LeakyBucketRateLimiter
import net.dean.jraw.ratelimit.RateLimiter
import net.dean.jraw.references.*
import net.dean.jraw.websocket.OkHttpWebSocketAdapter
import net.dean.jraw.websocket.WebSocketAdapter
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

/**
 * Specialized class for sending requests to [oauth.reddit.com](https://www.reddit.com/dev/api/oauth).
 *
 * RedditClients cannot be instantiated directly through the public API. See the
 * [OAuthHelper][net.dean.jraw.http.oauth.OAuthHelper] class.
 *
 * This class can also be used to send HTTP requests to other domains, but it is recommended to just use an
 * [HttpAdapter] to do that since all requests are rate limited.
 *
 * By default, all network activity that originates from this class are logged with [logger]. You can provide your own
 * [HttpLogger] implementation or turn it off entirely using [logHttp]
 *
 * Any requests sent by RedditClients are rate-limited on a per-instance basis. That means that it is possible to
 * consume your app's quota of 60 requests per second using more than one RedditClient authenticated under the same
 * OAuth2 app credentials operating independently of each other. For that reason, it is recommended to have only one
 * instance of the RedditClient per application.
 *
 * By default, any request that responds with a server error code (5XX) will be retried up to five times. You can change
 * this by changing [retryLimit].
 */
class RedditClient internal constructor(
    /** How this client will send HTTP requests */
    val http: HttpAdapter,
    initialOAuthData: OAuthData,
    creds: Credentials,
    /** The TokenStore to assign to [AuthManager.tokenStore] */
    tokenStore: TokenStore = NoopTokenStore(),
    /** A non-null value will prevent a request to /api/v1/me to figure out the authenticated username */
    overrideUsername: String? = null
) {
    /** Every HTTP request/response will be logged with this, unless [logHttp] is false */
    var logger: HttpLogger = SimpleHttpLogger()

    /** Whether or not to log HTTP requests */
    var logHttp = true

    /**
     * How many times this client will retry requests that result in 5XX erorr codes. Defaults to 5. Set to a number
     * less than 1 to disable this functionality.
     */
    var retryLimit: Int = DEFAULT_RETRY_LIMIT

    /**
     * How this client will determine when it can send a request.
     *
     * By default, this RateLimiter is a [LeakyBucketRateLimiter] with a capacity of 5 permits and refills them at a
     * rate of 1 token per second.
     */
    var rateLimiter: RateLimiter = LeakyBucketRateLimiter(BURST_LIMIT, RATE_LIMIT, TimeUnit.SECONDS)

    /** If true, any time a request is made, the access token will be renewed if necessary. */
    var autoRenew = true

    /** How this client manages (re)authentication */
    var authManager = AuthManager(http, creds)

    /** The type of OAuth2 app used to authenticate this client */
    val authMethod: AuthMethod = creds.authMethod

    val websocketAdapter: WebSocketAdapter = OkHttpWebSocketAdapter()

    init {
        authManager.currentUsername = if (overrideUsername == AuthManager.USERNAME_USERLESS)
            // We know there's no authenticated user if we're given the special username for userless auth
            null
        else
            // Use overrideUsername if available, otherwise try to fetch the name from the API. We can't use
            // me().about().name since that would require a valid access token (we have to call authManager.update after
            // we have a valid username so TokenStores get the proper name once it gets updated). Instead we directly
            // make the request and parse the response.
            overrideUsername ?: try {
                val json = request(HttpRequest.Builder()
                    .url("https://oauth.reddit.com/api/v1/me")
                    .header("Authorization", "bearer ${initialOAuthData.accessToken}")
                    .build()).json

                if (!json.has("name"))
                    throw IllegalArgumentException("Cannot get name from response")
                json.get("name").asText()
            } catch (e: ApiException) {
                // Delay throwing an exception until `requireAuthenticatedUser()` is called
                null
            }

        authManager.tokenStore = tokenStore
        authManager.update(initialOAuthData)
    }

    /**
     * Creates a [HttpRequest.Builder], setting `secure(true)`, `host("oauth.reddit.com")`, and the Authorization header
     */
    fun requestStub(): HttpRequest.Builder {
        return HttpRequest.Builder()
            .secure(true)
            .host("oauth.reddit.com")
            .header("Authorization", "bearer ${authManager.accessToken}")
    }

    @Throws(NetworkException::class)
    private fun request(r: HttpRequest, retryCount: Int = 0): HttpResponse {
        if (autoRenew && authManager.needsRenewing() && authManager.canRenew())
            authManager.renew()

        // Only ratelimit on the first try
        if (retryCount == 0)
            rateLimiter.acquire()

        val res = if (logHttp) {
            // Log the request and response, returning 'res'
            val tag = logger.request(r)
            val res = http.execute(r)
            logger.response(tag, res)
            res
        } else {
            http.execute(r)
        }

        if (res.code in 500..599 && retryCount < retryLimit) {
            return request(r, retryCount + 1)
        }

        // Try to find any API errors embedded in the document
        val stub = if (res.body == "") null else jackson.treeToValue(res.json, RedditExceptionStub::class.java)

        if (!res.successful) {
            // If there isn't any reddit API errors, throw the NetworkException instead
            stub ?: throw NetworkException(res)
            throw stub.create(NetworkException(res))
        } else {
            // Reddit has some legacy endpoints that return 200 OK even though the JSON contains errors
            if (stub != null)
                throw stub.create(NetworkException(res))
        }

        return res
    }

    /**
     * Attempts to open a WebSocket connection at the given URL.
     */
    fun websocket(url: String, listener: WebSocketListener): WebSocket {
        return websocketAdapter.open(url, listener)
    }

    /**
     * Uses the [HttpAdapter] to execute a synchronous HTTP request
     *
     * ```
     * val response = reddit.request(reddit.requestStub()
     *     .path("/api/v1/me")
     *     .build())
     * ```
     *
     * @throws NetworkException If the response's code is out of the range of 200..299.
     * @throws RedditException if an API error if the response comes back unsuccessful and a typical error structure is
     * detected in the response.
     */
    @Throws(NetworkException::class, RedditException::class)
    fun request(r: HttpRequest): HttpResponse = request(r, retryCount = 0)

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
     * This will execute `POST https://oauth.reddit.com/api/v1/foo` with the headers 'X-Foo: Bar' and
     * 'Authorization: bearer $accessToken' and a form body of `baz=qux`.
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
    @Throws(NetworkException::class, RedditException::class)
    fun request(configure: (stub: HttpRequest.Builder) -> HttpRequest.Builder) = request(configure(requestStub()).build())

    /**
     * Creates a UserReference for the currently logged in user.
     *
     * @throws IllegalStateException If there is no authenticated user
     */
    @Throws(IllegalStateException::class)
    fun me() = SelfUserReference(this)

    /**
     * Creates a UserReference for any user
     *
     * @see me
     */
    fun user(name: String) = OtherUserReference(this, name)

    /** Creates a [Paginator.Builder] to iterate posts on the front page */
    fun frontPage() = DefaultPaginator.Builder<Submission>(this, baseUrl = "", sortingAlsoInPath = true)

    /**
     * Creates a [SubredditReference]
     *
     * Reddit has some special subreddits:
     *
     * - /r/all - posts from every subreddit
     * - /r/popular - includes posts from subreddits that have opted out of /r/all. Guaranteed to not have NSFW content.
     * - /r/mod - submissions from subreddits the logged-in user moderates
     * - /r/friends - submissions from the user's friends
     *
     * Trying to use [SubredditReference.about], [SubredditReference.submit], or the like for these subreddits will
     * likely result in an API-side error.
     */
    fun subreddit(name: String) = SubredditReference(this, name)

    /**
     * Creates a [Paginator.Builder] for more than one subreddit.
     *
     * For example, to fetch 50 posts from /r/pics and /r/funny:
     *
     * ```kotlin
     * val paginator = redditClient.subreddits("pics", "funny").limit(50).build()
     * val posts = paginator.next()
     * ```
     *
     * This works by using a useful little-known trick. You can view multiple subreddits at one time by joining them
     * together with a `+`, like "/r/redditdev+programming+kotlin"
     */
    fun subreddits(first: String, second: String, vararg others: String) =
        SubredditReference(this, arrayOf(first, second, *others).joinToString("+")).posts()

    /**
     * Creates a SubredditReference for a random subreddit. Although this method is decorated with
     * [EndpointImplementation], it does not execute an HTTP request and is not a blocking call. This method is
     * equivalent to
     *
     * ```kotlin
     * reddit.subreddit("random")
     * ```
     *
     * @see SubredditReference.randomSubmission
     */
    @EndpointImplementation(Endpoint.GET_RANDOM, type = MethodType.NON_BLOCKING_CALL)
    fun randomSubreddit() = subreddit("random")

    /**
     * Creates a SubmissionReference. Note that `id` is NOT a full name (like `t3_6afe8u`), but rather an ID
     * (like `6afe8u`)
     */
    fun submission(id: String) = SubmissionReference(this, id)

    /**
     * Creates a BarebonesPaginator.Builder that will iterate over the latest comments from all subreddits when built.
     * This Paginator will be especially useful when used with the [Paginator.restart] method.
     */
    fun comments() = BarebonesPaginator.Builder<Comment>(this, "/comments")

    /**
     * Returns the name of the logged-in user
     *
     * @throws IllegalStateException If there is no logged-in user
     */
    fun requireAuthenticatedUser(): String {
        if (authMethod.isUserless)
            throw IllegalStateException("Expected the RedditClient to have an active user, was authenticated with " +
                authMethod)
        return authManager.currentUsername ?: throw IllegalStateException("Expected an authenticated user")
    }

    /**
     * varargs version of `lookup` provided for convenience.
     */
    fun lookup(vararg fullNames: String) = lookup(listOf(*fullNames))

    /**
     * Attempts to find information for the given full names. Only the full names of submissions, comments, and
     * subreddits are accepted.
     */
    @EndpointImplementation(Endpoint.GET_INFO)
    fun lookup(fullNames: List<String>): Listing<RedditObject> {
        if (fullNames.isEmpty()) return Listing()

        return request {
            it.endpoint(Endpoint.GET_INFO)
                .query(mapOf("id" to fullNames.joinToString(",")))
        }.deserialize()
    }

    fun liveThread(id: String) = LiveThreadReference(this, id)

    override fun toString(): String {
        return "RedditClient(username=${authManager.currentUsername()})"
    }

    companion object {
        /** Amount of requests per second reddit allows for OAuth2 apps (equal to 1) */
        const val RATE_LIMIT = 1L
        private const val BURST_LIMIT = 5L
        private const val DEFAULT_RETRY_LIMIT = 5
    }
}
