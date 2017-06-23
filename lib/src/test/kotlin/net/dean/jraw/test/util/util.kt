package net.dean.jraw.test.util

import net.dean.jraw.RateLimitException
import net.dean.jraw.RedditClient
import net.dean.jraw.http.*
import net.dean.jraw.http.oauth.OAuthData
import net.dean.jraw.models.Listing
import net.dean.jraw.models.Submission
import net.dean.jraw.test.util.TestConfig.userAgent
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.TestBody
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import java.util.*
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
    try {
        // Make sure the request doesn't fail
        reddit.request {
            it.path("/hot")
        }
    } catch (e: NetworkException) {
        // Wrap the error to make sure the tester knows why the test failed
        if (e.res.code == 401)
            throw IllegalStateException("Not authenticated, API responded with 401", e)

        // Something else went wrong
        throw e
    }
}

fun newOkHttpAdapter() = OkHttpAdapter(userAgent)

fun expectDescendingScore(posts: Listing<Submission>, allowedMistakes: Int = 0) {
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
    } catch (e: Exception) {
        if (e is RateLimitException) {
            System.err.println("Skipping test due to rate limit (${e.message})")
            return
        }
        throw e
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
    private val responses: Queue<MockHttpResponse> = LinkedList()

    override fun execute(r: HttpRequest): HttpResponse {
        if (responses.isEmpty()) throw IllegalStateException("response queue is empty")
        val res = responses.remove()

        return HttpResponse(
            code = res.code,
            readBody = { res.body },
            contentType = res.contentType,
            requestMethod = res.requestMethod,
            requestUrl = r.url
        )
    }

    fun enqueue(r: MockHttpResponse) { responses.add(r) }
    fun remaining() = responses.size
}

/**
 * Used exclusively with [MockHttpAdapter]
 */
data class MockHttpResponse(
    val requestMethod: String = "GET",
    val body: String = "{'mock':'response'}".replace('\'', '"'),
    val code: Int = 200,
    val contentType: String = "application/json"
)

/** Creates a totally BS OAuthData object */
fun createMockOAuthData() = OAuthData(
    accessToken = "<access_token>",
    tokenType = "bearer", // normal OAuthData has this as well, might as well keep it
    scopes = listOf("*"), // '*' means all scopes
    shelfLife = -1
)
