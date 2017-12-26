package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.models.Comment
import net.dean.jraw.models.DistinguishedStatus
import net.dean.jraw.test.SharedObjects
import net.dean.jraw.test.TestConfig.reddit
import net.dean.jraw.test.assume
import net.dean.jraw.test.expectException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class CommentReferenceTest : Spek({

    describe("createAndSticky") {
        assume({ SharedObjects.submittedSelfPost != null }, description = "should have a self-post created") {}

        val comment = SharedObjects.submittedSelfPost!!.reply("Test comment on ${Date()}")
        val commentReference = comment.toReference(reddit)

        comment.distinguished.should.equal(DistinguishedStatus.NORMAL)
        comment.isStickied.should.be.`false`

        it("should sticky the created comment") {
            commentReference.distinguish(DistinguishedStatus.MODERATOR, true)
        }
        it("should have an effect on the model") {
            val distinguishedComment = reddit.lookup(commentReference.fullName).filterIsInstance<Comment>().first()
            distinguishedComment.distinguished.should.equal(DistinguishedStatus.MODERATOR)
            distinguishedComment.isStickied.should.be.`true`
        }
        it("should fail preemptively when trying to sticky with normal status") {
            expectException(IllegalArgumentException::class) {
                commentReference.distinguish(DistinguishedStatus.NORMAL, true)
            }
        }
    }
})
