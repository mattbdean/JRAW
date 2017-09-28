package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.ApiException
import net.dean.jraw.models.SubmissionKind
import net.dean.jraw.test.SharedObjects
import net.dean.jraw.test.TestConfig.reddit
import net.dean.jraw.test.TestConfig.redditUserless
import net.dean.jraw.test.assume
import net.dean.jraw.test.expectException
import net.dean.jraw.test.ignoreRateLimit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class SubredditReferenceTest : Spek({
    describe("submit") {
        val now = Date()
        val ref = reddit.subreddit("jraw_testing2")

        // submittedSelfPost is a lazily-initiated object that is created by attempting to submit a self post. All we
        // have to do is access it.
        assume({ SharedObjects.submittedSelfPost != null }, description = "should be able to submit a self post") {}

        it("should be able to submit a link") {
            ignoreRateLimit {
                val postRef = ref.submit(SubmissionKind.LINK, "test link post", "http://example.com/${now.time}", sendReplies = false)
                postRef.inspect().subreddit.should.equal(ref.subreddit)
            }
        }
    }

    describe("subscribe/unsubscribe") {
        it("should subscribe the user to the specific subreddit") {
            val pics = reddit.subreddit("pics")
            pics.subscribe()
            pics.about().isUserSubscriber.should.be.`true`
            pics.unsubscribe()
            pics.about().isUserSubscriber.should.be.`false`
        }
    }

    describe("submitText") {
        it("should return a string") {
            reddit.subreddit("pics").submitText().should.have.length.above(0)
        }
    }

    describe("userFlairOptions/linkFlairOptions") {
        val srName = "jraw_testing2"

        it("should throw an ApiException when there is no active user") {
            expectException(ApiException::class) {
                redditUserless.subreddit(srName).linkFlairOptions()
            }

            expectException(ApiException::class) {
                redditUserless.subreddit(srName).userFlairOptions()
            }
        }

        it("should return a list of Flairs") {
            reddit.subreddit(srName).linkFlairOptions().should.have.size.above(0)
            reddit.subreddit(srName).userFlairOptions().should.have.size.above(0)
        }
    }

    describe("rules") {
        it("should return a Ruleset") {
            val rules = reddit.subreddit("jraw_testing2").rules()
            rules.subredditRules.should.have.size.above(0)
            rules.siteRules.should.have.size.above(0)
        }
    }

    describe("stylesheet") {
        it("should return text") {
            reddit.subreddit("RocketLeague").stylesheet().should.have.length.above(0)
        }
    }
})
