package net.dean.jraw.tree

/**
 * Simple tree traversal interface
 */
interface TreeTraverser {
    /** Generates a List of comments organized by the given traversal order */
    fun traverse(order: Order): List<CommentNode<*>>

    /** How to iterate a tree. See [CommentNode] class docs for more info. */
    enum class Order {
        /** */
        PRE_ORDER,

        /** */
        POST_ORDER,

        /** */
        BREADTH_FIRST
    }
}

