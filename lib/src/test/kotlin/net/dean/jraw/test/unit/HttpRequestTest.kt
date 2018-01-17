package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.Endpoint
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.test.expectException
import okio.Buffer
import okio.Okio
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.ByteArrayOutputStream
import java.io.PrintStream

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

    it("should automatically URL-encode path params") {
        HttpRequest.Builder()
            .secure(true)
            .host("google.com")
            .path("/{foo}/{bar}", "<", ">")
            .build().url.should.equal("https://google.com/%3C/%3E")
    }

    it("should handle path relevantParameters") {
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

    it("endpoint with subreddit sets the url correctly") {
        val r = HttpRequest.Builder()
            .endpoint(Endpoint.GET_HOT, "pics")
            .build()

        r.url.should.equal("https://oauth.reddit.com/r/pics/hot")
    }

    it("endpoint with no subreddit sets the url correctly") {
        val r = HttpRequest.Builder()
            .endpoint(Endpoint.GET_HOT, null)
            .build()

        r.url.should.equal("https://oauth.reddit.com/hot")
    }

    it("endpoint throws exception on null parameter") {
        expectException(IllegalArgumentException::class) {
            HttpRequest.Builder()
                .endpoint(Endpoint.GET_COMMENTS_ARTICLE, "pics", null)
        }
    }

    it("builder throws exception if optional subreddit `null` is not supplied") {
        expectException(IllegalArgumentException::class) {
            HttpRequest.Builder()
                .endpoint(Endpoint.GET_HOT)
                .build()
        }
    }

    it("should not strip special characters from form body when adding via map") {
        // See #220 for why this test exists
        val r = HttpRequest.Builder()
            .url("https://foo.bar")
            .post(mapOf(
                "withTabs" to "a\tb",
                "withNewLines" to "a\nb",
                "withPluses" to "a+b",
                "withSpaces" to "a b"
            ))
            .build()

        // Write the form body to a UTF-8 string
        val out = ByteArrayOutputStream()
        val sink = Okio.buffer(Okio.sink(out))
        r.body!!.writeTo(sink)
        sink.flush()

        // \n == %0A, \t == %09, + == %2B, ' ' (space) == %20
        out.toString("UTF-8").should.equal("withTabs=a%09b&withNewLines=a%0Ab&withPluses=a%2Bb&withSpaces=a%20b")
    }
})
