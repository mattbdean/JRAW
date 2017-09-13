package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.RedditModel;
import net.dean.jraw.databind.UnixTime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

@AutoValue
@RedditModel
public abstract class LiveThread implements Created, Identifiable {
    @NotNull
    @Override
    @Json(name = "created_utc") @UnixTime public abstract Date getCreated();

    public abstract String getDescription();

    @NotNull
    @Override
    @Json(name = "name") public abstract String getFullName();

    /** True if the content in this thread is NSFW (not safe for work) */
    public abstract boolean isNsfw();

    /** One of 'live' or 'complete' */
    public abstract String getState();
    public abstract String getTitle();

    /** The amount of people viewing the thread, or null if it's already completed */
    @Nullable
    @Json(name = "viewer_count") public abstract Integer getViewerCount();

    /** If the viewer count is randomly skewed, or null if it's already completed */
    @Nullable
    @Json(name = "viewer_count_fuzzed") public abstract Boolean getViewerCountFuzzed();

    /** The `ws://` URL for new live updates, or null if it's already completed */
    @Nullable
    @Json(name = "websocket_url") public abstract String getWebsocketUrl();

    /** Any additional resources provided by the moderators of the thread */
    public abstract String getResources();

    public static JsonAdapter<LiveThread> jsonAdapter(Moshi moshi) {
        return new AutoValue_LiveThread.MoshiJsonAdapter(moshi);
    }
}
