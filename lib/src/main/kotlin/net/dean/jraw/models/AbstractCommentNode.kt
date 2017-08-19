package net.dean.jraw.models

import net.dean.jraw.Endpoint
import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.references.CommentsRequest
import net.dean.jraw.util.IterativeTreeTraverser
import net.dean.jraw.util.TreeTraverser
import java.io.PrintStream

abstract class AbstractCommentNode<out T : PublicContribution<*>> : CommentNode<T> {
    protected var _moreChildren: MoreChildren? = null
    override val moreChildren: MoreChildren?
        get() = _moreChildren

    override fun hasMoreChildren(): Boolean = moreChildren != null

    override fun iterator(): Iterator<ReplyCommentNode> = replies.iterator()

    override fun totalSize(): Int {
        // walkTree() goes through this node and all child nodes, but we only care about child nodes
        return walkTree().size - 1
    }

    override fun visualize(out: PrintStream) {
        val relativeRootDepth = depth
        for (node in walkTree(TreeTraverser.Order.PRE_ORDER)) {
            val subj = node.subject
            val indent = "  ".repeat(node.depth - relativeRootDepth)

            // Use the submission URL if it's not a self post, otherwise just use the comment/submission body
            val body = if (subj is Submission && !subj.isSelfPost) subj.url else subj.body?.replace("\n", "\\n")
            out.println(indent + "${subj.author} (${subj.score}↑): $body")
        }
    }

    override fun walkTree(order: TreeTraverser.Order): List<CommentNode<*>> =
        IterativeTreeTraverser(this).traverse(order)

    override fun loadMore(reddit: RedditClient): FakeRootCommentNode<T> {
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
        return attach(requestMore(reddit))
    }

    /**
     * Fetches up to [MORE_CHILDREN_LIMIT] Comments/MoreChildren from the API. The objects that are returned are listed
     * as if they had been visited in pre-order traversal.
     */
    private fun requestMore(reddit: RedditClient): List<NestedIdentifiable> {
        if (!hasMoreChildren()) throw IllegalStateException("This node has no more children")

        val more = moreChildren!!

        if (more.isThreadContinuation())
            return continueThread(reddit)

        // Make sure we are only making one request to this endpoint at a time, as noted by the docs:
        // "**NOTE**: you may only make one request at a time to this API endpoint. Higher concurrency will result in an
        // error being returned."
        return synchronized(moreChildrenLock) {
            val json = reddit.request {
                it.endpoint(Endpoint.GET_MORECHILDREN)
                    .query(mapOf(
                        "api_type" to "json",
                        "children" to more.childrenIds.take(MORE_CHILDREN_LIMIT).joinToString(","),
                        "link_id" to KindConstants.SUBMISSION + '_' + settings.submissionId,
                        "sort" to settings.sort.name.toLowerCase()
                    ))
            }.json

            // IDs that weren't included in the request
            val leftoverIds = more.childrenIds.takeLast(more.childrenIds.size - MORE_CHILDREN_LIMIT)

            val things = JrawUtils.navigateJson(json, "json", "data", "things")

            // Don't use things.map() so we don't have to also use .toMutableList() after
            val redditObjects = ArrayList<NestedIdentifiable>()
            for (it in things) {
                val clazz: Class<out NestedIdentifiable> = if (it["data"].asText() == KindConstants.COMMENT)
                    Comment::class.java
                else
                    MoreChildren::class.java

                redditObjects.add(JrawUtils.jackson.treeToValue(it, clazz))
            }

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
                redditObjects[newRootMoreIndex] = MoreChildren(
                    parentFullName = newRootMore.parentFullName,
                    id = newRootMore.id,
                    fullName = newRootMore.fullName,
                    childrenIds = leftoverIds + newRootMore.childrenIds
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
                        subject = child,
                        depth = currentRoot.depth + 1,
                        parent = currentRoot,
                        settings = this.settings
                    )

                    currentRoot.replies.add(newNode)

                    if (currentRoot.subject.fullName == root.subject.fullName)
                        newDirectChildren.add(newNode)

                    // Assume the next comment is the child of the new node
                    currentRoot = newNode
                }
                is MoreChildren -> currentRoot._moreChildren = child
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
            .walkTree(TreeTraverser.Order.PRE_ORDER)
            // When we specify `focus` in CommentsRequest, reddit only returns that comment and its children. The
            // submission is technically the "root" of the whole tree, but its only child will be a node for the
            // "focus" comment. We only care about the children of the focus comment, so drop the first node
            // (submission node) and the second node (focus comment)
            .drop(2)
            // Map everything to the subject
            .map { it.subject as NestedIdentifiable }
    }

    companion object {
        /** The upper limit to how many more comments can be requested at one time. Equal to 100. */
        const val MORE_CHILDREN_LIMIT = 100
        private val moreChildrenLock = Any()
    }
}
