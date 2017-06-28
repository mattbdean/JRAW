package net.dean.jraw.test.unit

import com.fasterxml.jackson.databind.JsonNode
import com.winterbe.expekt.should
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.OkHttpAdapter
import net.dean.jraw.test.TestConfig.userAgent
import okhttp3.internal.http.HttpMethod
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.net.URL

private val otherHeader = "X-Foo" to "Bar"
private val formBody = mapOf("foo" to "bar", "baz" to "qux")

class OkHttpAdapterTest : Spek({
    var http: OkHttpAdapter = OkHttpAdapter(userAgent)
    beforeEachTest {
        http = OkHttpAdapter(userAgent)
    }

    describe("execute") {
        it("should support sending request bodies") {
            // Pick these two because they don't need a response body
            for (method in listOf("GET", "POST", "PUT", "PATCH", "DELETE")) {
                validateResponse(http.execute(createTestRequestBuilder(method).build()).json)
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

fun validateResponse(body: JsonNode) {
    body.get("headers").get("User-Agent").textValue().should.equal(userAgent.value)
    body.get("headers").get(otherHeader.first).textValue().should.equal(otherHeader.second)

    val method = URL(body.get("url").asText()).path.substring(1).toUpperCase()
    if (HttpMethod.requiresRequestBody(method)) {
        val formNode = body.get("form")
        formNode.size().should.equal(formBody.size)
        for ((k, v) in formBody) {
            formNode.has(k).should.be.`true`
            formNode.get(k).textValue().should.equal(v)
        }
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
