package net.dean.jraw.test.integration

import net.dean.jraw.test.TestConfig.reddit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import java.util.*

class SubredditReferenceTest : Spek({
    describe("submit") {
        val now = Date()
        val ref = reddit.subreddit("jraw_testing2")

        // TODO
        // submittedSelfPost is a lazily-initiated object that is created by attempting to submit a self post. All we
        // have to do is access it.
//        assume({ SharedObjects.submittedSelfPost != null }, description = "should be able to submit a self post", body = {})

        // TODO
//        it("should be able to submit a link") {
//            ignoreRateLimit {
//                val postRef = ref.submit(SubmissionKind.LINK, "test link post", "http://example.com/${now.time}", sendReplies = false)
//                postRef.inspect().subreddit.should.equal(ref.subject)
//            }
//        }
    }

    // TODO
//    describe("subscribe/unsubscribe") {
//        it("should subscribe the user to the specific subreddit") {
//            val pics = reddit.subreddit("pics")
//            pics.subscribe()
//            pics.about().isUserIsSubscriber.should.be.`true`
//            pics.unsubscribe()
//            pics.about().isUserIsSubscriber.should.be.`false`
//        }
//    }
//
//    describe("submitText") {
//        it("should return a string") {
//            reddit.subreddit("pics").submitText().should.have.length.above(0)
//        }
//    }
})
