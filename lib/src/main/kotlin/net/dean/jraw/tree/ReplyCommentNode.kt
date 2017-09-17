package net.dean.jraw.tree

import net.dean.jraw.models.Comment
import net.dean.jraw.models.MoreChildren

class ReplyCommentNode(
    depth: Int,
    moreChildren: MoreChildren? = null,
    comment: Comment,
    settings: CommentTreeSettings,
    override val parent: CommentNode<*>
) : AbstractCommentNode<Comment>(depth, moreChildren, comment, settings) {

    init {
        initReplies(comment.replies)
    }
}
