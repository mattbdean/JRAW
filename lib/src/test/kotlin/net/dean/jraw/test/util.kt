package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.RateLimitException
import net.dean.jraw.RedditClient
import net.dean.jraw.http.*
import net.dean.jraw.models.Listing
import net.dean.jraw.models.Submission
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.NoopTokenStore
import net.dean.jraw.oauth.OAuthData
import net.dean.jraw.oauth.TokenStore
import net.dean.jraw.ratelimit.NoopRateLimiter
import net.dean.jraw.test.TestConfig.userAgent
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.TestBody
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.reflect.KClass

fun <T : Exception> expectException(clazz: KClass<T>, doWork: () -> Unit) {
    val message = "Should have thrown ${clazz.qualifiedName}"
    try {
        doWork()
        throw IllegalStateException(message)
    } catch (e: Exception) {
        // Make sure rethrow the Exception we created here
        if (e.message == message) throw e
        // Make sure we got the right kind of Exception
        if (e::class != clazz)
            throw IllegalStateException("Expecting function to throw ${clazz.qualifiedName}, instead threw ${e::class.qualifiedName}", e)
    }
}

fun ensureAuthenticated(reddit: RedditClient) {
    reddit.authManager.current.should.not.be.`null`
    try {
        // Try to guess the best endpoint to hit based on the current OAuth scopes
        val scopes = reddit.authManager.current!!.scopes
        if (scopes.contains("*") || scopes.contains("read")) {
            reddit.frontPage().build().next()
        } else if (!reddit.authManager.authMethod.isUserless && scopes.contains("identity")) {
            reddit.me().about()
        }
    } catch (e: NetworkException) {
        // Wrap the error to make sure the tester knows why the test failed
        if (e.res.code == 401)
            throw IllegalStateException("Not authenticated, API responded with 401", e)

        // Something else went wrong
        throw e
    }
}

val rand = SecureRandom()
fun randomName(length: Int = 10): String {
    return "jraw_test_" + BigInteger(130, rand).toString(32).substring(0..length - 1)
}

val mockScriptCredentials = Credentials.script("", "", "", "")
val mockAppCredentials = Credentials.installedApp("", "")
fun newOkHttpAdapter() = OkHttpAdapter(userAgent)
fun newMockRedditClient(adapter: MockHttpAdapter): RedditClient {
    val r = RedditClient(adapter, createMockOAuthData(), mockScriptCredentials, NoopTokenStore(), overrideUsername = "<mock>")
    r.rateLimiter = NoopRateLimiter()
    return r
}

fun expectDescendingScore(posts: Listing<Submission>, allowedMistakes: Int = 0) {
    if (posts.isEmpty()) throw IllegalArgumentException("posts was empty")
    var prevScore = posts[0].score
    var mistakes = 0

    for (i in 1..posts.size - 1) {
        if (posts[i].score > prevScore)
            if (++mistakes > allowedMistakes) {
                val scores = posts.map { it.score }
                throw IllegalArgumentException("Was not descending score (allowed $allowedMistakes mistakes): $scores")
            }
        prevScore = posts[i].score
    }
}

fun SpecBody.assume(check: () -> Boolean, description: String, body: TestBody.() -> Unit) {
    if (check())
        it(description, body)
    else
        xit(description, reason = "assumption failed", body = body)
}

fun ignoreRateLimit(block: () -> Unit) {
    try {
        block()
    } catch (e: RateLimitException) {
        System.err.println("Skipping test due to rate limit (${e.message})")
    }
}

/**
 * An HttpAdapter that we can pre-configure responses for.
 *
 * Use [enqueue] to add a response to the queue. Executing a request will send the response at the head of the queue and
 * remove it.
 */
class MockHttpAdapter : HttpAdapter {
    override var userAgent: UserAgent = UserAgent("doesn't matter, no requests are going to be sent")
    val http = OkHttpClient()
    var mockServer = MockWebServer()

    private val responseCodeQueue: Queue<Int> = LinkedList()

    override fun execute(r: HttpRequest): HttpResponse {
        val path = HttpUrl.parse(r.url)!!.encodedPath()

        val res = http.newCall(Request.Builder()
            .headers(r.headers)
            .method(r.method, r.body)
            .url(mockServer.url("")
                .newBuilder()
                .encodedPath(path)
                .build())
            .build()).execute()
        return HttpResponse(res)
    }

    fun enqueue(json: String) = enqueue(MockHttpResponse(json))

    fun enqueue(r: MockHttpResponse) {
        mockServer.enqueue(MockResponse()
            .setResponseCode(r.code)
            .setBody(r.body)
            .setHeader("Content-Type", r.contentType))
        responseCodeQueue.add(r.code)
    }

    fun start() {
        mockServer.start()
    }

    fun reset() {
        mockServer.shutdown()
        mockServer = MockWebServer()
    }
}

class InMemoryTokenStore : TokenStore {
    private val dataMap: MutableMap<String, OAuthData?> = HashMap()
    private val refreshMap: MutableMap<String, String?> = HashMap()
    override fun storeCurrent(username: String, data: OAuthData) {
        dataMap.put(username, data)
    }

    override fun storeRefreshToken(username: String, token: String) {
        refreshMap.put(username, token)
    }

    override fun fetchCurrent(username: String): OAuthData? {
        return dataMap[username]
    }

    override fun fetchRefreshToken(username: String): String? {
        return refreshMap[username]
    }

    fun reset() {
        dataMap.clear()
        refreshMap.clear()
    }

    fun resetDataOnly() {
        dataMap.clear()
    }
}

object NoopHttpAdapter : HttpAdapter {
    override var userAgent: UserAgent = UserAgent("")

    override fun execute(r: HttpRequest): HttpResponse {
        throw NotImplementedError("NoopHttpAdapter cannot execute requests")
    }
}

/**
 * Used exclusively with [MockHttpAdapter]
 */
data class MockHttpResponse(
    val body: String = """{"mock":"response"}""",
    val code: Int = 200,
    val contentType: String = "application/json"
)

/** Creates a totally BS OAuthData object */
fun createMockOAuthData(includeRefreshToken: Boolean = false) = OAuthData(
    accessToken = "<access_token>",
    tokenType = "bearer", // normal OAuthData has this as well, might as well keep it
    scopes = listOf("*"), // '*' means all scopes
    shelfLife = TimeUnit.SECONDS.toMillis(3600).toInt(),
    refreshToken = if (includeRefreshToken) "<refresh_token>" else null,
    expiration = Date(Date().time + TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS))
)
