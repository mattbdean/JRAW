package net.dean.jraw

import com.squareup.moshi.Types
import net.dean.jraw.databind.Enveloped
import net.dean.jraw.http.*
import net.dean.jraw.models.*
import net.dean.jraw.models.internal.RedditExceptionStub
import net.dean.jraw.oauth.*
import net.dean.jraw.pagination.BarebonesPaginator
import net.dean.jraw.pagination.DefaultPaginator
import net.dean.jraw.ratelimit.LeakyBucketRateLimiter
import net.dean.jraw.ratelimit.RateLimiter
import net.dean.jraw.references.*
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

/**
 * Specialized class for sending requests to [oauth.reddit.com](https://www.reddit.com/dev/api/oauth).
 *
 * RedditClients cannot be instantiated directly through the public API. See the
 * [OAuthHelper][net.dean.jraw.oauth.OAuthHelper] class.
 *
 * This class can also be used to send HTTP requests to other domains, but it is recommended to just use an
 * [NetworkAdapter] to do that since all requests made using this class are rate limited using [rateLimiter].
 *
 * By default, all network activity that originates from this class are logged with [logger]. You can provide your own
 * [HttpLogger] implementation or turn it off entirely by setting [logHttp] to `false`.
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
    val http: NetworkAdapter,
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

    internal var forceRenew = false

    /** How this client manages (re)authentication */
    var authManager = AuthManager(http, creds)

    /** The type of OAuth2 app used to authenticate this client */
    val authMethod: AuthMethod = creds.authMethod

    internal var loggedOut: Boolean = false

    init {
        // Use overrideUsername if available, otherwise try to fetch the name from the API. We can't use
        // me().about().name since that would require a valid access token (we have to call authManager.update after
        // we have a valid username so TokenStores get the proper name once it gets updated). Instead we directly
        // make the request and parse the response.
        authManager.currentUsername = overrideUsername ?: try {
                val me = request(HttpRequest.Builder()
                    .url("https://oauth.reddit.com/api/v1/me")
                    .header("Authorization", "bearer ${initialOAuthData.accessToken}")
                    .build()).deserialize<Map<*, *>>()

                // Avoid deserializing an Account because it's harder to mock an response to Account while testing
                me["name"] as? String ?:
                    throw IllegalArgumentException("Expected a name")
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
        if (loggedOut)
            throw IllegalStateException("This client is logged out and should not be used anymore")

        if (forceRenew || (autoRenew && authManager.needsRenewing() && authManager.canRenew())) {
            authManager.renew()

            // forceRenew is a one-time thing, usually used when given an OAuthData that only contains valid refresh
            // token and a made-up access token, expiration, etc.
            forceRenew = false
        }

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

        val type = res.raw.body()?.contentType()

        // Try to find any API errors embedded in the JSON document
        if (type != null && type.type() == "application" && type.subtype() == "json") {
            val stub = if (res.body == "") null else JrawUtils.adapter<RedditExceptionStub<*>>().fromJson(res.body)

            // Reddit has some legacy endpoints that return 200 OK even though the JSON contains errors
            if (stub != null) {
                val ex = stub.create(NetworkException(res))
                if (ex != null) throw ex
            }
        }

        // Make sure we're still failing on non-success status codes if we couldn't find an API error in the JSON
        if (!res.successful)
            throw NetworkException(res)

        return res
    }

    /**
     * Attempts to open a WebSocket connection at the given URL.
     */
    fun websocket(url: String, listener: WebSocketListener): WebSocket {
        return http.connect(url, listener)
    }

    /**
     * Uses the [NetworkAdapter] to execute a synchronous HTTP request
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

    /**
     * Returns a Paginator builder that will iterate user subreddits. See [here](https://www.reddit.com/comments/6bqemt)
     * for more info.
     *
     * Possible `where` values:
     *
     *  - `new`
     *  - `popular`
     */
    @EndpointImplementation(Endpoint.GET_USERS_WHERE, type = MethodType.NON_BLOCKING_CALL)
    fun userSubreddits(where: String) = BarebonesPaginator.Builder.create<Subreddit>(this, "/users/${JrawUtils.urlEncode(where)}")

    /** Creates a [DefaultPaginator.Builder] to iterate posts on the front page */
    fun frontPage() = DefaultPaginator.Builder.create<Submission>(this, baseUrl = "", sortingAlsoInPath = true)

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
     * Tests if the client has the ability to access the given API endpoint. This method always returns true for script
     * apps.
     */
    fun canAccess(e: Endpoint): Boolean {
        val scopes = authManager.current?.scopes ?: return false
        // A '*' means all scopes, only used for scripts
        if (scopes.contains("*")) return true
        return scopes.contains(e.scope)
    }

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
     * Creates a BarebonesPaginator.Builder that will iterate over the latest comments from the given subreddits when
     * built. If no subreddits are given, comments will be from any subreddit.
     */
    fun latestComments(vararg subreddits: String): BarebonesPaginator.Builder<Comment> {
        val prefix = if (subreddits.isEmpty()) "" else "/r/" + subreddits.joinToString("+")
        return BarebonesPaginator.Builder.create(this, "$prefix/comments")
    }

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
    fun lookup(fullNames: List<String>): Listing<Any> {
        if (fullNames.isEmpty()) return Listing.empty()

        val type = Types.newParameterizedType(Listing::class.java, Any::class.java)
        val adapter = JrawUtils.moshi.adapter<Listing<Any>>(type, Enveloped::class.java)
        return request {
            it.endpoint(Endpoint.GET_INFO)
                .query(mapOf("id" to fullNames.joinToString(",")))
        }.deserializeWith(adapter)
    }

    fun liveThread(id: String) = LiveThreadReference(this, id)

    @EndpointImplementation(Endpoint.GET_LIVE_HAPPENING_NOW)
    fun happeningNow(): LiveThread? {
        val res = request {
            it.endpoint(Endpoint.GET_LIVE_HAPPENING_NOW)
        }

        // A 204 response means there's nothing happening right now
        if (res.code == 204) return null

        return res.deserialize()
    }

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
