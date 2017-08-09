package net.dean.jraw.models

import net.dean.jraw.util.IterativeTreeTraverser
import net.dean.jraw.util.TreeTraverser
import java.io.PrintStream

/**
 * This class represents one comment in a comment tree.
 *
 * Each CommentNode has a depth, a [Comment] instance, and zero or more CommentNode children. Depth is defined as how
 * many parents the comment has. For example, a top-level reply has a depth of 1 (the Submission where it was posted is
 * a top-level comment's parent), a reply to that comment has a depth of 2, and so on.
 *
 * At the root of every comment tree there is a single root node, which represents the submission the comments belong
 * to. This node will have a depth of 0 and its children will be top-level replies to the submission. Although this node
 * is technically part of the tree, it will not be included when iterating the nodes with [traverser].
 *
 * For example, take this tree structure:
 *
 * ```
 *       z
 *       |
 *       a
 *     / | \
 *    b  c  d
 *       |   \
 *     f,g,h  i
 * ```
 *
 * Node `z` represents the root node (submission) with a depth of 0. Node `a` is a top level reply to that submission
 * with a depth of 1. Nodes `f`, `g` and `h`, and `i` have a depth of 3. This tree can be traversed using several
 * different methods: Pre-order (`abcfghdi`), post-order (`bfghcida`), and breadth-first (`abcdfghi`).
 *
 * Note that although this class implements Iterable, the provided Iterator will only iterate through direct
 * children. To walk the entire tree, use [walkTree].
 *
 * @author Matthew Dean
 */
interface CommentNode<out T : PublicContribution<*>> : Iterable<ReplyCommentNode> {
    /**
     * How many nodes have this node as a child (directly or indirectly). For example, A top level reply has a depth of
     * 1, and a reply to that has a depth of 2.
     */
    val depth: Int

    /** This node's direct children */
    val replies: List<ReplyCommentNode>

    /**
     * A nullable object representing comments that couldn't be included in the response because it was already too big.
     *
     * @see hasMoreChildren
     */
    val moreChildren: MoreChildren?

    /** The PublicContribution this CommentNode was created for */
    val subject: T

    /**
     * Tests if this CommentNode has more children. By default, this function returns if the [moreChildren] is not null.
     */
    fun hasMoreChildren(): Boolean = moreChildren != null

    override fun iterator(): Iterator<ReplyCommentNode> = replies.iterator()

    /**
     * Organizes this comment tree into a List whose order is determined by the given [TreeTraverser.Order]. For example,
     * reddit uses pre-order traversal to generate the website's comments section.
     */
    fun walkTree(order: TreeTraverser.Order = TreeTraverser.Order.PRE_ORDER): List<CommentNode<*>> =
        IterativeTreeTraverser(this).traverse(order)

    /**
     * Prints out
     */
    fun visualize(out: PrintStream = System.out) {
        val relativeRootDepth = depth
        for (node in walkTree(TreeTraverser.Order.PRE_ORDER)) {
            val subj = node.subject
            out.println("  ".repeat(node.depth - relativeRootDepth) + "${subj.author} (${subj.score}↑): ${subj.body?.replace("\n", "")}")
        }
    }

    /**
     * Returns the amount of direct and indirect children this node has.
     */
    fun totalSize(): Int {
        // walkTree() goes through this node and all child nodes, but we only care about child nodes
        return walkTree().size - 1
    }

}

