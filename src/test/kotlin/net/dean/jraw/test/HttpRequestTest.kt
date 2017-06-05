package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.http.HttpRequest
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.lang.AssertionError

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
            .query(mapOf("q" to "hello"))
            .build().url.should.equal("https://google.com/search?q=hello")
    }

    it("should throw an error if we leave the host out") {
        try {
            HttpRequest.Builder()
                .path("/foo")
                .build()

            throw AssertionError("Expecting an IllegalArgumentException")
        } catch (ex: Exception) {
            if (ex !is IllegalArgumentException) {
                // We were expecting an IllegalArgumentException
                throw AssertionError("Expecting an IllegalArgumentException")
            }
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
})
