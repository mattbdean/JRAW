package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.Endpoint
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.test.util.expectException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class HttpRequestTest: Spek({
    it("should let us use url() only") {
        val url = "https://google.com"
        HttpRequest.Builder()
            .get()
            .url(url)
            .build().url.should.equal(url)
    }

    it("should let us use secure(), host(), path(), and query() instead of url()") {
        HttpRequest.Builder()
            .get()
            .secure(true)
            .host("google.com")
            .path("/search")
            .query(mapOf("q" to "hello", "foo" to "bar"))
            .build().url.should.equal("https://google.com/search?q=hello&foo=bar")
    }

    it("should throw an error if we leave the host out") {
        expectException(IllegalArgumentException::class) {
            HttpRequest.Builder()
                .path("/foo")
                .build()
        }
    }

    it("should handle paths without a leading slash") {
        HttpRequest.Builder()
            .secure(true)
            .host("google.com")
            .path("search")
            .build().url.should.equal("https://google.com/search")
    }

    it("should handle path parameters") {
        HttpRequest.Builder()
            .host("github.com")
            .path("/{user}/{repo}", "JetBrains", "kotlin")
            .build().url.should.equal("https://github.com/JetBrains/kotlin")
    }

    it("should set the host and path for endpoint()") {
        val r = HttpRequest.Builder()
            .endpoint(Endpoint.DELETE_MULTI_MULTIPATH_R_SRNAME, "foo", "bar")
            .build()

        r.url.should.equal("https://oauth.reddit.com/api/multi/foo/r/bar")

        // endpoint() doesn't change the HTTP method
        r.method.should.equal("GET")
    }
})
