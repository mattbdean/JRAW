package net.dean.jraw.models

import net.dean.jraw.util.IterativeTreeTraverser
import net.dean.jraw.util.TreeTraverser
import java.io.PrintStream

abstract class AbstractCommentNode<out T : PublicContribution<*>> : CommentNode<T> {
    override fun hasMoreChildren(): Boolean = moreChildren != null

    override fun iterator(): Iterator<ReplyCommentNode> = replies.iterator()

    override fun totalSize(): Int {
        // walkTree() goes through this node and all child nodes, but we only care about child nodes
        return walkTree().size - 1
    }

    override fun visualize(out: PrintStream) {
        val relativeRootDepth = depth
        for (node in walkTree(TreeTraverser.Order.PRE_ORDER)) {
            val subj = node.subject
            val indent = "  ".repeat(node.depth - relativeRootDepth)

            // Use the submission URL if it's not a self post, otherwise just use the comment/submission body
            val body = if (subj is Submission && !subj.isSelfPost) subj.url else subj.body?.replace("\n", "\\n")
            out.println(indent + "${subj.author} (${subj.score}↑): $body")
        }
    }

    override fun walkTree(order: TreeTraverser.Order): List<CommentNode<*>> =
        IterativeTreeTraverser(this).traverse(order)
}
