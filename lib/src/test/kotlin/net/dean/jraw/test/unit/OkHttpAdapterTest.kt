package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.OkHttpNetworkAdapter
import net.dean.jraw.test.TestConfig.userAgent
import okhttp3.internal.http.HttpMethod
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.net.URL
import kotlin.properties.Delegates

class OkHttpAdapterTest : Spek({
    val otherHeader = "X-Foo" to "Bar"
    val formBody = mapOf("foo" to "bar", "baz" to "qux")

    fun validateResponse(body: HttpBinResponse) {
        // Make sure the headers we're sending are being echoed back
        body.headers["User-Agent"].should.equal(userAgent.value)
        body.headers[otherHeader.first].should.equal(otherHeader.second)

        // Extract "get" from "https://httbin.org/get?foo=bar"
        val method = URL(body.url).path.substring(1).toUpperCase()
        if (HttpMethod.requiresRequestBody(method)) {
            body.form.should.equal(formBody)
        }
    }

    fun createTestRequestBuilder(method: String): HttpRequest.Builder {
        val b = HttpRequest.Builder()
            .url("https://httpbin.org/${method.toLowerCase()}")
            .header(otherHeader.first, otherHeader.second)

        if (HttpMethod.requiresRequestBody(method.toUpperCase()))
            b.method(method, formBody)
        else
            b.method(method)

        return b
    }

    var http: OkHttpNetworkAdapter by Delegates.notNull()
    beforeEachTest {
        http = OkHttpNetworkAdapter(userAgent)
    }

    describe("execute") {
        it("should support sending request bodies") {
            // Pick these two because they don't need a response body
            for (method in listOf("GET", "POST", "PUT", "PATCH", "DELETE")) {
                validateResponse(http.execute(createTestRequestBuilder(method).build()).deserialize())
            }
        }

        it("should handle basic authentication") {
            // If basic authentication isn't working, execute() will throw a NetworkException
            http.execute(HttpRequest.Builder()
                .url("https://httpbin.org/basic-auth/user/passwd")
                .basicAuth("user" to "passwd")
                .build())
        }

        it("should accurately report the status code") {
            http.execute(HttpRequest.Builder()
                .url("https://httpbin.org/status/418")
                .build())
                .code.should.equal(418) // I'm a teapot
        }
    }
})

data class HttpBinResponse(
    val headers: Map<String, String>,
    val args: Map<String, String>,
    val url: String,
    val form: Map<String, String>
)
