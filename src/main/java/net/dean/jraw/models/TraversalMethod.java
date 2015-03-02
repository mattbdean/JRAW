package net.dean.jraw.models;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;

/**
 * This class represents different ways a tree structure (such as a Reddit comment tree) can be traversed.
 */
public enum TraversalMethod {
    /** Each node's subtrees are traversed after the node itself is returned. */
    PRE_ORDER {
        @Override
        public FluentIterable<CommentNode> provideIterable(TreeTraverser<CommentNode> traverser, CommentNode root) {
            FluentIterable<CommentNode> it = traverser.preOrderTraversal(root);
            if (isRootComment(root))
                // Skip root node, which is accessed first
                it = it.skip(1);
            return it;
        }
    },
    /** Iterates through each node's subtrees before iterating the root */
    POST_ORDER {
        @Override
        public FluentIterable<CommentNode> provideIterable(TreeTraverser<CommentNode> traverser, CommentNode root) {
            FluentIterable<CommentNode> it = traverser.postOrderTraversal(root);
            if (isRootComment(root))
                // Skip root node, which is accessed last
                it = it.limit(root.getTotalSize());
            return it;
        }
    },
    /** Iterates through all nodes in depth 1, then depth 2, etc. */
    BREADTH_FIRST {
        @Override
        public FluentIterable<CommentNode> provideIterable(TreeTraverser<CommentNode> traverser, CommentNode root) {
            FluentIterable<CommentNode> it = traverser.breadthFirstTraversal(root);
            if (isRootComment(root))
                // Skip root node, which is accessed first
                return it.skip(1);
            return it;
        }
    };

    private static boolean isRootComment(CommentNode node) {
        return node.getComment() instanceof CommentNode.RootComment;
    }

    abstract FluentIterable<CommentNode> provideIterable(TreeTraverser<CommentNode> traverser, CommentNode root);
}
