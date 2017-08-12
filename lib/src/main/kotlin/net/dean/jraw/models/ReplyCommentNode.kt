package net.dean.jraw.models

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import net.dean.jraw.JrawUtils

/**
 * Represents any non-root CommentNode.
 *
 * Just like RootCommentNode, creating one instance of this class recursively constructs a CommentNode for each comment
 * in the tree below this node.
 */
class ReplyCommentNode internal constructor(
    /** The data this node represents */
    override val subject: Comment,
    override val depth: Int,
    replies: JsonNode
) : AbstractCommentNode<Comment>() {
    override val moreChildren: MoreChildren?
        get() = _moreChildren
    override val replies: List<ReplyCommentNode>

    private var _moreChildren: MoreChildren? = null

    init {
        // For whatever reason some engineer at reddit thought it would be a good idea to make replies an empty string
        // if there are no replies instead of a zero-length array like a sane person.
        this.replies = if (replies.isTextual && replies.textValue() == "") {
            listOf()
        } else {
            // Find the Listing children
            val childrenNode = replies.get("data").get("children") ?:
                throw IllegalArgumentException("Unexpected JSON structure")
            // Make sure reddit is throwing us the right data
            if (!childrenNode.isArray) throw IllegalArgumentException("Expected children node to be an array")

            val (comments, moreChildren) = childrenNode.partition { it["kind"].asText() == KindConstants.COMMENT }
            if (moreChildren.size > 1)
                throw IllegalStateException("Found more than one MoreChildren")
            _moreChildren = if (moreChildren.isEmpty()) null else JrawUtils.jackson.treeToValue(moreChildren[0])

            // Map each JsonNode in the array to a CommentNode
            comments.map {
                val comment: Comment = JrawUtils.jackson.treeToValue(it)
                ReplyCommentNode(comment, depth + 1, it["data"]["replies"])
            }
        }
    }

    override fun toString(): String {
        return "ReplyCommentNode(subject=$subject, depth=$depth, moreChildren=$moreChildren, replies=$replies)"
    }
}
