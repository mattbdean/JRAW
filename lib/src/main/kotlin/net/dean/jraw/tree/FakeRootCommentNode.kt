package net.dean.jraw.tree

import net.dean.jraw.models.PublicContribution

/**
 * A FakeRootCommentNode is returned as the result of [CommentNode.loadMore]. It's "fake" because in "real" comment tree
 * structures, the submission is the root node. Here, a comment is the root node.
 */
class FakeRootCommentNode<out T : PublicContribution<*>>(depth: Int, subject: T, settings: CommentTreeSettings) : AbstractCommentNode<T>(
    depth = depth,
    moreChildren = null,
    subject = subject,
    settings = settings
) {
    override val parent: CommentNode<*>
        get() = throw IllegalArgumentException("Fake root nodes have no parent node")
}
