package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.models.CommentNode
import net.dean.jraw.models.ReplyCommentNode
import net.dean.jraw.models.RootCommentNode
import net.dean.jraw.test.TestConfig
import net.dean.jraw.util.TreeTraverser
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.properties.Delegates

class CommentNodeTest : Spek({
    val SUBMISSION_ID = "2zsyu4"
    var root: RootCommentNode by Delegates.notNull()
    var a: ReplyCommentNode by Delegates.notNull()

    beforeGroup {
        root = TestConfig.reddit.submission(SUBMISSION_ID).comments()
        a = root.replies[0]
    }

    /*
    All tests are based on submission 2zsyu4 that has a comment structure like this:

         a
       / | \
      b  c  d
        /|\  \
       f g h  i

    See reddit.com/comments/2zsyu4 for the actual Submission
     */

    it("should have a submission with the same ID") {
        root.subject.id.should.equal(SUBMISSION_ID)
        root.depth.should.equal(0)
        // Only one top level reply
        root.replies.should.have.size(1)
        root.hasMoreChildren().should.be.`false`

        // Top level replies
        a.depth.should.equal(1)
        a.replies.should.have.size(3)

        // Depth-2 replies
        val b = a.replies[0]
        b.depth.should.equal(2)
        b.replies.should.have.size(0)
        val c = a.replies[1]
        c.depth.should.equal(2)
        c.replies.should.have.size(3)
        val d = a.replies[2]
        d.depth.should.equal(2)
        d.replies.should.have.size(1)

        // Depth-3 replies, only test the first one since the rest are similar (no children)
        val f = c.replies[0]
        f.depth.should.equal(3)
        f.replies.should.have.size(0)
    }

    fun <T> Iterator<T>.toList(): List<T> {
        val values: MutableList<T> = ArrayList()
        while (this.hasNext()) values.add(this.next())
        return values
    }

     fun <T : CommentNode<*>> List<T>.mapToBody(): List<String?> {
         return map { it.subject.body }
     }

    it("should provide an Iterator that iterates over direct children") {
        a.iterator()
            .toList()
            .mapToBody()
            .should.equal(listOf("b", "c", "d"))
    }

    describe("walkTree") {
        it("should correctly iterate pre-order") {
            a.walkTree(TreeTraverser.Order.PRE_ORDER)
                .mapToBody()
                .should.equal("a b c f g h d i".split(" "))
        }

        it("should correctly iterate post-order") {
            a.walkTree(TreeTraverser.Order.POST_ORDER)
                .mapToBody()
                .should.equal("b f g h c i d a".split(" "))
        }

        it("should correctly iterate breadth-first") {
            a.walkTree(TreeTraverser.Order.BREADTH_FIRST)
                .mapToBody()
                .should.equal("a b c d f g h i".split(" "))
        }

        it("should default to pre-order") {
            a.walkTree().mapToBody().should.equal(a.walkTree(TreeTraverser.Order.PRE_ORDER).mapToBody())
        }
    }

    describe("visualize") {
        it("shouldn't throw an Exception") {
            a.visualize()
        }
    }

    describe("totalSize") {
        it("should calculate the correct size of the node's children") {
            root.totalSize().should.equal(8)
            a.totalSize().should.equal(7)

            // Node 'b' is a leaf
            a.replies[0].totalSize().should.equal(0)
        }
    }
})
