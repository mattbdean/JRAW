package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.RedditModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A MoreChildren is a model used by reddit to represent comments that exist, but could not be presented in the response
 * due to the large amounts of other, higher priority comments already being shown in the thread. On the website, a
 * MoreChildren is represented by the text "load more comments (<em>x</em> replies)". The average user shouldn't have to
 * deal with this class directly as {@link net.dean.jraw.tree.CommentNode} will handle loading more comments for you.
 *
 * MoreChildren instances can appear anywhere in a comment tree except at the very root.
 */
@AutoValue
@RedditModel
public abstract class MoreChildren implements NestedIdentifiable {
    @Override
    @NotNull
    @Json(name = "name") public abstract String getFullName();

    @Override
    @NotNull
    public abstract String getId();

    @Override
    @NotNull
    @Json(name = "parent_id") public abstract String getParentFullName();

    @Json(name = "children") public abstract List<String> getChildrenIds();

    /**
     * Returns true if this MoreChildren object represents a thread continuation. On the website, thread continuations
     * are illustrated with "continue this thread →". Thread continuations are only seen when the depth of a CommentNode
     * exceeds the depth that reddit is willing to render (defaults to 10).
     *
     * A MoreChildren that is a thread continuation will have an [id] of "_" and an empty [childrenIds] list.
     *
     * @see net.dean.jraw.references.CommentsRequest#depth
     */
    public final boolean isThreadContinuation() {
        return getChildrenIds().isEmpty() && getId().equals("_");
    }

    public static MoreChildren create(String fullName, String id, String parentFullName, List<String> childrenIds) {
        return new AutoValue_MoreChildren(fullName, id, parentFullName, childrenIds);
    }

    public static JsonAdapter<MoreChildren> jsonAdapter(Moshi moshi) {
        return new AutoValue_MoreChildren.MoshiJsonAdapter(moshi);
    }
}
