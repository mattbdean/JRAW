package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.models.Comment
import net.dean.jraw.models.MoreChildren
import net.dean.jraw.models.Submission
import net.dean.jraw.test.NoopNetworkAdapter
import net.dean.jraw.test.TestConfig.reddit
import net.dean.jraw.test.expectException
import net.dean.jraw.test.newMockRedditClient
import net.dean.jraw.tree.AbstractCommentNode
import net.dean.jraw.tree.CommentNode
import net.dean.jraw.tree.RootCommentNode
import net.dean.jraw.tree.TreeTraverser
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class CommentNodeTest : Spek({
    val SUBMISSION_ID = "2zsyu4"
    val root: RootCommentNode by lazy { reddit.submission(SUBMISSION_ID).comments() }
    val a: CommentNode<Comment> by lazy { root.replies[0] }

    // reddit.com/comments/6t8ioo
    val complexTree: RootCommentNode by lazy { reddit.submission("92dd8").comments() }

    // reddit.com/comments/2onit4
    val simpleTree: RootCommentNode by lazy { reddit.submission("2onit4").comments() }

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

    fun MoreChildren.copy(): MoreChildren {
        return MoreChildren.create(fullName, id, parentFullName, childrenIds)
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

    describe("loadMore") {
        it("should throw an Exception when MoreChildren is null") {
            simpleTree.moreChildren.should.be.`null`

            expectException(IllegalStateException::class) {
                // Make sure we aren't executing any network requests
                val mockReddit = newMockRedditClient(NoopNetworkAdapter)
                simpleTree.loadMore(mockReddit)
            }
        }

        it("should return a new root instead of altering the tree") {
            // Highly unusual to see a popular post without a root MoreChildren
            complexTree.moreChildren.should.not.be.`null`

            val originalMore = complexTree.moreChildren!!.copy()
            var prevCount = complexTree.moreChildren!!.childrenIds.size
            prevCount.should.be.above(0)

            var fakeRoot: CommentNode<Submission> = complexTree

            // Make sure that calling RootCommentNode.loadMore() works exactly the same as calling
            // FakeRootCommentNode.loadMore()
            for (i in 0..2) {
                // This thread has 2000+ comments, we should be able to do a few rounds of this no problem
                fakeRoot.hasMoreChildren().should.be.`true`

                // go deeper...
                fakeRoot = fakeRoot.loadMore(reddit)

                // Make sure the original tree's MoreChildren wasn't altered
                complexTree.moreChildren.should.equal(originalMore)

                // The new root MoreChildren should contain fewer children IDs than it did previously
                fakeRoot.moreChildren!!.childrenIds.should.have.size.below(prevCount)
                // Make sure the new root MoreChildren has the same parent ID
                fakeRoot.moreChildren!!.parentFullName.should.equal(originalMore.parentFullName)
                // We should only be requesting a certain amount of children, and therefore be receiving up to a certain
                // amount of children. The reason we don't assert an exact number is that reddit may only return 97
                // objects if we request 100 for whatever reason
                fakeRoot.replies.should.have.size.at.most(AbstractCommentNode.MORE_CHILDREN_LIMIT)

                prevCount = fakeRoot.moreChildren!!.childrenIds.size
            }
        }

        it("should handle thread continuations") {
            val threadContinuation = simpleTree.walkTree().first { it.hasMoreChildren() && it.moreChildren!!.isThreadContinuation }
            val parentDepth = threadContinuation.depth
            val fakeRoot = threadContinuation.loadMore(reddit)

            val flatTree = fakeRoot.walkTree()

            // The first element in the flat tree should represent the original comment
            flatTree[0].subject.fullName.should.equal(threadContinuation.subject.fullName)
            flatTree[0].depth.should.equal(threadContinuation.depth)

            // All nodes after the first should be children (directly or indirectly)
            for (node in flatTree.drop(1)) {
                node.depth.should.be.above(parentDepth)
            }
        }
    }

    describe("replaceMore") {
        it("should do nothing when MoreChildren is null") {
            simpleTree.moreChildren.should.be.`null`

            val original = simpleTree.walkTree()

            // Use a mock RedditClient so we can assert no network requests are sent
            val fakeReddit = newMockRedditClient(NoopNetworkAdapter)
            simpleTree.replaceMore(fakeReddit)
            simpleTree.walkTree().should.equal(original)
        }

        it("should alter the tree when called") {
            val tree = reddit.submission("92dd8").comments()
            val prevFlatTree = tree.walkTree()
            // Create a copy of the data
            val prevMoreChildren = tree.moreChildren!!.copy()
            val prevSize = tree.totalSize()

            val newDirectChildren = tree.replaceMore(reddit)
            tree.moreChildren!!.parentFullName.should.equal(prevMoreChildren.parentFullName)

            // Make sure we've taken IDs out of the MoreChildren and added them to the tree
            tree.moreChildren!!.childrenIds.should.have.size.below(prevMoreChildren.childrenIds.size)
            tree.totalSize().should.be.above(prevSize)

            for (directChild in newDirectChildren) {
                // Make sure each reportedly new direct children are in fact:
                // (1) direct children and
                directChild.subject.parentFullName.should.equal(tree.subject.fullName)
                // (2) new children
                prevFlatTree.find { it.subject.fullName == directChild.subject.fullName }.should.be.`null`
            }
        }
    }
})
