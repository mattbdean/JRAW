package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.LogAdapter
import net.dean.jraw.http.SimpleHttpLogger
import net.dean.jraw.test.InMemoryLogAdapter
import net.dean.jraw.test.MockNetworkAdapter
import net.dean.jraw.test.expectException
import net.dean.jraw.test.newMockRedditClient
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.PrintStream
import kotlin.properties.Delegates

class SimpleHttpLoggerTest : Spek({
    val logAdapter = InMemoryLogAdapter()
    var reddit: RedditClient by Delegates.notNull()
    val mockAdapter = MockNetworkAdapter()

    beforeGroup {
        reddit = newMockRedditClient(mockAdapter)
    }

    beforeEachTest {
        mockAdapter.start()
        reddit.logger = SimpleHttpLogger(out = logAdapter, maxLineLength = 120)
    }

    it("should log both input and output") {
        val url = "http://example.com/foo"
        val res = """{"foo":"bar"}"""
        mockAdapter.enqueue(res)
        reddit.request(HttpRequest.Builder()
            .url(url)
            .build())
        logAdapter.output().should.equal(listOf(
            "[1 ->] GET $url",
            "[<- 1] 200 application/json: '$res'"
        ))
    }

    it("should keep a record of all requests sent during its time logging") {
        val times = 5
        val res = """{"foo":"bar"}"""
        for (i in 0 until times) {
            mockAdapter.enqueue(res)
            reddit.request(HttpRequest.Builder()
                .url("http://example.com/foo")
                .build())
        }

        val output = logAdapter.output()
        output.should.have.size(times * 2) // 1 for the request, 1 for the response
        for (i in 1..times) {
            output[(i - 1) * 2].should.startWith("[$i ->] ")
            output[(i - 1) * 2 + 1].should.startWith("[<- $i] ")
        }
    }

    it("should truncate all lines to maxLineLength if above 0") {
        val maxLineLength = 50
        reddit.logger = SimpleHttpLogger(out = logAdapter, maxLineLength = maxLineLength)
        mockAdapter.enqueue("""{"foo": "${"bar".repeat(100)}"}""")
        reddit.request {
            it.url("http://example.com/${"reallylongpath/".repeat(10)}")
                .post(mapOf(
                    "key" to "value".repeat(15)
                ))
        }

        val output = logAdapter.output()
        output.forEach { it.should.have.length(maxLineLength) }
    }

    it("should not truncate for a maxLineLength < 0")  {
        reddit.logger = SimpleHttpLogger(out = logAdapter, maxLineLength = -1)
        val res = """{"foo": "${"bar".repeat(100)}"}"""
        mockAdapter.enqueue(res)
        reddit.request {
            it.url("http://example.com/${"reallylongpath/".repeat(10)}")
                .post(mapOf(
                    "key" to "value".repeat(30)
                ))
        }

        logAdapter.output().forEach { it.should.have.length.above(100) }
    }

    it("should throw an IllegalArgumentException if given a maxLineLength between 0 and ELLIPSIS.length") {
        for (i in 0..SimpleHttpLogger.ELLIPSIS.length) {
            expectException(IllegalArgumentException::class) {
                SimpleHttpLogger(maxLineLength = i)
            }
        }

        // Should not throw an exception
        SimpleHttpLogger(maxLineLength = SimpleHttpLogger.ELLIPSIS.length + 1)
    }

    it("should log the request body when it's form URL-encoded data") {
        val postData = mapOf("foo" to "bar", "baz" to "qux")
        mockAdapter.enqueue("""{"foo":"bar"}""")
        reddit.request(HttpRequest.Builder()
            .url("http://example.com")
            .post(postData)
            .build())

        val output = logAdapter.output()

        /*
        [12 ->] POST https://httpbin.org/post
                form: form=foo
                      abc=123
        [12 <-] application/json: <response>
         */

        // One for the request, one for the response, one for every key-value pair in the post data
        output.should.have.size(2 + postData.size)
        // output[0] should be the basic request info
        val baseIndent = " ".repeat("[0 ->]".length)

        output[1].should.equal("$baseIndent form: foo=bar")
        output[2].should.equal("$baseIndent       baz=qux")
    }

    afterEachTest {
        logAdapter.reset()
        mockAdapter.reset()
    }
}) {
}

