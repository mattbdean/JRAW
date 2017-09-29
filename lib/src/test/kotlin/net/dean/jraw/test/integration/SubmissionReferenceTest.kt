package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.models.CommentSort
import net.dean.jraw.models.KindConstants
import net.dean.jraw.models.VoteDirection
import net.dean.jraw.references.CommentsRequest
import net.dean.jraw.references.SubmissionReference
import net.dean.jraw.test.SharedObjects
import net.dean.jraw.test.TestConfig.reddit
import net.dean.jraw.test.assume
import net.dean.jraw.test.expectDescendingScore
import net.dean.jraw.test.ignoreRateLimit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class SubmissionReferenceTest : Spek({
    val ref: SubmissionReference = reddit.submission("65eeke")
    val refWithComments = reddit.submission("92dd8")

    describe("upvote/downvote/unvote") {
        it("should have an effect on a model") {
            fun expectVote(dir: VoteDirection) {
                ref.inspect().vote.should.equal(dir)
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
                ref.inspect().isSaved.should.equal(saved)
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
            ignoreRateLimit {
                val comment = reddit.submission(submissionId).reply(text)
                comment.body.should.equal(text)
                comment.submissionFullName.should.equal(KindConstants.SUBMISSION + "_$submissionId")
            }
        }
    }

    describe("edit") {
        assume({ SharedObjects.submittedSelfPost != null }, description = "should update the self-post text") {
            SharedObjects.submittedSelfPost!!.edit("Updated at ${Date()}")
        }
    }

    // This test must go last, since deleting the post will make it uneditable
    describe("delete") {
        assume({ SharedObjects.submittedSelfPost != null }, "should delete the submission") {
            SharedObjects.submittedSelfPost!!.delete()
            SharedObjects.submittedSelfPost!!.inspect().author.should.equal("[deleted]")
        }
    }

    describe("hide/unhide") {
        it("should hide/unhide the submission") {
            ref.hide()
            ref.inspect().isHidden.should.be.`true`
            ref.unhide()
            ref.inspect().isHidden.should.be.`false`
        }
    }

    describe("comments") {
        it("should request comments for the given submission") {
            val root = refWithComments.comments()
            root.subject.id.should.equal(refWithComments.id)
            root.replies.should.have.size.above(0)
        }

        it("should recognize the 'limit' and 'sort' parameters") {
            val limit = 5

            // GET: https://www.reddit.com/comments/92dd8?depth=1&limit=5&sort=top&sr_detail=false
            val comments = refWithComments.comments(CommentsRequest.Builder()
                .limit(limit)
                .sort(CommentSort.TOP)
                .depth(1)
                .build())
            comments.replies.should.have.size(limit)

            // We wanted comments sorted by the most votes, make sure we've requested it properly
            expectDescendingScore(comments.replies.map { it.subject })

            // We only requested a few comments, there should be a ton still left contained in the moreChildren object
            comments.moreChildren.should.not.be.`null`
            comments.moreChildren!!.parentFullName.should.equal(comments.subject.fullName)

            // Specifying depth=1 in the request will make reddit only return top level replies. Each MoreChildren
            // should be a thread continuation instead of a normal MoreChildren.
            for (reply in comments.replies) {
                reply.moreChildren.should.not.be.`null`
                reply.moreChildren!!.isThreadContinuation.should.be.`true`
            }
        }

        it("should recognize the 'context' and 'focus' parameters") {
            val commentId = "c0b6zmq"

            // GET: https://www.reddit.com/comments/92dd8?comment=c0b6zmq&context=1&sr_detail=false
            val comments = refWithComments.comments(CommentsRequest.Builder()
                .focus(commentId)
                .context(1)
                .build())

            // Only one top level reply since we specified a comment. This comment has a depth of 3, but since we
            // specified a context of 1, that comment should only have 1 parent.
            comments.replies.should.have.size(1)
            comments.replies[0].replies[0].subject.id.should.equal(commentId)
        }
    }

    describe("sendReplies") {
        assume({ SharedObjects.submittedSelfPost != null }, description = "should complete successfully") {
            SharedObjects.submittedSelfPost!!.sendReplies(true)
        }
    }
})
