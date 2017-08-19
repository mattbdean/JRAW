package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class MoreChildren(
    @JsonProperty("parent_id") override val parentFullName: String,
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
