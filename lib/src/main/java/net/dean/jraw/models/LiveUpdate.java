package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.RedditModel;
import net.dean.jraw.databind.UnixTime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@AutoValue
@RedditModel
public abstract class LiveUpdate implements Created, Identifiable, Serializable {
    public abstract String getAuthor();
    public abstract String getBody();

    @NotNull
    @Override
    @Json(name = "created_utc") @UnixTime public abstract Date getCreated();

    @NotNull
    @Json(name = "name") public abstract String getFullName();

    public abstract List<Embed> getEmbeds();

    /**
     * If the update has been stricken. A stricken update appears on the website with the <strike>strikethrough</strike>
     * effect applied to its body.
     */
    public abstract boolean isStricken();

    @NotNull
    @Override
    public String getUniqueId() { return getFullName(); }

    public static JsonAdapter<LiveUpdate> jsonAdapter(Moshi moshi) {
        return new AutoValue_LiveUpdate.MoshiJsonAdapter(moshi);
    }

    /** Embedded media inside of a live update. */
    @AutoValue
    public static abstract class Embed implements Serializable {
        public abstract String getUrl();
        public @Nullable abstract Integer getWidth();
        public @Nullable abstract Integer getHeight();

        public static JsonAdapter<Embed> jsonAdapter(Moshi moshi) {
            return new AutoValue_LiveUpdate_Embed.MoshiJsonAdapter(moshi);
        }
    }
}
