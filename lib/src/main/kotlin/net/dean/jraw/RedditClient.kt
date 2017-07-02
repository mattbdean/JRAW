package net.dean.jraw

import net.dean.jraw.JrawUtils.jackson
import net.dean.jraw.http.*
import net.dean.jraw.http.oauth.AuthManager
import net.dean.jraw.http.oauth.AuthMethod
import net.dean.jraw.http.oauth.Credentials
import net.dean.jraw.http.oauth.OAuthData
import net.dean.jraw.models.Submission
import net.dean.jraw.pagination.DefaultPaginator
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.ratelimit.LeakyBucketRateLimiter
import net.dean.jraw.ratelimit.RateLimiter
import net.dean.jraw.references.OtherUserReference
import net.dean.jraw.references.SelfUserReference
import net.dean.jraw.references.SubmissionReference
import net.dean.jraw.references.SubredditReference
import java.util.concurrent.TimeUnit

/**
 * Specialized class for sending requests to [oauth.reddit.com](https://www.reddit.com/dev/api/oauth).
 *
 * RedditClients **must** be authenticated before they can be of any use. The
 * [OAuthHelper][net.dean.jraw.http.oauth.OAuthHelper] class can help you get authenticated.
 *
 * **RedditClients authenticated with non-script OAuth2 apps are expensive to create** since it requires sending an
 * additional HTTP request. **Creating an instance in an Android app should be done off the main thread**.
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
class RedditClient(
    /** How this client will send HTTP requests */
    val http: HttpAdapter,
    initialOAuthData: OAuthData,
    creds: Credentials
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

    /**
     * The logged-in user, or null if this RedditClient is authorized using a non-script app
     *
     * @see requireAuthenticatedUser
     */
    val username: String? = creds.username

    /** The type of OAuth2 app used to authenticate this client */
    val authMethod: AuthMethod = creds.authMethod

    private val authenticatedUsername: String?

    init {
        authManager._current = initialOAuthData

        // username will be non-null for script apps, otherwise we have to manually poll /api/v1/me for the username.
        // We can't simply use me().about().name because creating a SelfUserReference requires a call to
        // requireAuthenticatedUser() which in turn requires this variable.
        authenticatedUsername = username ?: try {
            val json = request {
                it.path("/api/v1/me")
            }.json

            if (!json.has("name"))
                throw IllegalArgumentException("Cannot get name from response")
            json.get("name").asText()
        } catch (e: ApiException) {
            // Delay throwing an exception until `requireAuthenticatedUser()` is called
            null
        }
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

        // Try to prevent API errors
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

        if (!res.successful) {
            val stub = jackson.treeToValue(res.json, RedditExceptionStub::class.java) ?: throw NetworkException(res)
            throw stub.create(NetworkException(res))
        }

        return res
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
    fun frontPage() = DefaultPaginator.Builder<Submission>(this, baseUrl = "", sortingAsPathParameter = true)

    /**
     * Creates a [SubredditReference]
     *
     * Reddit has some special subreddits:
     *
     * - /r/all - posts from every subreddit
     * - /r/popular - includes posts from subreddits that have opted out of /r/all. Guaranteed to not have NSFW content.
     * - /r/mod - submissions from subreddits the logged-in user moderates
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
    @EndpointImplementation(Endpoint.GET_RANDOM)
    fun randomSubreddit() = subreddit("random")

    /**
     * Creates a SubmissionReference. Note that `id` is NOT a full name (like `t3_6afe8u`), but rather an ID
     * (like `6afe8u`)
     */
    fun submission(id: String) = SubmissionReference(this, id)

    /**
     * Returns the name of the logged-in user
     *
     * @throws IllegalStateException If there is no logged-in user
     */
    fun requireAuthenticatedUser(): String {
        if (authMethod.isUserless)
            throw IllegalStateException("Expected the RedditClient to have an active user, was authenticated with " +
                authMethod)
        return authenticatedUsername ?: throw IllegalStateException("Expected an authenticated user")
    }

    companion object {
        /** Amount of requests per second reddit allows for OAuth2 apps (equal to 1) */
        const val RATE_LIMIT = 1L
        private const val BURST_LIMIT = 5L
        private const val DEFAULT_RETRY_LIMIT = 5
    }
}
