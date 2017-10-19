package net.dean.jraw.test

import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.HttpResponse
import net.dean.jraw.http.NetworkAdapter
import net.dean.jraw.http.UserAgent
import net.dean.jraw.models.OAuthData
import net.dean.jraw.oauth.AuthManager
import net.dean.jraw.oauth.AuthMethod
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.TokenStore
import net.dean.jraw.ratelimit.NoopRateLimiter
import okhttp3.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.util.*
import java.util.concurrent.TimeUnit

/** Creates a totally BS OAuthData object */
fun createMockOAuthData(includeRefreshToken: Boolean = false) = OAuthData.create(
    /* accessToken = */ "<access_token>",
    /* scopes = */ listOf("*"), // '*' means all scopes
    /* refreshToken = */ if (includeRefreshToken) "<refresh_token>" else null,
    /* expiration = */ Date(Date().time + TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS))
)


fun createMockCredentials(type: AuthMethod) = when (type) {
    AuthMethod.SCRIPT -> Credentials.script("", "", "", "")
    AuthMethod.APP -> Credentials.installedApp("", "")
    AuthMethod.WEBAPP -> Credentials.webapp("", "", "")
    AuthMethod.USERLESS -> Credentials.userless("", "", UUID.randomUUID())
    AuthMethod.USERLESS_APP -> Credentials.userlessApp("", UUID.randomUUID())
    else -> throw IllegalArgumentException("Not implemented for $type")
}

/** Creates a RedditClient with mocked OAuthData, Credentials, an InMemoryTokenStore, and a NoopRateLimiter */
fun newMockRedditClient(adapter: NetworkAdapter): RedditClient {
    val r = RedditClient(adapter, createMockOAuthData(), createMockCredentials(AuthMethod.SCRIPT), InMemoryTokenStore(), overrideUsername = "<mock>")
    r.rateLimiter = NoopRateLimiter()
    return r
}

/**
 * An NetworkAdapter that we can pre-configure responses for.
 *
 * Use [enqueue] to add a response to the queue. Executing a request will send the response at the head of the queue and
 * remove it.
 */
class MockNetworkAdapter : NetworkAdapter {
    override var userAgent: UserAgent = UserAgent("doesn't matter, no requests are going to be sent")
    val http = OkHttpClient()
    var mockServer = MockWebServer()

    private val responseCodeQueue: Queue<Int> = LinkedList()

    override fun connect(url: String, listener: WebSocketListener): WebSocket {
        throw NotImplementedError()
    }

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

/**
 * Used exclusively with [MockNetworkAdapter]
 */
data class MockHttpResponse(
    val body: String = """{"mock":"response"}""",
    val code: Int = 200,
    val contentType: String = "application/json"
)

class InMemoryTokenStore : TokenStore {
    private val dataMap: MutableMap<String, OAuthData?> = HashMap()
    private val refreshMap: MutableMap<String, String?> = HashMap()
    override fun storeLatest(username: String, data: OAuthData) {
        if (username == AuthManager.USERNAME_UNKOWN)
            throw IllegalArgumentException("Username was ${AuthManager.USERNAME_UNKOWN}")
        dataMap.put(username, data)
    }

    override fun storeRefreshToken(username: String, token: String) {
        if (username == AuthManager.USERNAME_UNKOWN)
            throw IllegalArgumentException("Username was ${AuthManager.USERNAME_UNKOWN}")
        refreshMap.put(username, token)
    }

    override fun fetchLatest(username: String): OAuthData? {
        return dataMap[username]
    }

    override fun fetchRefreshToken(username: String): String? {
        return refreshMap[username]
    }

    override fun deleteLatest(username: String) {
        dataMap.remove(username)
    }

    override fun deleteRefreshToken(username: String) {
        refreshMap.remove(username)
    }

    fun reset() {
        dataMap.clear()
        refreshMap.clear()
    }

    fun resetDataOnly() {
        dataMap.clear()
    }
}
