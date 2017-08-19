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
    override val settings: CommentTreeSettings,
    override val parent: CommentNode<*>,
    repliesNode: JsonNode? = null
) : AbstractCommentNode<Comment>() {
    override val replies: MutableList<ReplyCommentNode> = ArrayList()

    init {
        if (repliesNode != null) {
            // For whatever reason some engineer at reddit thought it would be a good idea to make replies an empty
            // string if there are no replies instead of a zero-length array like a sane person.
            this.replies.addAll(if (repliesNode.isTextual && repliesNode.textValue() == "") {
                mutableListOf()
            } else {
                // Find the Listing children
                val childrenNode = repliesNode.get("data").get("children") ?:
                    throw IllegalArgumentException("Unexpected JSON structure")
                // Make sure reddit is throwing us the right data
                if (!childrenNode.isArray) throw IllegalArgumentException("Expected children node to be an array")

                val (comments, moreChildren) = parseReplies(childrenNode)
                this._moreChildren = moreChildren

                // Map each JsonNode in the array to a CommentNode. Avoid using map() since we'd have to use toMutableMap()
                // which would involve copying the whole list
                val list = mutableListOf<ReplyCommentNode>()
                for (commentJson in comments) {
                    val comment: Comment = JrawUtils.jackson.treeToValue(commentJson)
                    list.add(ReplyCommentNode(comment, depth + 1, settings, this, commentJson["data"]["replies"]))
                }

                /*return*/ list
            })
        }
    }

    private fun parseReplies(arrayNode: JsonNode): Pair<List<JsonNode>, MoreChildren?> {
        val (comments, mores) = arrayNode.partition { it["kind"].asText() == KindConstants.COMMENT }
        if (mores.size > 1)
            throw IllegalStateException("Found more than one MoreChildren")

        val moreChildren: MoreChildren? = if (mores.isEmpty()) null else JrawUtils.jackson.treeToValue(mores[0])

        return comments to moreChildren
    }

    override fun toString(): String {
        return "ReplyCommentNode(subject=$subject, depth=$depth, moreChildren=$moreChildren, replies=$replies)"
    }
}
