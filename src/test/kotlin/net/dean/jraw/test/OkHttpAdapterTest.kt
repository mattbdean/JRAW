package net.dean.jraw.test

import com.fasterxml.jackson.databind.JsonNode
import com.winterbe.expekt.should
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.HttpResponse
import net.dean.jraw.http.OkHttpAdapter
import okhttp3.internal.http.HttpMethod
import org.awaitility.Awaitility.await
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.net.URL

private val userAgent = "net.dean.jraw.test"
private val formBody = mapOf("foo" to "bar", "baz" to "qux")

class OkHttpAdapterTest: Spek({
    var http: OkHttpAdapter = OkHttpAdapter(userAgent)
    beforeEachTest {
        http = OkHttpAdapter(userAgent)
    }

    describe("execute") {
        it("should execute asynchronously by default") {
            // Pick these two because they don't need a response body
            for (method in listOf("GET", "POST", "PUT", "PATCH", "DELETE")) {
                httpAsync(http, createTestRequestBuilder(method), ::validateResponse)
            }
        }

        it("should allow sync via a builder flag") {
            http.execute(createTestRequestBuilder("GET")
                .success({ validateResponse(it.json) })
                .build())
        }

        it("should handle basic authentication") {
            var challenged = false

            http.execute(HttpRequest.Builder()
                .url("https://httpbin.org/basic-auth/user/passwd")
                .basicAuth("user" to "passwd")
                .success { challenged = true }
                .failure({ challenged = true; throw IllegalStateException("should have passed basic auth") })
                .build())

            await().until({ challenged })
        }
    }

    describe("executeSync") {
        it("should ignore success and failure callbacks") {
            val fail: (res: HttpResponse) -> Unit = { throw IllegalStateException("should not have reached here") }
            http.execute(createTestRequestBuilder("GET")
                .success(fail)
                .failure(fail)
                .build())
        }
    }
})

fun validateResponse(body: JsonNode) {
    body.get("headers").get("User-Agent").textValue().should.equal(userAgent)

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

    if (HttpMethod.requiresRequestBody(method.toUpperCase()))
        b.method(method, formBody)
    else
        b.method(method)

    return b
}
