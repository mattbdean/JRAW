package net.dean.jraw.tree

import net.dean.jraw.models.Listing
import net.dean.jraw.models.NestedIdentifiable
import net.dean.jraw.models.Submission
import net.dean.jraw.references.CommentsRequest

class RootCommentNode(submission: Submission, replies: Listing<NestedIdentifiable>, settings: CommentTreeSettings?) : AbstractCommentNode<Submission>(
    depth = 0,
    moreChildren = null,
    subject = submission,
    settings = settings ?: CommentTreeSettings(submission.id, CommentsRequest.DEFAULT_COMMENT_SORT)
) {
    override val parent: CommentNode<*>
        get() = throw IllegalArgumentException("Root nodes have no parent node")

    override val depth: Int = 0

    init {
        initReplies(replies)
    }
}
