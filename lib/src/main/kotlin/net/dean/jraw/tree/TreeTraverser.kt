package net.dean.jraw.tree

interface TreeTraverser {
    fun traverse(order: Order): List<CommentNode<*>>

    enum class Order {
        PRE_ORDER,
        POST_ORDER,
        BREADTH_FIRST
    }
}

