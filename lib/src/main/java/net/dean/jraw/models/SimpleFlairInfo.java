package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

/**
 * SimpleFlairInfo contains basic information about user's flair on a subreddit: username, flair css class, flair text.
 *
 * Used by endpoints with `modflair` scope (require moderator priveleges).
 *
 * @see net.dean.jraw.references.SubredditReference#flairList()
 * @see net.dean.jraw.references.SubredditReference#patchFlairList(List<SimpleFlairInfo>)
 * @see <a href="https://www.reddit.com/dev/api/#POST_api_flaircsv">Reddit API - POST /api/flaircsv</a>
 */
@AutoValue
public abstract class SimpleFlairInfo implements Serializable, UniquelyIdentifiable {

    /** Username */
    public abstract String getUser();

    /** CSS class of user's flair used for custom styling of the flair, if any */
    @Nullable
    @Json(name = "flair_css_class") public abstract String getCssClass();

    /** Text displayed on the flair, if any */
    @Nullable
    @Json(name = "flair_text") public abstract String getText();

    @NotNull
    @Override
    public String getUniqueId() {
        return String.valueOf(hashCode());
    }

    /**
     * Convert this object into a single line in a CSV line used for setting user flairs in bulk.
     *
     * @see net.dean.jraw.references.SubredditReference#patchFlairList(List<SimpleFlairInfo>)
     */
    public String toCsvLine() {
        String text = getText() != null ? getText() : "";
        String cssClass = getCssClass() != null ? getCssClass() : "";
        return getUser() + "," + text + "," + cssClass;
    }

    public static JsonAdapter<SimpleFlairInfo> jsonAdapter(Moshi moshi) {
        return new AutoValue_SimpleFlairInfo.MoshiJsonAdapter(moshi);
    }

    public static SimpleFlairInfo create(String user, String cssClass, String text) {
        return new AutoValue_SimpleFlairInfo(user, cssClass, text);
    }
}
