package net.dean.jraw.test

import com.fasterxml.jackson.databind.JsonNode
import com.winterbe.expekt.should
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.RestClient
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

private val userAgent = "net.dean.jraw.test"

class RestClientTest: Spek({
    var http: RestClient = RestClient(userAgent)
    beforeEachTest {
        http = RestClient(userAgent)
    }

    describe("execute") {
        it("should execute asynchronously by default") {
            // Pick these two because they don't need a response body
            httpAsync(http, createTestRequestBuilder("GET"), ::validateResponse)
            httpAsync(http, createTestRequestBuilder("DELETE"), ::validateResponse)
        }

        it("should allow sync via a builder flag") {
            http.execute(createTestRequestBuilder("GET").success({ validateResponse(it.json) }).sync().build())
        }
    }
})

fun validateResponse(body: JsonNode) {
    body.get("headers").get("User-Agent").textValue().should.equal(userAgent)
}

fun createTestRequestBuilder(method: String) = HttpRequest.Builder()
    .method(method.toUpperCase())
    .url("https://httpbin.org/${method.toLowerCase()}")
