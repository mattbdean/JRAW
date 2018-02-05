package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.NetworkException
import net.dean.jraw.http.SimpleHttpLogger
import net.dean.jraw.models.*
import net.dean.jraw.oauth.OAuthHelper
import net.dean.jraw.pagination.Paginator
import net.dean.jraw.test.*
import net.dean.jraw.test.TestConfig.reddit
import okhttp3.HttpUrl
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*
import kotlin.properties.Delegates

class RedditClientTest : Spek({
    describe("requestStub") {
        it("should provide a builder with the default settings") {
            val request = reddit.requestStub().build()
            request.url.should.equal("https://oauth.reddit.com/")
            request.headers.get("Authorization").should.match(Regex("^bearer [A-Za-z0-9_\\-]+"))
            request.method.should.equal("GET")
            request.basicAuth.should.be.`null`
            request.body.should.be.`null`
        }
    }

    describe("logHttp") {
        it("shouldn't log HTTP requests when false") {
            val reddit = OAuthHelper.automatic(newOkHttpAdapter(), CredentialsUtil.script)
            val adapter = InMemoryLogAdapter()

            // Give the RedditClient our logger
            reddit.logger = SimpleHttpLogger(out = InMemoryLogAdapter())
            reddit.logHttp = false

            // Make a request, which would trigger writing to the BAOS if logHttp is being ignored
            reddit.request(HttpRequest.Builder()
                .url("https://httpbin.org/get")
                .build())

            adapter.output().should.be.empty
        }
    }

    describe("request") {
        describe("rawJson") {
            lateinit var adapter: SpyNetworkAdapter
            lateinit var reddit: RedditClient

            beforeEachTest {
                adapter = SpyNetworkAdapter()
                reddit = newMockRedditClient(adapter)
            }

            it("should not add raw_json=1 to the query when rawJson is false") {
                reddit.request(HttpRequest.Builder()
                    .url("https://foo.bar")
                    .rawJson(false)
                    .build())

                val url = HttpUrl.parse(adapter.history.first().url)!!
                url.queryParameterNames().should.not.contain("raw_json")
            }

            it("should add raw_json=1 to the query when rawJson is true") {
                reddit.request(HttpRequest.Builder()
                    .url("https://foo.bar")
                    .build())

                val url = HttpUrl.parse(adapter.history.first().url)!!
                url.queryParameterValues("raw_json").should.equal(listOf("1"))
            }

            it("should not add raw_json=1 to the query when it's already present") {
                reddit.request(HttpRequest.Builder()
                    .url("https://foo.bar/?raw_json=1")
                    .build())

                val url = HttpUrl.parse(adapter.history.first().url)!!
                url.queryParameterValues("raw_json").should.equal(listOf("1"))
            }
        }
    }

    describe("randomSubreddit") {
        it("should return a random subreddit") {
            // Just make sure the request succeeds and the JSON deserializes
            reddit.randomSubreddit().about()
        }
    }

    describe("retryLimit") {
        var httpAdapter: MockNetworkAdapter by Delegates.notNull()

        beforeEachTest {
            httpAdapter = MockNetworkAdapter()
            httpAdapter.start()
        }

        it("should affect how many times a request is retried when it encounters a 5XX error") {
            val retryLimit = 3

            // Enqueue `retryLimit + 1` responses. The client should execute the request and then retry [retryLimit]
            // more times.
            for (i in 0..retryLimit) {
                // Use a dynamic code to make sure it's including all 5XX codes
                httpAdapter.enqueue(MockHttpResponse(code = 500 + i))
            }

            val reddit = newMockRedditClient(httpAdapter)
            reddit.retryLimit = retryLimit

            expectException(NetworkException::class) {
                // Doesn't matter what request is sent, responses are controlled by us. The client should receive these
                // HTTP 500 Internal Server Error responses we enqueued earlier and retry up to [retryLimit] times,
                // eventually giving up and throwing a NetworkException
                reddit.request { it.url(httpAdapter.mockServer.url("/").toString()) }
            }

            // Should have executed [retryLimit] requests after the first failed request
            httpAdapter.mockServer.requestCount.should.equal(retryLimit + 1)
        }

        afterEachTest {
            httpAdapter.reset()
        }
    }

    describe("autoRenew") {
        it("should request a new token when the old one has expired") {
            val reddit = OAuthHelper.automatic(newOkHttpAdapter(),  CredentialsUtil.script)
            val initialAccessToken = reddit.authManager.accessToken

            fun doRequest() {
                reddit.me().about()
            }

            // Send a request that should NOT trigger a renewal
            doRequest()
            reddit.authManager.accessToken.should.equal(initialAccessToken)

            // Set the tokenExpiration to 1 ms in the past
            reddit.authManager.update(
                reddit.authManager.current!!.withExpiration(Date(Date().time - 1))
            )

            // Send a request that SHOULD trigger a renewal
            doRequest()
            reddit.authManager.accessToken.should.not.equal(initialAccessToken)
        }
    }

    describe("subreddits") {
        it("should create a Paginator.Builder that iterates multiple subreddits") {
            reddit.subreddits("pics", "funny", "videos").posts().limit(Paginator.RECOMMENDED_MAX_LIMIT).build().next()
                .map { it.subreddit } // Transform each post to its subreddit
                .distinct() // Leave only unique values
                .sorted() // Sort the subreddits in ABC order
                .should.equal(listOf("funny", "pics", "videos"))
        }
    }

    describe("frontPage") {
        it("should iterate the front page") {
            expectDescendingScore(
                objects = reddit.frontPage()
                    .sorting(SubredditSort.TOP)
                    .timePeriod(TimePeriod.ALL)
                    .limit(10)
                    .build().next(),
                allowedMistakes = 3)
        }
    }

    describe("lookup") {
        it("should not send a request when given no names") {
            // NoopNetworkAdapter throws an Exception when trying to send a request
            val reddit = newMockRedditClient(NoopNetworkAdapter)
            reddit.lookup().should.equal(Listing.empty())
        }

        it("should accept full names of submissions, comments, and subreddits") {
            val res = reddit.lookup("t5_2qh0u", "t3_6afe8u", "t1_dhe4fl0")

            val supertypes = listOf(Subreddit::class, Submission::class, Comment::class)

            // We specified 3 full names, should get 3 models back
            res.should.have.size(3)

            for (i in supertypes.indices) {
                res[i].should.be.an.instanceof(supertypes[i].java)
            }
        }
    }

    describe("latestComments") {
        it("should iterate over the latest comments") {
            val limit = 50
            val comments = reddit.latestComments().limit(limit).build()
            for (i in 0..1) {
                // There should be more than enough content to fill the limit
                comments.next().should.have.size(limit)
            }
        }

        it("should only include comments from the specified subreddits") {
            val subreddits = listOf("java", "redditdev")
            reddit.latestComments(*subreddits.toTypedArray()).build().next().forEach {
                subreddits.should.contain(it.subreddit)
            }
        }
    }

    describe("happeningNow") {
        it("should return a LiveThread or null when nothing big is happening") {
            val thread = reddit.happeningNow()
            thread?.state?.should?.equal("live")
        }
    }

    describe("userSubreddits") {
        val whereValues = listOf("new", "popular")
        for (where in whereValues) {
            it("should iterate $where") {
                val limit = 25
                val pages = 2
                val paginator = reddit.userSubreddits(where).limit(limit).build()
                paginator.accumulateMerged(pages).should.have.size(limit * pages)
            }
        }
    }
})
