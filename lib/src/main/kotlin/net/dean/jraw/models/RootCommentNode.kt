package net.dean.jraw.models

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import net.dean.jraw.JrawUtils
import net.dean.jraw.references.CommentsRequest

/**
 * A RootCommentNode represents the Submission where comments are hosted.
 *
 * All CommentNodes in [replies] are instances of [ReplyCommentNode]. Creating one instance of a RootCommentNode
 * recursively creates a CommentNode for each Comment in the JSON structure. A RootCommentNode's depth is always 0 (see
 * [CommentNode] documentation for an explanation).
 */
class RootCommentNode internal constructor(
    rootArrayNode: JsonNode,
    settings: CommentTreeSettings? = null
) : AbstractCommentNode<Submission>() {
    override val depth: Int = 0
    override val replies: MutableList<ReplyCommentNode>
    override val subject: Submission
    override val settings: CommentTreeSettings

    override val parent: CommentNode<*>
        get() = throw IllegalStateException("A RootCommentNode has no parent node")

    init {
        val (submissionNode, baseCommentNode) = validateRootNodeShape(rootArrayNode)
        // Parse the Submission as normal
        this.subject = JrawUtils.jackson.treeToValue(submissionNode)

        // If no settings are passed, use the ID from the parsed Submission and the default CommentSort to create it.
        // This only happens when we request a random Submission and consequently cannot know the ID until after the
        // request is complete.
        this.settings = settings ?: CommentTreeSettings(submissionId = subject.id, sort = CommentsRequest.DEFAULT_COMMENT_SORT)

        var moreChildrenNode: JsonNode? = null

        // Filter out non-Comment nodes while also discovering a MoreChildren object, if it exists
        val childrenNodes: List<JsonNode> = baseCommentNode.filter {
            val kind = it["kind"].textValue()
            if (kind == KindConstants.MORE_CHILDREN) moreChildrenNode = it

            it["kind"].textValue() == KindConstants.COMMENT
        }

        replies = childrenNodes.map {
            val comment = JrawUtils.jackson.treeToValue<Comment>(it)
            ReplyCommentNode(comment, depth + 1, this.settings, this, it["data"]["replies"])
        }.toMutableList()

        // Parse the MoreChildren if available
        _moreChildren = if (moreChildrenNode == null)
            null
        else
            JrawUtils.jackson.treeToValue<MoreChildren>(moreChildrenNode!!)
    }

    override fun toString(): String =
        "RootCommentNode(depth=$depth, replies=List(size=${replies.size}), moreChildren=$moreChildren, submission=${subject.fullName})"

    companion object {
        /**
         * Validates that the root JsonNode is an array of size 2. Returns the Submission root node (the one that
         * contains `data` and `kind` for the Submission in question) paired to the JsonNode for the children Listing
         * (the one with `data` and `kind`).
         */
        private fun validateRootNodeShape(root: JsonNode): Pair<JsonNode, JsonNode> {
            if (!root.isArray) throw IllegalArgumentException("expecting root node to be an array")
            if (root.size() != 2) throw IllegalArgumentException("expecting root node to have size of 2")
            val submissionNode = root.get(0)?.get("data")?.get("children")?.get(0) ?:
                throw IllegalArgumentException("Unexpected JSON structure: no submission")

            val baseCommentNode = root.get(1)?.get("data")?.get("children") ?:
                throw IllegalArgumentException("Unexpected JSON structure: no comments")

            return submissionNode to baseCommentNode
        }
    }
}
