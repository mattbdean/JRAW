package net.dean.jraw.util

//class IterativeTreeTraverser(val root: CommentNode<*>) : TreeTraverser {
//    override fun traverse(order: TreeTraverser.Order): List<CommentNode<*>> {
//        return when (order) {
//            TreeTraverser.Order.PRE_ORDER -> preOrder()
//            TreeTraverser.Order.POST_ORDER -> postOrder()
//            TreeTraverser.Order.BREADTH_FIRST -> breadthFirst()
//        }
//    }
//
//    private fun preOrder(): List<CommentNode<*>> {
//        val result = ArrayList<CommentNode<*>>()
//
//        val stack = ArrayDeque<CommentNode<*>>()
//        stack.add(root)
//
//        var root: CommentNode<*>
//        while (!stack.isEmpty()) {
//            root = stack.pop()
//            result.add(root)
//
//            if (root.replies.isNotEmpty()) {
//                for (i in root.replies.size - 1 downTo 0) {
//                    stack.push(root.replies[i])
//                }
//            }
//        }
//
//        return result
//    }
//
//    private fun postOrder(): List<CommentNode<*>> {
//        val unvisited = ArrayDeque<CommentNode<*>>()
//        val visited = ArrayDeque<CommentNode<*>>()
//        unvisited.add(root)
//        var root: CommentNode<*>
//
//        while (unvisited.isNotEmpty()) {
//            root = unvisited.pop()
//            visited.push(root)
//
//            if (root.replies.isNotEmpty()) {
//                for (reply in root.replies) {
//                    unvisited.push(reply)
//                }
//            }
//        }
//
//        return visited.toList()
//    }
//
//    private fun breadthFirst(): List<CommentNode<*>> {
//        val queue = ArrayDeque<CommentNode<*>>()
//        val result = ArrayList<CommentNode<*>>()
//        var node: CommentNode<*>
//
//        queue.add(root)
//
//        while (queue.isNotEmpty()) {
//            node = queue.remove()
//            result.add(node)
//
//            queue += node.replies
//        }
//
//        return result
//    }
//}
