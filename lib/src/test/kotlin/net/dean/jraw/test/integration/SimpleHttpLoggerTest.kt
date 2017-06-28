package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.SimpleHttpLogger
import net.dean.jraw.http.oauth.OAuthHelper
import net.dean.jraw.ratelimit.NoopRateLimiter
import net.dean.jraw.test.CredentialsUtil
import net.dean.jraw.test.newOkHttpAdapter
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import kotlin.properties.Delegates

class SimpleHttpLoggerTest : Spek({
    var baos = ByteArrayOutputStream()
    var reddit: RedditClient by Delegates.notNull()

    // Get the string value of the byte array in UTF-8 and split by newlines, removing the last element because the
    // output string ends in a newline, thus creating an empty string at the end of the list
    fun loggerOutput() = String(baos.toByteArray(), StandardCharsets.UTF_8).split("\n").dropLast(1)

    beforeGroup {
        reddit = OAuthHelper.script(CredentialsUtil.script, newOkHttpAdapter())
    }

    beforeEachTest {
        baos = ByteArrayOutputStream()
        reddit.logger = SimpleHttpLogger(PrintStream(baos), maxLineLength = 120)
        reddit.rateLimiter = NoopRateLimiter()
    }

    it("should log both input and output") {
        val url = "https://httpbin.org/get"
        reddit.request(HttpRequest.Builder()
            .url(url)
            .build())
        val output = loggerOutput()
        output.size.should.equal(2)
        output[0].should.equal("[1 ->] GET $url")
        output[1].should.startWith("[<- 1] 200 application/json: '")
        // The request response should be more than maxLineLength, so SimpleHttpLogger should truncate it
        output[1].should.have.length.at.most((reddit.logger as SimpleHttpLogger).maxLineLength)
    }

    it("should keep a record of all requests sent during its time logging") {
        val times = 5
        for (i in 0..times - 1) {
            reddit.request(HttpRequest.Builder()
                .url("https://httpbin.org/get")
                .build())
        }

        val output = loggerOutput()
        output.should.have.size(times * 2) // 1 for the request, 1 for the response
        for (i in 1..times) {
            output[(i - 1) * 2].should.startWith("[$i ->] ")
            output[(i - 1) * 2 + 1].should.startWith("[<- $i] ")
        }
    }

    it("should log the request body when it's form URL-encoded data") {
        val postData = mapOf("foo" to "bar", "baz" to "qux")
        reddit.request(HttpRequest.Builder()
            .url("https://httpbin.org/post")
            .post(postData)
            .build())

        val output = loggerOutput()

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
        baos.close()
    }
})

