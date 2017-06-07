package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.SimpleHttpLogger
import net.dean.jraw.http.oauth.OAuthHelper
import net.dean.jraw.test.util.CredentialsUtil
import net.dean.jraw.test.util.TestConfig.reddit
import net.dean.jraw.test.util.newOkHttpAdapter
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
})
