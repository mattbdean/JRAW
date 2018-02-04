package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@AutoValue
public abstract class LiveWebSocketUpdate implements Serializable {
    /**
     * <ul>
     *     <li> {@code update} — a new update has been posted in the thread. the payload is a {@link LiveUpdate}.
     *     <li> {@code activity} — periodic update on the viewer count, the payload is an {@link Activity}.
     *     <li> {@code settings} — change in the thread's settings, the payload is a {@link Settings}.
     *     <li> {@code delete} — an update has been deleted (removed from the thread), the payload is the ID of the deleted update
     *     <li> {@code strike} — an update has been stricken, the payload is the ID of the stricken update
     *     <li> {@code embeds_ready} — a previously posted update has been parsed and embedded media has been found in it. The
     *           payload is an {@link EmbedsReady}
     *     <li> {@code complete} — the thread has been marked as complete, no further updates will be sent. No payload.
     * </ul>
     */
    public abstract String getType();
    public abstract Object getPayload();

    public static LiveWebSocketUpdate create(@NotNull String type, @NotNull Object payload) {
        return new AutoValue_LiveWebSocketUpdate(type, payload);
    }

    @AutoValue
    public static abstract class Settings implements Serializable {
        public abstract String getDescription();
        public abstract String getTitle();
        public abstract boolean isNsfw();
        public abstract String getResources();

        public static JsonAdapter<Settings> jsonAdapter(Moshi moshi) {
            return new AutoValue_LiveWebSocketUpdate_Settings.MoshiJsonAdapter(moshi);
        }
    }

    @AutoValue
    public static abstract class EmbedsReady implements Serializable {
        /** Basic information about each of the embedded links present in the update */
        @Json(name = "media_embeds") public abstract List<LiveUpdate.Embed> getEmbeds();

        /**
         * Metadata provided from an external website (e.g. YouTube) about a link. For example, if the URL
         * <a href="https://www.youtube.com/watch?v=xuCn8ux2gbs">https://www.youtube.com/watch?v=xuCn8ux2gbs</a> was
         * posted to a live thread, one of the entries in this list could look like this:
         *
         * <pre>
         * {
         *   "provider_url":"https://www.youtube.com/",
         *   "description":"http://billwurtz.com patreon: http://patreon.com/billwurtz (...)",
         *   "title":"history of the entire world, i guess",
         *   "url":"http://www.youtube.com/watch?v=xuCn8ux2gbs",
         *   "type":"video",
         *   "original_url":"https://www.youtube.com/watch?v=xuCn8ux2gbs",
         *   "author_name":"bill wurtz",
         *   "height":281,
         *   "width":500,
         *   "html":"<iframe class=\"embedly-embed\" src=\"//cdn.embedly.com/widgets/me(...)",
         *   "thumbnail_width":480,
         *   "version":"1.0",
         *   "provider_name":"YouTube",
         *   "thumbnail_url":"https://i.ytimg.com/vi/xuCn8ux2gbs/hqdefault.jpg",
         *   "thumbnail_height":360,
         *   "author_url":"https://www.youtube.com/user/billwurtz"
         * }
         * </pre>
         */
        @Json(name = "mobile_embeds") public abstract List<Map<String, Object>> getExternalMetadata();

        @Json(name = "liveupdate_id")
        public abstract String getUpdateId();

        public static JsonAdapter<EmbedsReady> jsonAdapter(Moshi moshi) {
            return new AutoValue_LiveWebSocketUpdate_EmbedsReady.MoshiJsonAdapter(moshi);
        }
    }

    @AutoValue
    public static abstract class Activity implements Serializable {
        public abstract int getUsersActive();
        public abstract boolean isFuzzed();

        public static JsonAdapter<Activity> jsonAdapter(Moshi moshi) {
            return new AutoValue_LiveWebSocketUpdate_Activity.MoshiJsonAdapter(moshi);
        }
    }
}
