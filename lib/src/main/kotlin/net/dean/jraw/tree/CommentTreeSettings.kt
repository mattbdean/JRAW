package net.dean.jraw.tree

import net.dean.jraw.models.CommentSort

data class CommentTreeSettings(
    val submissionId: String,
    val sort: CommentSort
)
