package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.RedditClient;
import net.dean.jraw.databind.RedditModel;
import net.dean.jraw.databind.UnixTime;
import net.dean.jraw.models.internal.SubredditElement;
import net.dean.jraw.references.MultiredditReference;
import net.dean.jraw.references.Referenceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AutoValue
@RedditModel
public abstract class Multireddit implements Created, Referenceable<MultiredditReference> {
    private List<String> subreddits;

    /** If the currently logged-in user can edit this multireddit */
    @Json(name = "can_edit") public abstract boolean isEditable();

    /** The path this multireddit was copied from, or null if not copied */
    @Nullable
    @Json(name = "copied_from") public abstract String getCopiedFrom();

    @NotNull
    @Override
    @Json(name = "created_utc") @UnixTime public abstract Date getCreated();

    /** Name used in the API. Usually the same thing as {@link #getDisplayName()} unless specifically altered. */
    @Json(name = "name") public abstract String getCodeName();

    /** Markdown-formatted description */
    @Json(name = "description_md") public abstract String getDescription();

    /** Name displayed to the user */
    @Json(name = "display_name") public abstract String getDisplayName();

    /** See {@link MultiredditPatch#getIconName()} */
    @Json(name = "icon_name") public abstract String getIconName();

    /**
     * A hex-formatted hex string, like `#CEE3F8`. This color is primarily used when viewing the multireddit on the
     * mobile site.
     */
    @Json(name = "key_color") public abstract String getKeyColor();

    /** An absolute URL to an icon based on {@link #getIconName()}, if any */
    @Nullable
    @Json(name = "icon_url") public abstract String getIconUrl();

    /** The full multireddit path in the format of `/user/{username}/m/{multiname}` */
    public abstract String getPath();

    @Json(name = "subreddits") abstract List<SubredditElement> getSubredditElements();

    /** A list of subreddit names that this multireddit draws from */
    public List<String> getSubreddits() {
        if (subreddits != null) return subreddits;

        synchronized (this) {
            subreddits = new ArrayList<>(getSubredditElements().size());
            for (SubredditElement sr : getSubredditElements())
                subreddits.add(sr.getName());
        }

        return subreddits;
    }

    /** One of `public`, `private`, or `hidden` */
    public abstract String getVisibility();

    /** Either 'classic' or 'fresh' */
    @Json(name = "weighting_scheme") public abstract String getWeightingScheme();

    @NotNull
    @Override
    public MultiredditReference toReference(@NotNull RedditClient reddit) {
        String[] parts = getPath().split("/");
        // "/user/{username}/m/{name}".split("/") => ["", "user", "{username}", "m", "{name}", ""]
        return reddit.user(parts[2]).multi(parts[4]);
    }

    public static JsonAdapter<Multireddit> jsonAdapter(Moshi moshi) {
        return new AutoValue_Multireddit.MoshiJsonAdapter(moshi);
    }
}
