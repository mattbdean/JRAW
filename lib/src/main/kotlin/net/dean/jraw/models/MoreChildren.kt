package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A MoreChildren is a model used by reddit to represent comments that exist, but could not be presented in the response
 * due to the large amounts of other, higher priority comments already being shown in the thread. On the website, a
 * MoreChildren is represented by the text "load more comments (*x* replies)". The average user shouldn't have to deal
 * with this class directly as [CommentNode] will handle loading more comments for you.
 *
 * MoreChildren instances can appear anywhere in a comment tree except at the very root.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class MoreChildren(
    /** The full name of the comment or submission whose direct, unshown children this model represents. */
    @JsonProperty("parent_id") override val parentFullName: String,

    /** A list of base-36 IDs */
    @JsonProperty("children") val childrenIds: List<String>,

    @JsonProperty("name") override val fullName: String,
    override val id: String
) : RedditObject(KindConstants.MORE_CHILDREN), NestedIdentifiable {

    override fun toString(): String {
        return "MoreChildren(size=${childrenIds.size}, parentFullName='$parentFullName')"
    }

    /**
     * Returns true if this MoreChildren object represents a thread continuation. On the website, thread continuations
     * are illustrated with "continue this thread →". Thread continuations are only seen when the depth of a CommentNode
     * exceeds the depth that reddit is willing to render (defaults to 10).
     *
     * A MoreChildren that is a thread continuation will have an [id] of "_" and an empty [childrenIds] list.
     *
     * @see net.dean.jraw.references.CommentsRequest.depth
     */
    fun isThreadContinuation(): Boolean {
        return childrenIds.isEmpty() && id == "_"
    }
}
