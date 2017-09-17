package net.dean.jraw.tree

import net.dean.jraw.models.PublicContribution

class FakeRootCommentNode<out T : PublicContribution<*>>(depth: Int, subject: T, settings: CommentTreeSettings) : AbstractCommentNode<T>(
    depth = depth,
    moreChildren = null,
    subject = subject,
    settings = settings
) {
    override val parent: CommentNode<*>
        get() = throw IllegalArgumentException("Fake root nodes have no parent node")
}
