package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.RateLimitException
import net.dean.jraw.RedditClient
import net.dean.jraw.http.*
import net.dean.jraw.models.OAuthData
import net.dean.jraw.models.UniquelyIdentifiable
import net.dean.jraw.models.Votable
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.test.TestConfig.userAgent
import okhttp3.*
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.TestBody
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*
import kotlin.reflect.KClass

fun <T : Exception> expectException(clazz: KClass<T>, doWork: () -> Unit): T {
    val err: T

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

        @Suppress("UNCHECKED_CAST")
        err = e as T
    }

    return err
}

fun newOkHttpAdapter() = OkHttpNetworkAdapter(userAgent)

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

fun OAuthData.withExpiration(d: Date) = OAuthData.create(accessToken, scopes, refreshToken, d)!!

val rand = SecureRandom()
fun randomName(length: Int = 10): String {
    return "jraw_test_" + BigInteger(130, rand).toString(32).substring(0..length - 1)
}

fun <T> expectDescendingScore(objects: List<T>, allowedMistakes: Int = 0) {
    val votables = objects.map { it as Votable }
    if (votables.isEmpty()) throw IllegalArgumentException("posts was empty")
    var prevScore = votables[0].score
    var mistakes = 0

    for (i in 1 until votables.size) {
        if (votables[i].score > prevScore)
            if (++mistakes > allowedMistakes) {
                val scores = votables.map { it.score }
                throw IllegalArgumentException("Was not descending score (allowed $allowedMistakes mistakes): $scores")
            }
        prevScore = votables[i].score
    }
}

fun <T : UniquelyIdentifiable> testPaginator(p: Paginator.Builder<T>, mustHaveContent: Boolean = true): List<List<T>> {
    // Primarily just make sure that requests don't fail
    val lists = p.build().accumulate(maxPages = 2)

    // If requested, assert that the result has data
    if (mustHaveContent)
        lists[0].should.have.size.above(0)

    if (lists.size > 1)
        // Make sure we're properly requesting the next page
        lists[0][0].should.not.equal(lists[1][0])

    return lists
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

object NoopNetworkAdapter : NetworkAdapter {
    override var userAgent: UserAgent = UserAgent("")

    override fun execute(r: HttpRequest): HttpResponse {
        throw NotImplementedError("NoopNetworkAdapter cannot execute requests")
    }

    override fun connect(url: String, listener: WebSocketListener): WebSocket {
        throw NotImplementedError("NoopNetworkAdapter cannot open WebSocket connections")
    }
}

/**
 * A SpyNetworkAdapter keeps track of all the requests that it gets sent. It always returns 200 OK
 */
class SpyNetworkAdapter : NetworkAdapter {
    override var userAgent: UserAgent = UserAgent("n/a")
    val history: MutableList<HttpRequest> = mutableListOf()

    override fun execute(r: HttpRequest): HttpResponse {
        history.add(r)
        return HttpResponse(Response.Builder()
            .request(Request.Builder()
                .url("https://foo.bae")
                .build())
            .body(ResponseBody.create(MediaType.parse("text/plain"), "Response from SpyNetworkAdapter"))
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build())
    }

    override fun connect(url: String, listener: WebSocketListener): WebSocket {
        throw NotImplementedError()
    }
}
