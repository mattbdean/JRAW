package net.dean.jraw.util

import net.dean.jraw.models.CommentNode

interface TreeTraverser {
    fun traverse(order: Order): List<CommentNode<*>>

    enum class Order {
        PRE_ORDER,
        POST_ORDER,
        BREADTH_FIRST
    }
}

