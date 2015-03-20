package net.dean.jraw.test;

import com.google.common.collect.ImmutableMap;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.CommentSort;
import net.dean.jraw.models.FauxListing;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.LocationHint;
import net.dean.jraw.models.TraversalMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;

public class CommentNodeTest extends RedditTest {
    private final CommentNode simpleTree;
    private final int simpleTreeSize = 8;

    public CommentNodeTest() {
        super();
        this.simpleTree = initCommentTree();
    }


    private CommentNode initCommentTree() {
        /*
        Generate a tree like so:

                 z
                 |
                 a
                /|\
               b c d
                 |  \
               f,g,h i

        Where 'z' is the inferred root node (the parent submission) and 'a' is the first and only top level reply
         */

        // Brace yourselves
        List<Comment> cChildrenList = new ArrayList<>();
        // Add instances of MockComment, a class that overrides the getBody() function of the Comment class to return
        // the String given in the constructor instead. This is used for quick identification of the comment and as a
        // key in the expectedDepths map.

        // Create children of 'c'
        cChildrenList.add(new MockComment("f"));
        cChildrenList.add(new MockComment("g"));
        cChildrenList.add(new MockComment("h"));
        Listing<Comment> cChildren = new FauxListing<>(cChildrenList, null, null, null);

        // Create children of 'd'
        List<Comment> dChildrenList = new ArrayList<>();
        dChildrenList.add(new MockComment("i"));
        Listing<Comment> dChildren = new FauxListing<>(dChildrenList, null, null, null);

        // Create children of 'a'
        List<Comment> aChildrenList = new ArrayList<>();
        aChildrenList.add(new MockComment("b"));
        aChildrenList.add(new MockComment("c", cChildren));
        aChildrenList.add(new MockComment("d", dChildren));

        // Create top level children, which only contains 'a'
        List<Comment> topLevelList = new ArrayList<>();
        topLevelList.add(new MockComment("a", new FauxListing<>(aChildrenList, null, null)));

        // Create and test the expected depths
        return new CommentNode("t3_ownerId", topLevelList, null, CommentSort.TOP);
    }

    @Test
    public void testDepth() {
        Map<String, Integer> expectedDepths = ImmutableMap.<String, Integer>builder()
                .put("a", 1)
                .put("b", 2)
                .put("c", 2)
                .put("d", 2)
                .put("f", 3)
                .put("g", 3)
                .put("h", 3)
                .put("i", 3)
                .build();

        for (CommentNode n : simpleTree.walkTree()) {
            // Ensure the calculated depth matches the expected depth
            assertEquals(n.getDepth(), (int) expectedDepths.get(n.getComment().getBody()),
                    "Node not at expected depth (node=" + n.getComment().getBody() + ")");
        }
    }

    @Test
    public void testLoadMoreComments() {
        try {
            CommentNode node = reddit.getSubmission("92dd8").getComments();
            node.loadMoreComments(reddit);
        } catch (NetworkException e) {
            handle(e);
        }
    }

    @Test
    public void testVisualize() {
        // Nothing we can really test here besides it if throws an exception
        simpleTree.visualize();
    }

    @Test
    public void testFindChild() {
        assertTrue(simpleTree.findChild("c").isPresent());
    }

    @Test
    public void testFindChildMoreMethods() {
        for (TraversalMethod method : TraversalMethod.values()) {
            assertTrue(simpleTree.findChild("c", LocationHint.of(method)).isPresent());
        }
    }

    @Test
    public void testTopLevel() {
        CommentNode node = simpleTree;
        // The first element in a pre-order traversal is logically a root node
        assertTrue(node.walkTree(TraversalMethod.PRE_ORDER).first().get().isTopLevel());

        // Likewise the first element in a post-order traversal should *not* be a root node
        assertFalse(node.walkTree(TraversalMethod.POST_ORDER).first().get().isTopLevel());
    }

    @Test
    public void testWalkPreorder() {
        testWalkTree(TraversalMethod.PRE_ORDER);
    }

    @Test
    public void testWalkPostorder() {
        testWalkTree(TraversalMethod.POST_ORDER);
    }
    @Test
    public void testWalkBreadthFirst() {
        testWalkTree(TraversalMethod.BREADTH_FIRST);
    }

    private void testWalkTree(TraversalMethod method) {
        // Test size from root
        assertEquals(calculateTotal(simpleTree, method), simpleTreeSize);

        // Test size from some other node
        CommentNode node = simpleTree.get(0).get(1); // Node 'c' in the tree
        int expected = 3 + 1; // Children size + parent node
        assertEquals(calculateTotal(node, method), expected);

    }

    private int calculateTotal(CommentNode root, TraversalMethod method) {
        return root.walkTree(method).size();
    }

    @Test
    public void testTotalSize() {
        assertEquals(simpleTree.getTotalSize(), simpleTreeSize);
    }

    @Test
    public void testImmediateSize() {
        // Test size of node 'a'
        assertEquals(simpleTree.get(0).getImmediateSize(), 3);
    }

    @Test
    public void testLoadFully() {
        CommentNode node = reddit.getSubmission("2kx1ly").getComments();
        node.loadFully(reddit);

        for (CommentNode child : node.walkTree()) {
            assertFalse(child.hasMoreComments(), "Child had more comments: " + child);
        }
        assertFalse(node.hasMoreComments(), "Root node had more comments: " + node);
    }

    @Test
    public void testContinueThread() {
        CommentNode node = reddit.getSubmission("2onit4").getComments();
        node.loadFully(reddit);
        CommentNode subject = null;
        for (CommentNode n : node.walkTree()) {
            if (n.isThreadContinuation()) {
                subject = n;
                break;
            }
        }

        if (subject == null)
            throw new IllegalStateException("Could not find a node that is a thread continuation");

        subject.loadMoreComments(reddit);
        assertFalse(subject.hasMoreComments());
        assertFalse(subject.isThreadContinuation());
        assertTrue(subject.getImmediateSize() > 0);
    }

    private static class MockComment extends Comment {
        private final String identifier;
        private final Listing<Comment> replies;
        public MockComment(String identifier) {
            this(identifier, new FauxListing<>(new ArrayList<Comment>(), null, null));
        }

        public MockComment(String identifier, Listing<Comment> replies) {
            super(JrawUtils.fromString("{\"replies\":null}"));
            this.identifier = identifier;
            this.replies = replies;
        }

        @Override
        public String getBody() {
            return identifier;
        }

        @Override
        public String getFullName() {
            return identifier;
        }

        @Override
        public Listing<Comment> getReplies() {
            return replies;
        }

        @Override
        public String getAuthor() {
            // Needed for CommentNode.visualize()
            return "[JRAW]";
        }

        @Override
        public Integer getScore() {
            // Needed for CommentNode.visualize()
            return 1;
        }

        @Override
        public String toString() {
            return "MockComment (" + identifier + ")";
        }
    }
}
