package net.dean.jraw.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.TreeTraverser;
import net.dean.jraw.EndpointImplementation;
import net.dean.jraw.Endpoints;
import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.RestResponse;
import net.dean.jraw.models.meta.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>This class represents one comment a comment tree.
 *
 * <p>Each CommentNode has a depth, a {@link Comment} instance, and zero or more CommentNode children. Depth is defined
 * as the level at which the comment exists. For example, a top-level reply has a depth of 1, a reply to that comment
 * has a depth of 2, and so on. It is only necessary to create one CommentNode, as the constructor will recursively
 * create a CommentNode for every reply to that comment, making a tree structure. For every Comment in the Submission,
 * there will be one CommentNode for it.
 *
 * <p>At the root of every comment tree there is a single root node, which represents the submission the comments belong
 * to. This node will have a depth of 0 and its children will be top-level replies to the submission. Although this node
 * is technically part of the tree, it will not be included when using {@link #walkTree()}.
 *
 * <p>For example, take this tree structure:
 *
 * <pre>
 *       z
 *       |
 *       a
 *     / | \
 *    b  c  d
 *       |   \
 *     f,g,h  i
 * </pre>
 *
 * <p>Node {@code z} represents the root node (submission) with a depth of 0. Node {@code a} is a top level reply to
 * that submission with a depth of 1. Nodes {@code f}, {@code g} and {@code h}, and {@code i} have a depth of 3. This
 * tree can be traversed using several different methods: Pre-order ({@code abcfghdi}), post-order ({@code bfghcida}),
 * and breadth-first ({@code abcdfghi}).
 *
 * @author Matthew Dean
 */
public class CommentNode {
    private static final SimpleTreeTraverser traverser = new SimpleTreeTraverser();
    private static final int TOP_LEVEL_DEPTH = 1;
    private static final Lock morechildrenLock = new ReentrantLock();

    private final String ownerId;
    private MoreChildren moreChildren;
    private final Comment comment;
    private final CommentNode parent;
    private final List<CommentNode> children;
    private final int depth;
    private final CommentSort commentSort;

    /**
     * Instantiates a new root CommentNode. This will create a CommentNode for every Comment in {@code topLevelReplies},
     * and then for their children, and so on.
     *
     * @param ownerId The Submission's fullname (ex: t3_92dd8)
     * @param topLevelReplies A list of top level replies to this submission
     * @param more A More object which can be used to retrieve more comments later
     */
    public CommentNode(String ownerId, List<Comment> topLevelReplies, MoreChildren more, CommentSort commentSort) {
        this.ownerId = ownerId;
        // This CommentNode is actually representing the Submission, whose depth is 0
        this.depth = 0;
        this.parent = null;
        this.comment = new RootComment(ownerId);
        this.moreChildren = more;
        this.commentSort = commentSort;
        this.children = createChildNodes(topLevelReplies);
    }

    private CommentNode(String ownerId, CommentNode parent, Comment data, MoreChildren more, CommentSort commentSort, int depth) {
        this.ownerId = ownerId;
        this.depth = depth;
        this.parent = parent;
        this.comment = data;
        this.moreChildren = more;
        this.commentSort = commentSort;
        this.children = createChildNodes(data.getReplies());
    }

    private List<CommentNode> createChildNodes(List<Comment> comments) {
        // Create a CommentNode for every Comment
        List<CommentNode> children = new LinkedList<>();
        for (Comment c : comments) {
            children.add(new CommentNode(this.ownerId, this, c, c.getReplies().getMoreChildren(), commentSort, depth + 1));
        }
        return children;
    }

    /** Gets fullname of the submission to which this CommentNode belongs (ex: t3_92dd8). */
    public String getSubmissionName() {
        return ownerId;
    }

    /** Checks if this CommentNode has any children. */
    public boolean isEmpty() {
        return children.isEmpty();
    }

    /** Checks if there exists a More object for this CommentNode. */
    public boolean hasMoreChildren() {
        return moreChildren != null;
    }

    /** Gets any more replies to this comment. Can be null. */
    public MoreChildren getMoreChildren() {
        return moreChildren;
    }

    /**
     * Attempts to find a CommentNode in this node's children by its fullname. No optimization will be used.
     * @param fullName The fullname of the comment to find. For example: t1_c0b75sp
     */
    public Optional<CommentNode> findChild(String fullName) {
        return findChild(fullName, LocationHint.anywhere());
    }

    /**
     * Attempts to find a CommentNode in this node's children by its fullname.
     * @param fullName The fullname of the comment to find. For example: t1_c0b75sp
     * @param hint A hint at where the comment is most likely to be
     */
    public Optional<CommentNode> findChild(String fullName, LocationHint hint) {
        if (fullName ==  null)
            throw new NullPointerException("fullName must not be null");

        for (CommentNode node : hint.getTraversalMethod().provideIterable(traverser, this)) {
            if (node.getComment().getFullName().equals(fullName))
                return Optional.of(node);
        }
        return Optional.absent();
    }

    /**
     * Logs this CommentNode and all of its children to {@link JrawUtils#logger()}. Includes indentation based on depth,
     * score, author, and body. Useful for debugging.
     */
    public void visualize() {
        int relativeRootDepth = depth;
        for (CommentNode node : walkTree()) {
            JrawUtils.logger().info("{}({}↑) {}: {}",
                    makeIndent(node.getDepth() - relativeRootDepth),
                    node.comment.getScore(),
                    node.comment.getAuthor(),
                    formatCommentBody(node.comment.getBody()));
        }
    }

    private String makeIndent(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth - 1; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    private String formatCommentBody(String body) {
        return body.replace("\n", "\\n").replace("\r", "\\r");
    }

    /**
     * Gets more comments from {@link #getMoreComments(RedditClient)} and inserts them into the tree.
     * This method returns only new <em>root</em> nodes. If this CommentNode is not associated with a More, then an
     * empty list is returned. Null is never returned.
     *
     * @param reddit Used to make the request
     * @return A List of new root nodes
     * @throws NetworkException If the request was not successful
     */
    public List<CommentNode> loadMoreComments(RedditClient reddit) throws NetworkException {
        if (!hasMoreChildren())
            // Nothing to do
            return new ArrayList<>();

        int relativeRootDepth = depth + 1;
        List<CommentNode> newRootNodes = new ArrayList<>();
        List<Thing> thingsToAdd = getMoreComments(reddit);

        List<Comment> newComments = new ArrayList<>();
        List<MoreChildren> newMores = new ArrayList<>();

        // Assert every Thing is either a Comment or a More
        for (Thing t : thingsToAdd) {
            if (t instanceof Comment) {
                newComments.add((Comment) t);
            } else if (t instanceof MoreChildren) {
                newMores.add((MoreChildren) t);
            } else {
                throw new IllegalStateException("Received a Thing that was not a Comment or MoreChildren, was "
                        + t.getClass().getName());
            }
        }

        // Comments from /api/morechildren are listed as if they were iterated in pre-order traversal
        CommentNode parent = this;
        for (Iterator<Comment> it = newComments.iterator(); it.hasNext(); ) {
            Comment newComment = it.next();
            // Navigate up the tree until we find the comment whose ID matches the new comment's parent_id
            while (!newComment.getParentId().equals(parent.getComment().getFullName())) {
                parent = parent.parent;
            }
            // Instantiate a new CommentNode. The More, if applicable, will be instantiated later.
            CommentNode node = new CommentNode(ownerId, parent, newComment, null, commentSort, parent.depth + 1);
            // Remove the Comment from the list
            it.remove();
            if (node.depth == relativeRootDepth)
                newRootNodes.add(node);
            parent.children.add(node);
            parent = node;
        }

        // newComments should be empty if everything was successful
        for (Comment c : newComments) {
            JrawUtils.logger().warn("Unable to find parent for " + c);
        }

        // Map of the More's parent_id (which is a fullname) to the More itself
        Map<String, MoreChildren> mores = new HashMap<>();
        for (MoreChildren m : newMores) {
            mores.put(m.getParentId(), m);
        }

        // Iterate the tree and insert Mores
        for (CommentNode node : walkTree()) {
            if (mores.containsKey(node.getComment().getFullName())) {
                MoreChildren m = mores.get(node.getComment().getFullName());
                node.moreChildren = m;
                newMores.remove(m);
            }
        }

        // Special handling for the More of the root node
        if (mores.containsKey(ownerId)) {
            MoreChildren m = mores.get(ownerId);
            this.moreChildren = m;
            newMores.remove(m);
        }

        for (MoreChildren m : newMores) {
            JrawUtils.logger().warn("Unable to find parent for " + m);
        }

        return newRootNodes;
    }

    /**
     * Gets a list of {@link Comment} and {@link MoreChildren} objects from this node's More object. The resulting Things will
     * be listed as if they were iterated in pre-order traversal. To add these new comments to the tree, use
     * {@link #loadMoreComments(RedditClient)}.
     *
     * @param reddit The RedditClient to make the HTTP request with
     * @return A list of new Comments and Mores
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.MORECHILDREN)
    public List<Thing> getMoreComments(RedditClient reddit)
            throws NetworkException {
        if (!hasMoreChildren())
            return new ArrayList<>();

        List<String> moreIds = moreChildren.getChildrenIds();
        StringBuilder ids = new StringBuilder(moreIds.get(0));
        for (int i = 1; i < moreIds.size(); i++) {
            String other = moreIds.get(i);
            ids.append(',').append(other);
        }

        RestResponse response;

        // Make sure we are only making one request to this endpoint at a time, as noted by the docs:
        // "**NOTE**: you may only make one request at a time to this API endpoint. Higher concurrency will result in an
        // error being returned."
        morechildrenLock.lock();
        try {
            // POST with a body could be used instead of GET with a query to avoid a long URL, but Reddit seems to
            // handle it just fine.
            response = reddit.execute(reddit.request()
                    .endpoint(Endpoints.MORECHILDREN)
                    .query(JrawUtils.mapOf(
                            "children", ids.toString(),
                            "link_id", ownerId,
                            "sort", commentSort.name().toLowerCase(),
                            "api_type", "json"
                    )).build());
        } finally {
            morechildrenLock.unlock();
        }

        JsonNode things = response.getJson().get("json").get("data").get("things");
        List<Thing> commentList = new ArrayList<>(things.size());
        for (JsonNode node : things) {
            String kind = node.get("kind").asText();
            JsonNode data = node.get("data");
            if (node.get("kind").asText().equals(Model.Kind.COMMENT.getValue())) {
                commentList.add(new Comment(data));
            } else if (node.get("kind").asText().equals(Model.Kind.MORE.getValue())) {
                commentList.add(new MoreChildren(data));
            } else {
                throw new IllegalArgumentException(String.format("Unexpected data type: %s. Expecting %s or %s",
                        kind, Model.Kind.COMMENT, Model.Kind.MORE));
            }
        }

        return commentList;
    }

    /** Gets the Comment this CommentNode is representing. */
    public Comment getComment() { return comment; }

    public CommentNode getParent() { return parent; }

    public int getDepth() { return depth; }

    /** Checks if this comment is a top-level reply */
    public boolean isTopLevel() {
        return depth == TOP_LEVEL_DEPTH;
    }

    /** Gets a CommentNode at the specified index */
    public CommentNode get(int i) {
        return children.get(i);
    }

    /**
     * Gets how many direct children this node has
     * @see #getTotalSize()
     */
    public int getImmediateSize() {
        return children.size();
    }

    /**
     * Gets the total number of children this node has. Will include this node's immediate size, the immediate size of
     * every child, and so on.
     *
     * @return The total number of nodes in this tree, counting all descendants of this node.
     * @see #getImmediateSize()
     */
    public int getTotalSize() {
        int size = getImmediateSize();
        for (CommentNode node : walkTree()) {
            size += node.getImmediateSize();
        }
        return size;
    }

    /** Provides a {@link FluentIterable} that will iterate every CommentNode in the tree, including this one. */
    public FluentIterable<CommentNode> walkTree() {
        return walkTree(TraversalMethod.PRE_ORDER);
    }

    /**
     * Provides a {@link FluentIterable} that will iterate every CommentNode in the tree, including this one.
     * @param method How the tree should be traversed.
     */
    public FluentIterable<CommentNode> walkTree(TraversalMethod method) {
        return method.provideIterable(traverser, this);
    }

    @Override
    public String toString() {
        return "CommentNode {" +
                "ownerId='" + ownerId + '\'' +
                ", parent=" + parent +
                ", depth=" + depth +
                ", more=" + moreChildren +
                ", comment=" + comment +
                ", totalSize=" + getTotalSize() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommentNode that = (CommentNode) o;

        if (depth != that.depth) return false;
        if (children != null ? !children.equals(that.children) : that.children != null) return false;
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
        if (moreChildren != null ? !moreChildren.equals(that.moreChildren) : that.moreChildren != null) return false;
        if (ownerId != null ? !ownerId.equals(that.ownerId) : that.ownerId != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ownerId != null ? ownerId.hashCode() : 0;
        result = 31 * result + (moreChildren != null ? moreChildren.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        result = 31 * result + depth;
        return result;
    }

    private static class SimpleTreeTraverser extends TreeTraverser<CommentNode> {
        @Override
        public Iterable<CommentNode> children(CommentNode root) {
            return root.children;
        }
    }

    static class RootComment extends Comment {
        private String submissionId;
        public RootComment(String submissionId) {
            super(JrawUtils.fromString("{\"replies\":null}"));
            this.submissionId = submissionId;
        }

        @Override
        public String getFullName() {
            return submissionId;
        }

        @Override
        public String getParentId() {
            throw new IllegalStateException("No parent ID on a RootComment");
        }

        @Override
        public String toString() {
            return "RootComment {" +
                    "submission='" + submissionId + '\'' +
                    '}';
        }
    }
}
