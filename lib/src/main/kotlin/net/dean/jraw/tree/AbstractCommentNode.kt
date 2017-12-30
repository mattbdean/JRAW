package net.dean.jraw.tree

import net.dean.jraw.Endpoint
import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.databind.Enveloped
import net.dean.jraw.http.LogAdapter
import net.dean.jraw.models.*
import net.dean.jraw.models.internal.GenericJsonResponse
import net.dean.jraw.references.CommentsRequest
import net.dean.jraw.tree.CommentNode.Companion.NO_LIMIT

/**
 * This class is the base implementation for all CommentNodes
 */
abstract class AbstractCommentNode<out T : PublicContribution<*>> protected constructor(
    override val depth: Int,
    override var moreChildren: MoreChildren?,
    override val subject: T,
    override val settings: CommentTreeSettings
) : CommentNode<T> {
    override val replies: MutableList<CommentNode<Comment>> = mutableListOf()

    /**
     * Initializes [moreChildren] and [replies].
     */
    protected fun initReplies(replies: List<NestedIdentifiable>) {
        val (comments, allMoreChildren) = replies.partition { it is Comment }
        if (allMoreChildren.size > 1)
            throw IllegalStateException("More than 1 MoreChildren object found")

        this.moreChildren = if (allMoreChildren.isNotEmpty()) allMoreChildren[0] as MoreChildren else null

        for (reply in comments) {
            this.replies.add(ReplyCommentNode(
                depth = this.depth + 1,
                comment = reply as Comment,
                settings = settings,
                parent = this
            ))
        }
    }

    override fun hasMoreChildren(): Boolean = moreChildren != null

    /** */
    override fun iterator(): Iterator<CommentNode<Comment>> = replies.iterator()

    override fun totalSize(): Int {
        // walkTree() goes through this node and all child nodes, but we only care about child nodes
        return walkTree().count() - 1
    }

    override fun visualize(out: LogAdapter) {
        val relativeRootDepth = depth
        for (node in walkTree(TreeTraversalOrder.PRE_ORDER)) {
            val subj = node.subject
            val indent = "  ".repeat(node.depth - relativeRootDepth)

            // Use the submission URL if it's not a self post, otherwise just use the comment/submission body
            val body = if (subj is Submission && !subj.isSelfPost) subj.url else subj.body?.replace("\n", "\\n")
            out.writeln(indent + "${subj.author} (${subj.score}↑): $body")
        }
    }

    override fun walkTree(order: TreeTraversalOrder): Sequence<CommentNode<*>> =
        TreeTraverser.traverse(this, order)

    override fun loadMore(reddit: RedditClient): FakeRootCommentNode<T> {
        if (moreChildren == null)
            throw IllegalStateException("No more children")

        val fakeRoot = FakeRootCommentNode(
            depth = this.depth,
            settings = this.settings,
            subject = this.subject
        )

        attach(requestMore(reddit), fakeRoot)
        return fakeRoot
    }

    override fun replaceMore(reddit: RedditClient): List<ReplyCommentNode> {
        if (!hasMoreChildren()) return listOf()
        val moreExpanded = requestMore(reddit)
        // if not all children were loaded, MoreChildren will be attached later
        this.moreChildren = null
        return attach(moreExpanded)
    }

    /**
     * Fetches up to [MORE_CHILDREN_LIMIT] Comments/MoreChildren from the API. The objects that are returned are listed
     * as if they had been visited in pre-order traversal.
     */
    private fun requestMore(reddit: RedditClient): List<NestedIdentifiable> {
        if (!hasMoreChildren()) throw IllegalStateException("This node has no more children")

        val more = moreChildren!!

        if (more.isThreadContinuation)
            return continueThread(reddit)

        // Make sure we are only making one request to this endpoint at a time, as noted by the docs:
        // "**NOTE**: you may only make one request at a time to this API endpoint. Higher concurrency will result in an
        // error being returned."
        return synchronized(moreChildrenLock) {
            val json: GenericJsonResponse = reddit.request {
                it.endpoint(Endpoint.GET_MORECHILDREN)
                    .query(mapOf(
                        "api_type" to "json",
                        "children" to more.childrenIds.take(MORE_CHILDREN_LIMIT).joinToString(","),
                        "link_id" to KindConstants.SUBMISSION + '_' + settings.submissionId,
                        "sort" to settings.sort.name.toLowerCase()
                    ))
            }.deserialize()

            // IDs that weren't included in the request
            val leftoverIds = more.childrenIds.drop(MORE_CHILDREN_LIMIT)

            // The "things" node is an array of either comments or morechildren
            val things = json.json?.data?.get("things") as? List<*> ?:
                throw IllegalArgumentException("Unexpected JSON response")

            // Transform every element to either a Comment or a MoreChildren
            val adapter = JrawUtils.adapter<NestedIdentifiable>(Enveloped::class.java)
            val redditObjects = things.map { adapter.fromJsonValue(it)!! } as MutableList<NestedIdentifiable>

            // Sometimes the reddit API will send us another MoreChildren object for the same root node. Since we can't
            // have more than one MoreChildren for a single CommentNode, we have to combine the two
            val newRootMoreIndex = if (leftoverIds.isEmpty()) {
                // If we don't have any leftover IDs then there is nothing to do
                -1
            } else {
                // Try to find a MoreChildren that has the same parent as this.moreChildren
                redditObjects.indexOfFirst {
                    it is MoreChildren && it.parentFullName == more.parentFullName
                }
            }

            // If we found an additional root MoreChildren, replace it with the data we already have
            if (newRootMoreIndex >= 0) {
                val newRootMore = redditObjects[newRootMoreIndex] as MoreChildren
                redditObjects[newRootMoreIndex] = MoreChildren.create(
                    /*fullName = */newRootMore.fullName,
                    /*id = */newRootMore.id,
                    /*parentFullName = */newRootMore.parentFullName,
                    /*childrenIds = */leftoverIds + newRootMore.childrenIds
                )
            }

            /*return*/ redditObjects
        }
    }

    /**
     * Attaches a list of Comments and MoreChildren to a given root node. Returns all new direct children.
     */
    private fun attach(children: List<NestedIdentifiable>, root: AbstractCommentNode<T> = this): List<ReplyCommentNode> {
        val newDirectChildren: MutableList<ReplyCommentNode> = ArrayList()

        var currentRoot: AbstractCommentNode<*> = root

        // Children are listed in pre-order traversal
        for (child in children) {
            while (child.parentFullName != currentRoot.subject.fullName) {
                currentRoot = currentRoot.parent as AbstractCommentNode<*>

                if (currentRoot.depth < root.depth)
                    throw IllegalStateException("Failed to properly create tree")
            }

            when (child) {
                is Comment -> {
                    val newNode = ReplyCommentNode(
                        comment = child,
                        depth = currentRoot.depth + 1,
                        parent = currentRoot,
                        settings = this.settings
                    )

                    // Sometimes same nodes are added more than once. Instead of not processing the duplicates we remove
                    // the old ones in order to not break the traversal algorithm
                    currentRoot.replies.removeIf { it.subject == newNode.subject }

                    currentRoot.replies.add(newNode)

                    if (currentRoot.subject.fullName == root.subject.fullName)
                        newDirectChildren.add(newNode)

                    // Assume the next comment is the child of the new node
                    currentRoot = newNode
                }
                is MoreChildren -> currentRoot.moreChildren = child
                else -> throw IllegalArgumentException("Expected Comment or MoreChildren, got " + child.javaClass)
            }
        }

        return newDirectChildren
    }

    /**
     * Requests more comments from a MoreChildren that is a thread continuation.
     *
     * @see MoreChildren.isThreadContinuation
     */
    private fun continueThread(reddit: RedditClient): List<NestedIdentifiable> {
        // Request a whole new comment tree with this comment as the root node instead of the actual submission
        val root = reddit
            .submission(settings.submissionId)
            .comments(CommentsRequest(focus = subject.id, sort = settings.sort))

        return root
            .walkTree(TreeTraversalOrder.PRE_ORDER)
            // When we specify `focus` in CommentsRequest, reddit only returns that comment and its children. The
            // submission is technically the "root" of the whole tree, but its only child will be a node for the
            // "focus" comment. We only care about the children of the focus comment, so drop the first node
            // (submission node) and the second node (focus comment)
            .drop(2)
            // Map everything to the subject
            .map { it.subject as NestedIdentifiable }
            .toList()
    }

    override fun loadFully(reddit: RedditClient, depthLimit: Int, requestLimit: Int) {
        var requests = 0
        if (depthLimit < NO_LIMIT || requestLimit < NO_LIMIT)
            throw IllegalArgumentException("Expecting a number greater than or equal to -1, got " + if (requestLimit < NO_LIMIT) requestLimit else depthLimit)
        // Load this node's comments first
        while (hasMoreChildren()) {
            replaceMore(reddit)
            if (++requests > requestLimit && depthLimit != NO_LIMIT)
                return
        }

        // Load the children's comments next
        for (node in walkTree(TreeTraversalOrder.BREADTH_FIRST)) {
            // Travel breadth first so we can accurately compare depths
            if (depthLimit != NO_LIMIT && node.depth > depthLimit)
                return
            while (node.hasMoreChildren()) {
                node.replaceMore(reddit)
                if (++requests > requestLimit && depthLimit != NO_LIMIT)
                    return
            }
        }
    }

    override fun toString(): String {
        return "AbstractCommentNode(depth=$depth, body=${subject.body}, replies=List[${replies.size}])"
    }

    /** */
    companion object {
        /** The upper limit to how many more comments can be requested at one time. Equal to 100. */
        const val MORE_CHILDREN_LIMIT = 100
        private val moreChildrenLock = Any()
    }
}
