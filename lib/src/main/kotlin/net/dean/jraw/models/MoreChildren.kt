package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class MoreChildren(
    @JsonProperty("count") val actualSize: Int,
    @JsonProperty("parent_id") val parentFullName: String,
    @JsonProperty("children") val childrenIds: List<String>,
    @JsonProperty("name") val fullName: String,
    val id: String
) : RedditObject(KindConstants.MORE_CHILDREN) {

    override fun toString(): String {
        return "MoreChildren(actualSize=$actualSize, parentFullName='$parentFullName')"
    }

    /**
     * Returns true if this MoreChildren object represents a thread continuation. On the website, thread continuations
     * are illustrated with "continue this thread →". Thread continuations are only seen when the depth of a CommentNode
     * exceeds the depth that reddit is willing to render (defaults to 10).
     *
     * A MoreChildren that is a thread continuation will have an [id] of "_" and an [actualSize] of 0.
     *
     * @see net.dean.jraw.references.CommentsRequest.depth
     */
    fun isThreadContinuation(): Boolean {
        return actualSize == 0 && id == "_"
    }
}
