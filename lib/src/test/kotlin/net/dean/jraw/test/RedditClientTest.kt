package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.NetworkException
import net.dean.jraw.http.SimpleHttpLogger
import net.dean.jraw.http.oauth.OAuthHelper
import net.dean.jraw.test.util.*
import net.dean.jraw.test.util.TestConfig.reddit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class RedditClientTest : Spek({
    describe("requestStub") {
        it("should provide a builder with the default settings") {
            val request = reddit.requestStub().build()
            request.url.should.equal("https://oauth.reddit.com/")
            request.headers.get("Authorization").should.match(Regex("^bearer [A-Za-z0-9_\\-]+"))
        }
    }

    it("shouldn't log HTTP requests when logHttp=false") {
        val baos = ByteArrayOutputStream()
        val reddit = OAuthHelper.script(CredentialsUtil.script, newOkHttpAdapter())

        // Give the RedditClient our logger
        reddit.logger = SimpleHttpLogger(PrintStream(baos))
        reddit.logHttp = false

        // Make a request, which would trigger writing to the BAOS if logHttp is being ignored
        reddit.request(HttpRequest.Builder()
            .url("https://httpbin.org/get")
            .build())

        baos.size().should.equal(0)
    }

    describe("randomSubreddit") {
        it("should return a random subreddit") {
            // Just make sure the request succeeds and the JSON deserializes
            reddit.randomSubreddit().about()
        }
    }

    describe("retryLimit") {
        it("should affect how many times a request is retried when it encounters a 5XX error") {
            val retryLimit = 3
            val httpAdapter = MockHttpAdapter()

            // Enqueue (retryLimit + 1) responses. The client should execute the request and then retry [retryLimit] more
            // times.
            for (i in 0..retryLimit) {
                // Use a dynamic code to make sure it's including all 5XX codes
                httpAdapter.enqueue(MockHttpResponse(code = 500 + i))
            }

            val reddit = RedditClient(httpAdapter, createMockOAuthData(), CredentialsUtil.script)
            reddit.retryLimit = retryLimit

            expectException(NetworkException::class) {
                // Doesn't matter what request is sent, responses are controlled by us. The client should receive these
                // HTTP 500 Internal Server Error responses we enqueued earlier and retry up to [retryLimit] times,
                // eventually giving up and throwing a NetworkException
                reddit.request { it.path("foo") }
            }

            // We enqueued (retryLimit + 1) responses
            httpAdapter.remaining().should.equal(0)
        }
    }

    describe("subreddits") {
        it("should create a Paginator.Builder that iterates multiple subreddits") {
            reddit.subreddits("pics", "funny", "videos").limit(100).build().next()
                .map { it.subreddit } // Transform each post to its subreddit
                .distinct() // Leave only unique values
                .sorted() // Sort the subreddits in ABC order
                .should.equal(listOf("funny", "pics", "videos"))
        }
    }
})
