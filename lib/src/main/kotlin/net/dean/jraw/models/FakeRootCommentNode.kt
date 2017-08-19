package net.dean.jraw.models

class FakeRootCommentNode<out T : PublicContribution<*>> internal constructor(
    override val depth: Int,
    override val settings: CommentTreeSettings,
    override val subject: T
) : AbstractCommentNode<T>() {
    override val parent: CommentNode<*>
        get() = throw IllegalStateException("Fake root comment nodes have no parent")
    override val replies: MutableList<ReplyCommentNode> = ArrayList()
}
