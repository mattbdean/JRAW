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
import net.dean.jraw.http.SubmissionRequest;
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
 * <p>Note that although this class implements {@link Iterable}, the provided Iterator will not have the same function
 * as using {@link #walkTree()}; only direct children will be iterated.
 *
 * @author Matthew Dean
 */
public class CommentNode implements Iterable<CommentNode> {
    private static final SimpleTreeTraverser traverser = new SimpleTreeTraverser();
    private static final int TOP_LEVEL_DEPTH = 1;
    private static final Lock morechildrenLock = new ReentrantLock();

    private MoreChildren moreChildren;
    private final String ownerId;
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
     * @param more A MoreChildren object which can be used to retrieve more comments later
     */
    public CommentNode(String ownerId, List<Comment> topLevelReplies, MoreChildren more, CommentSort commentSort) {
        // Validate only the public constructor because this value will be passed to the private constructor when the
        // children are instantiated.
        if (!JrawUtils.isFullname(ownerId))
            throw new IllegalArgumentException("Expecting fullname. Input for ownerId ('" + ownerId + "') is not suitable.");
        this.ownerId = ownerId;
        // This CommentNode is actually representing the Submission, whose depth is 0
        this.depth = 0;
        this.parent = null;
        this.comment = new RootComment(ownerId);
        this.moreChildren = more;
        this.commentSort = commentSort;
        this.children = createChildNodes(topLevelReplies);
    }

    private CommentNode(String ownerId, CommentNode parent, Comment data, MoreChildren moreChildren, CommentSort commentSort, int depth) {
        this.ownerId = ownerId;
        this.depth = depth;
        this.parent = parent;
        this.comment = data;
        this.moreChildren = moreChildren;
        this.commentSort = commentSort;
        this.children = createChildNodes(data.getDataNode());
    }

    private List<CommentNode> createChildNodes(JsonNode dataNode) {
        return createChildNodes(parseReplies(dataNode));
    }

    private List<CommentNode> createChildNodes(List<Comment> comments) {
        // Create a CommentNode for every Comment
        List<CommentNode> children = new LinkedList<>();
        for (Comment c : comments) {
            children.add(new CommentNode(this.ownerId, this, c, parseReplies(c.getDataNode()).getMoreChildren(), commentSort, depth + 1));
        }
        return children;
    }

    private Listing<Comment> parseReplies(JsonNode commentDataNode) {
        // If it has no replies, the value for the replies key will be an empty string or null
        JsonNode replies = commentDataNode.get("replies");
        if (replies.isNull() || (replies.isTextual() && replies.asText().isEmpty())) {
            return new Listing<>(Comment.class);
        } else {
            return new Listing<>(commentDataNode.get("replies").get("data"), Comment.class);
        }
    }

    /** Gets fullname of the submission to which this CommentNode belongs (ex: t3_92dd8). */
    public String getSubmissionName() {
        return ownerId;
    }

    /** Checks if this CommentNode has any children. */
    public boolean isEmpty() {
        return children.isEmpty();
    }

    /** Checks if there exists a MoreChildren object for this CommentNode. */
    public boolean hasMoreComments() {
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
     * Fully expands the comment tree below this node. This can be a very expensive call depending on how large the
     * thread is, as every {@link MoreChildren} requires its own HTTP request. It is therefore advised to use
     * {@link #loadFully(RedditClient, int, int)} instead to restrict the number of HTTP requests sent.
     *
     * @param reddit Used to make requests
     */
    public void loadFully(RedditClient reddit) {
        loadFully(reddit, -1, -1);
    }

    /**
     * Fully expands the comment tree below this node by finding all {@link MoreChildren} objects belonging to and below
     * this node and loading them into the tree using {@link #loadMoreComments(RedditClient)}. Be aware that without
     * setting a depth or request limit this may be a very costly operation, requiring potentially hundreds of requests
     * to fully satisfy the method call. Note that one request must be sent for every {@link MoreChildren} found.
     *
     * @param reddit Used to make requests
     * @param depthLimit The maximum depth to look into. A value of -1 disable the limit.
     * @param requestLimit The maximum amount of requests to send; there will be one request for every MoreChildren
     *                     object found. A value of -1 will disable the limit.
     * @throws NetworkException If there was a problem sending the request
     */
    public void loadFully(RedditClient reddit, int depthLimit, int requestLimit) throws NetworkException {
        int requests = 0;
        if (depthLimit < -1 || depthLimit < -1)
            throw new IllegalArgumentException("Expecting a number greater than or equal to -1, got " +
                    (requestLimit < -1 ? requestLimit : depthLimit));
        // Load this node's comments first
        while (hasMoreComments()) {
            loadMoreComments(reddit);
            if (++requests > requestLimit && depthLimit != -1)
                return;
        }

        // Load the children's comments next
        for (CommentNode node : walkTree(TraversalMethod.BREADTH_FIRST)) {
            // Travel breadth first so we can accurately compare depths
            if (depthLimit != -1 && node.depth > depthLimit)
                return;
            while (node.hasMoreComments()) {
                node.loadMoreComments(reddit);
                if (++requests > requestLimit && depthLimit != -1)
                    return;
            }
        }
    }

    /**
     * Gets more comments from {@link #getMoreComments(RedditClient)} and inserts them into the tree.
     * This method returns only new <em>root</em> nodes. If this CommentNode is not associated with a
     * {@link MoreChildren}, then an empty list is returned. A null value is never returned.
     *
     * @param reddit Used to make the request
     * @return A List of new root nodes
     * @throws NetworkException If the request was not successful
     */
    public List<CommentNode> loadMoreComments(RedditClient reddit) throws NetworkException {
        if (!hasMoreComments())
            // Nothing to do
            return new ArrayList<>();

        if (isThreadContinuation())
            return continueThread(reddit);

        int relativeRootDepth = depth + 1;
        List<CommentNode> newRootNodes = new ArrayList<>();
        List<Thing> thingsToAdd = getMoreComments(reddit);
        this.moreChildren = null;

        List<Comment> newComments = new ArrayList<>();
        List<MoreChildren> newMores = new ArrayList<>();

        // Assert every Thing is either a Comment or a MoreChildren
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
            // Instantiate a new CommentNode. The MoreChildren, if applicable, will be instantiated later.
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

        // Map of the MoreChildren's parent_id (which is a fullname) to the MoreChildren itself
        Map<String, MoreChildren> mores = new HashMap<>();
        for (MoreChildren m : newMores) {
            mores.put(m.getParentId(), m);
        }

        // Iterate the tree and insert MoreChildren objects
        for (CommentNode node : walkTree()) {
            if (mores.containsKey(node.getComment().getFullName())) {
                MoreChildren m = mores.get(node.getComment().getFullName());
                node.moreChildren = m;
                newMores.remove(m);
            }
        }

        // Special handling for the root node's MoreChildren
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

    private List<CommentNode> continueThread(RedditClient reddit) {
        if (!isThreadContinuation())
            throw new IllegalArgumentException("This CommentNode's MoreChildren is not a thread continuation");
        if (!hasMoreComments())
            return new ArrayList<>();

        // ownerId is a fullname, we only want the ID
        String id = ownerId.substring("t3_".length());
        CommentNode newNode = reddit.getSubmission(new SubmissionRequest.Builder(id)
                .focus(getComment().getId())
                .build()).getComments();
        this.moreChildren = null;

        // newNode is the RootComment, newNode[0] is the same as this CommentNode, so use newNode[0].children
        List<CommentNode> newRootNodes = newNode.children.get(0).children;
        int baseDepth = depth - 1;
        for (CommentNode node : newRootNodes) {
            children.add(new CommentNode(ownerId, this, node.comment, node.moreChildren, node.commentSort, node.depth + baseDepth));
        }

        return newRootNodes;
    }

    /**
     * <p>Checks if this node's {@link MoreChildren} object represents a truncated thread. This normally happens when
     * the depth of a particular branch of the tree continues to a depth exceeding 10. On the website, the MoreChildren
     * will be represented as a link with the text "continue this thread &rarr;." If a MoreChildren object points to a
     * truncated branch, then two things must be true:
     *
     * <ol>
     *     <li>The MoreChildren's "count" attribute is zero
     *     <li>The first ID listed in the MoreChildren is the same as its own ID.
     * </ol>
     *
     * <p>If these things are true, then this method will return true.
     *
     * @return If this comment's MoreChildren object represents a truncated comment branch.
     */
    public boolean isThreadContinuation() {
        // A 'continue this thread' type MoreChildren will have a count of 0 and the first child will be the ID of this
        // node's comment
        return hasMoreComments() &&
                moreChildren.getCount() == 0 &&
                moreChildren.getChildrenIds().get(0).equals(moreChildren.getId());
    }

    /**
     * Gets a list of {@link Comment} and {@link MoreChildren} objects from this node's MoreChildren object. The
     * resulting Things will be listed as if they were iterated in pre-order traversal. To add these new comments to the
     * tree, use {@link #loadMoreComments(RedditClient)} instead.
     *
     * @param reddit The RedditClient to make the HTTP request with
     * @return A list of new Comments and MoreChildren objects
     * @throws NetworkException If the request was not successful
     */
    @EndpointImplementation(Endpoints.MORECHILDREN)
    public List<Thing> getMoreComments(RedditClient reddit)
            throws NetworkException {
        if (!hasMoreComments())
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

    /** Gets this node's immediate children */
    public List<CommentNode> getChildren() { return children; }

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

    @Override
    public Iterator<CommentNode> iterator() {
        return children.iterator();
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
