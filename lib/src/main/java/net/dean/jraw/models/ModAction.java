package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.UnixTime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Date;

@AutoValue
public abstract class ModAction implements Serializable, UniquelyIdentifiable {
    /** The type of action (e.g. "wikirevise", "editflair", etc.) */
    public abstract String getAction();

    /** Short summary of the action */
    @Nullable
    public abstract String getDetails();

    /** Extra information */
    @Nullable
    public abstract String getDescription();

    /** The name (not fullname) of the user that is the focus of this action, or an empty string if there is none */
    @Json(name = "target_author")
    abstract String getTargetAuthorInternal();

    // Make this return null so that it's consistent with the other getTarget* methods
    /** The name (not fullname) of the user that is the focus of this action, or null if there is none */
    public final String getTargetAuthor() {
        String author = getTargetAuthorInternal();
        return author.isEmpty() ? null : author;
    }

    /** Body of the submission of comment being targeted, or null if there is no target */
    @Nullable
    @Json(name = "target_body")
    public abstract String getTargetBody();

    /** The full name of the target, or null if there is none */
    @Json(name = "target_fullname")
    @Nullable
    public abstract String getTargetFullName();

    /** The title of the target, or null if there is none */
    @Nullable
    @Json(name = "target_title")
    public abstract String getTargetTitle();

    /** A permanent link to the target (just the path, not the full URL), or null if there is no target */
    @Nullable
    @Json(name = "target_permalink")
    public abstract String getTargetPermalink();

    /** The ID (not fullname) of the mod who did this action */
    @Json(name = "mod_id36")
    public abstract String getModeratorId();

    public abstract String getSubreddit();

    /** The ID (not fullname) of the subreddit */
    @Json(name = "sr_id36")
    public abstract String getSubredditId();

    /** The time at which the action was done */
    @UnixTime
    @Json(name = "created_utc")
    public abstract Date getActionDate();

    /** This action's ID */
    public abstract String getId();

    /** The username of the moderator who performed this action */
    @Json(name = "mod")
    public abstract String getModerator();

    @NotNull
    @Override
    public final String getUniqueId() {
        return getId();
    }

    public static JsonAdapter<ModAction> jsonAdapter(Moshi moshi) {
        return new AutoValue_ModAction.MoshiJsonAdapter(moshi);
    }
}
