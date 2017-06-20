package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.models.SubmissionKind
import net.dean.jraw.test.util.TestConfig.reddit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class SubredditReferenceTest : Spek({
    describe("submit") {
        val now = Date()
        val ref = reddit.subreddit("jraw_testing2")

        it("should be able to submit a self post") {
            val id = ref.submit(SubmissionKind.SELF, "test self post", "submitted $now", sendReplies = false)
            reddit.submission(id).comments().submission.id.should.equal(id)
        }

        it("should be able to submit a link") {
            val id = ref.submit(SubmissionKind.LINK, "test link post", "http://example.com/${now.time}", sendReplies = false)
            reddit.submission(id).comments().submission.id.should.equal(id)
        }
    }
})
