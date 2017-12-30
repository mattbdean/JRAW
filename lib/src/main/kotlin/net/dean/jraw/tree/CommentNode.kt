package net.dean.jraw.tree

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.RedditClient
import net.dean.jraw.http.LogAdapter
import net.dean.jraw.http.PrintStreamLogAdapter
import net.dean.jraw.models.Comment
import net.dean.jraw.models.MoreChildren
import net.dean.jraw.models.PublicContribution

/**
 * This class represents one comment in a comment tree.
 *
 * Each CommentNode has a depth, a [Comment] instance, and zero or more CommentNode children. Depth is defined as how
 * many parents the comment has. For example, a top-level reply has a depth of 1 (the Submission where it was posted is
 * a top-level comment's parent), a reply to that comment has a depth of 2, and so on.
 *
 * At the root of every comment tree there is a single root node, which represents the submission the comments belong
 * to. This node will have a depth of 0 and its children will be top-level replies to the submission. Although this node
 * is technically part of the tree, it will not be included when iterating the nodes with [walkTree].
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
 * different methods: Pre-order (`abcfghdi`), post-order (`bfghcida`), and breadth-first (`abcdfghi`). For reference,
 * reddit uses pre-order traversal to display comments on the website.
 *
 * Note that although this class implements Iterable, the provided Iterator will only iterate through direct
 * children. To walk the entire tree, use [walkTree].
 *
 * @author Matthew Dean
 */
interface CommentNode<out T : PublicContribution<*>> : Iterable<CommentNode<*>> {
    /**
     * How many nodes have this node as a child (directly or indirectly). For example, A top level reply has a depth of
     * 1, and a reply to that has a depth of 2.
     */
    val depth: Int

    /** This node's direct children */
    val replies: MutableList<CommentNode<Comment>>

    /**
     * A nullable object representing comments that couldn't be included in the response because it was already too big.
     *
     * @see hasMoreChildren
     */
    val moreChildren: MoreChildren?

    /** The PublicContribution this CommentNode was created for */
    val subject: T

    /**
     * The settings that was used to create this node and the that will be used in all future requests for more children
     */
    val settings: CommentTreeSettings

    /** The node directly above this one. Throws an IllegalStateException when trying to access the root node's parent. */
    val parent: CommentNode<*>

    /** Tests if this CommentNode has more children available to load */
    fun hasMoreChildren(): Boolean

    /**
     * Walks the comment tree using pre-order traversal. This is how reddit generates the comment section on the
     * website.
     */
    fun walkTree() = walkTree(TreeTraversalOrder.PRE_ORDER)

    /**
     * Organizes this comment tree into a Sequence whose order is determined by the given [TreeTraversalOrder]. For example,
     * reddit uses pre-order traversal to generate the website's comments section.
     */
    fun walkTree(order: TreeTraversalOrder = TreeTraversalOrder.PRE_ORDER): Sequence<CommentNode<PublicContribution<*>>>

    /** Prints out a brief overview of this CommentNode and its children to stdout */
    fun visualize() = visualize(PrintStreamLogAdapter())

    /**
     * Prints out a brief overview of this CommentNode and its children to the given PrintStream (defaults to stdout).
     */
    fun visualize(out: LogAdapter = PrintStreamLogAdapter())

    /** Returns the amount of direct and indirect children this node has. */
    fun totalSize(): Int

    /**
     * If [moreChildren] is not null, fetches up to 100 more child comments. Does not modify the tree. To have these new
     * nodes inserted into the tree, use [replaceMore] instead.
     */
    @EndpointImplementation(Endpoint.GET_MORECHILDREN)
    fun loadMore(reddit: RedditClient): CommentNode<T>

    /**
     * If [moreChildren] is not null, fetches up to 100 more child comments and inserts them into the current tree.
     * Returns all new direct children.
     */
    fun replaceMore(reddit: RedditClient): List<CommentNode<*>>

    /**
     * Fully expands the comment tree below this node by finding all [MoreChildren] objects belonging to and below
     * this node and loading them into the tree using [replaceMore]. Be aware that without
     * setting a depth or request limit this may be a very costly operation, requiring potentially hundreds of requests
     * to fully satisfy the method call. Note that one request must be sent for every [MoreChildren] found.
     *
     * @param depthLimit The maximum depth to look into. A value of [-1] will disable the limit.
     * @param requestLimit The maximum amount of requests to send. There will be one request for every MoreChildren
     * object found. A value of [-1] will disable the limit.
     */
    fun loadFully(reddit: RedditClient, depthLimit: Int = NO_LIMIT, requestLimit: Int = NO_LIMIT)

    /**
    * Fully expands the comment tree below this node. This can be a very expensive call depending on how large the
    * thread is, as every [MoreChildren] requires its own HTTP request. It is therefore advised to use
    * overloaded [loadFully] with depthLimit and/or requestLimit instead to restrict the number of HTTP requests sent.
    */
    fun loadFully(reddit: RedditClient) = loadFully(reddit, NO_LIMIT, NO_LIMIT)

    companion object {
        const val NO_LIMIT = -1
    }
}
