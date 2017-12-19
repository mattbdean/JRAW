package net.dean.jraw.tree

import java.util.*
import kotlin.coroutines.experimental.buildSequence

object TreeTraverser {
    fun traverse(root: CommentNode<*>, order: TreeTraversalOrder): Sequence<CommentNode<*>> {
        return when (order) {
            TreeTraversalOrder.PRE_ORDER -> preOrder(root)
            TreeTraversalOrder.POST_ORDER -> postOrder(root)
            TreeTraversalOrder.BREADTH_FIRST -> breadthFirst(root)
        }
    }

    private fun preOrder(base: CommentNode<*>): Sequence<CommentNode<*>> = buildSequence {
        val stack = ArrayDeque<CommentNode<*>>()
        stack.add(base)

        var root: CommentNode<*>
        while (!stack.isEmpty()) {
            root = stack.pop()
            yield(root)

            if (root.replies.isNotEmpty()) {
                for (i in root.replies.size - 1 downTo 0) {
                    stack.push(root.replies[i])
                }
            }
        }
    }

    private fun postOrder(base: CommentNode<*>): Sequence<CommentNode<*>> = buildSequence {
        // Post-order traversal isn't going to be as fast as the other methods, this traversal method discovers elements
        // in reverse order and sorts them using a stack. Instead of finding the next node and yielding it, we find the
        // entire sequence and yield all elements right then and there
        val unvisited = ArrayDeque<CommentNode<*>>()
        val visited = ArrayDeque<CommentNode<*>>()
        unvisited.add(base)
        var root: CommentNode<*>

        while (unvisited.isNotEmpty()) {
            root = unvisited.pop()
            visited.push(root)

            if (root.replies.isNotEmpty()) {
                for (reply in root.replies) {
                    unvisited.push(reply)
                }
            }
        }

        yieldAll(visited)
    }

    private fun breadthFirst(base: CommentNode<*>): Sequence<CommentNode<*>> = buildSequence {
        val queue = ArrayDeque<CommentNode<*>>()
        var node: CommentNode<*>

        queue.add(base)

        while (queue.isNotEmpty()) {
            node = queue.remove()
            yield(node)

            queue += node.replies
        }
    }
}
