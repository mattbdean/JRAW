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
            out.println("  ".repeat(node.depth - relativeRootDepth) + "${subj.author} (${subj.score}↑): ${subj.body?.replace("\n", "")}")
        }
    }

    override fun walkTree(order: TreeTraverser.Order): List<CommentNode<*>> =
        IterativeTreeTraverser(this).traverse(order)
}
