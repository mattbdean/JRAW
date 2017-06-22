package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.models.KindConstants
import net.dean.jraw.models.SubmissionKind
import net.dean.jraw.models.VoteDirection
import net.dean.jraw.references.SubmissionReference
import net.dean.jraw.test.util.TestConfig.reddit
import net.dean.jraw.test.util.ignoreRateLimit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class SubmissionReferenceTest : Spek({
    val ref: SubmissionReference = reddit.submission("65eeke")

    describe("upvote/downvote/unvote") {
        it("should have an effect on a model") {
            fun expectVote(dir: VoteDirection) {
                val value = if (dir == VoteDirection.UP) true else if (dir == VoteDirection.DOWN) false else null
                ref.inspect().likes.should.equal(value)
            }
            ref.upvote()
            expectVote(VoteDirection.UP)

            ref.downvote()
            expectVote(VoteDirection.DOWN)

            ref.unvote()
            expectVote(VoteDirection.NONE)
        }
    }

    describe("save/unsave") {
        it("should have an effect on the model") {
            fun expectSaved(saved: Boolean) {
                ref.inspect().saved.should.equal(saved)
            }

            ref.save()
            expectSaved(true)

            ref.unsave()
            expectSaved(false)
        }
    }

    // TODO create the submission first since reddit does not ratelimit comments to submissions made by the user that
    //      created the thread
    describe("reply") {
        it("should return the newly created Comment") {
            val submissionId = "6ib8fx"
            val text = "Comment made at ${Date()}"
            val comment = reddit.submission(submissionId).reply(text)
            comment.body.should.equal(text)
            comment.submissionFullName.should.equal(KindConstants.SUBMISSION + "_$submissionId")
        }
    }

    describe("delete") {
        it("should delete the submission") {
            ignoreRateLimit {
                val id = reddit.subreddit("jraw_testing2").submit(SubmissionKind.SELF, "temp", "temp", false)
                val linkRef = reddit.submission(id)
                linkRef.delete()
                linkRef.inspect().author.should.equal("[deleted]")
            }
        }
    }
})
