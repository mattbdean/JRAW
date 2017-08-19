package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.HttpResponse
import net.dean.jraw.http.NoopHttpLogger
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.properties.Delegates

class NoopHttpLoggerTest : Spek({
    var logger: NoopHttpLogger by Delegates.notNull()

    beforeEachTest {
        logger = NoopHttpLogger()
    }

    describe("request") {
        it("should always return the same tag") {
            logger.request(HttpRequest.Builder().url("http://foo").build()).should.equal(NoopHttpLogger.TAG)
        }
    }

    describe("response") {
        it("should do nothing") {
            logger.response(NoopHttpLogger.TAG, HttpResponse(
                Response.Builder()
                    .request(Request.Builder()
                        .url("http://foo")
                        .build())
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .build())
            )
        }
    }
})
