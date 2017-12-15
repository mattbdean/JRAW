package net.dean.jraw.tree

import net.dean.jraw.models.CommentSort

/**
 * A data class describing how a comment tree was initially requested and how future requests for more children will be
 * created
 */
data class CommentTreeSettings(
    /** ID (not fullname) of the submission */
    val submissionId: String,

    /** How comments should be sorted */
    val sort: CommentSort
)
