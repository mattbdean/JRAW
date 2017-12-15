package net.dean.jraw.references

import net.dean.jraw.models.CommentSort
import net.dean.jraw.references.CommentsRequest.Companion.DEFAULT_COMMENT_SORT

/**
 * Simple data class to model the parameters of fetching comments for a submission.
 */
data class CommentsRequest(
    /**
     * Sets the ID of the comment to focus on. If this comment does not exist, then this parameter is ignored.
     * Otherwise, only one comment tree is returned: the one in which the given comment resides.
     */
    val focus: String? = null,

    /**
     * Sets the number of parents shown in relation to the focused comment. For example, if the focused comment is
     * in the eighth level of the comment tree (meaning there are seven replies above it), and the context is set to
     * six, then the response will also contain the six direct parents of the given comment. For a better
     * understanding, play with [this link](https://www.reddit.com/comments/92dd8?comment=c0b73aj&context=8)
     *
     * A null value will exclude this parameter.
     */
    val context: Int? = null,

    /**
     * Sets the maximum amount of subtrees returned by this request. If the number is less than 1, it is ignored by
     * the reddit API and no depth restriction is enacted. A null value will exclude this parameter.
     */
    val depth: Int? = null,

    /** Sets the maximum amount of comments to return. A null value will exclude this parameter. */
    val limit: Int? = null,

    /** How the returned comments will be sorted. Defaults to [DEFAULT_COMMENT_SORT]. */
    val sort: CommentSort = DEFAULT_COMMENT_SORT
) {
    private constructor(b: Builder): this(b.focus, b.context, b.depth, b.limit, b.sort)

    /** Builder pattern implementation for the [CommentsRequest] class */
    class Builder {
        internal var focus: String? = null
        internal var context: Int? = null
        internal var depth: Int? = null
        internal var sort: CommentSort = DEFAULT_COMMENT_SORT
        internal var limit: Int? = null

        /** See [CommentsRequest.focus] */
        fun focus(commentId: String?): Builder { this.focus = commentId; return this }

        /** See [CommentsRequest.context] */
        fun context(context: Int?): Builder { this.context = context; return this }

        /** See [CommentsRequest.depth] */
        fun depth(depth: Int?): Builder { this.depth = depth; return this }

        /** See [CommentsRequest.sort] */
        fun sort(sort: CommentSort): Builder { this.sort = sort; return this }

        /** See [CommentsRequest.limit] */
        fun limit(limit: Int?): Builder { this.limit = limit; return this }

        /** Creates a CommentsRequest object */
        fun build() = CommentsRequest(this)
    }

    /** */
    companion object {
        /** The default CommentSort used by both reddit and this class. Equal to [CommentSort.CONFIDENCE] */
        @JvmField val DEFAULT_COMMENT_SORT = CommentSort.CONFIDENCE
    }
}
