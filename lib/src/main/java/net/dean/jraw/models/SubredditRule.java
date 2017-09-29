package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.UnixTime;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@AutoValue
public abstract class SubredditRule implements Created {
    /** "link" if the rule only applies to submissions, "comment" for only comments, and "all" for both. */
    public abstract String getKind();

    /** Short Markdown-formatted description of the rule */
    public abstract String getDescription();

    /** A succinct version of the description */
    @Json(name = "short_name") public abstract String getShortName();

    /** The String the user uses when reporting something that violates this rule */
    @Json(name = "violation_reason") public abstract String getViolationReason();

    @NotNull
    @Override
    @UnixTime
    @Json(name = "created_utc") public abstract Date getCreated();

    public static SubredditRule create(String newKind, String newDescription, String newShortName, String newViolationReason, Date newCreated) {
        return new AutoValue_SubredditRule(newKind, newDescription, newShortName, newViolationReason, newCreated);
    }

    public static JsonAdapter<SubredditRule> jsonAdapter(Moshi moshi) {
        return new AutoValue_SubredditRule.MoshiJsonAdapter(moshi);
    }
}
