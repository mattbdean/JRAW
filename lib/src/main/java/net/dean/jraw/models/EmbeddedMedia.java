package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.jetbrains.annotations.Nullable;

@AutoValue
public abstract class EmbeddedMedia {
    /** This is generally the OEmbed provider. Can also be the string {@code liveupdate} for a live reddit thread */
    public abstract String getType();

    @Nullable
    @Json(name = "oembed") public abstract OEmbed getOEmbed();

    /** The ID of the live thread referenced by the submission. Only present when {@link #getType()} is "liveupdate". */
    @Nullable
    @Json(name = "event_id") public abstract String getLiveThreadId();

    public static EmbeddedMedia create(String type, OEmbed newOEmbed) {
        return create(type, newOEmbed, null);
    }

    public static EmbeddedMedia create(String liveThreadId) {
        return create("liveupdate", null, liveThreadId);
    }

    public static EmbeddedMedia create(String newType, OEmbed newOEmbed, String newLiveThreadId) {
        return new AutoValue_EmbeddedMedia(newType, newOEmbed, newLiveThreadId);
    }

    public static JsonAdapter<EmbeddedMedia> jsonAdapter(Moshi moshi) {
        return new AutoValue_EmbeddedMedia.MoshiJsonAdapter(moshi);
    }

    /**
     * An object that models the JSON response for the oEmbed standard. Properties are made nullable if the spec deems
     * them optional. See [here](https://oembed.com/) for more.
     */
    @AutoValue
    public static abstract class OEmbed {
        /**
         * One of "photo", "video", "link", or "rich". See
         * [section 2.3.4.1 to 2.3.4.4 of the OEmbed standard](https://oembed.com) for more.
         */
        public abstract String getType();

        /** The string "1.0" */
        public abstract String getVersion();

        @Nullable
        public abstract String getTitle();

        @Nullable
        @Json(name = "author_name") public abstract String getAuthorName();

        @Nullable
        @Json(name = "author_url") public abstract String getAuthorUrl();

        @Nullable
        @Json(name = "provider_name") public abstract String getProviderName();

        @Nullable
        @Json(name = "provider_url") public abstract String getProviderUrl();

        /** The suggested length in seconds to hold this resource in cache */
        @Nullable
        @Json(name = "cache_age") public abstract Integer getCacheAge();

        @Nullable
        @Json(name = "thumbnail_url") public abstract String getThumbnailUrl();

        @Nullable
        @Json(name = "thumbnail_width") public abstract Integer getThumbnailWidth();

        @Nullable
        @Json(name = "thumbnail_height") public abstract Integer getThumbnailHeight();

        /** Direct link to the image in question. Present only when type is "photo" */
        @Nullable
        public abstract String getUrl();

        /** Width in pixels of the media. Present only when type is "photo", "video", or "rich" */
        @Nullable
        public abstract Integer getWidth();

        /** Height in pixels of the media. Present only when type is "photo", "video", or "rich" */
        @Nullable
        public abstract Integer getHeight();

        /** HTML to insert directly into a page to display the resource. Present only when type is "video" or "rich". */
        @Nullable
        public abstract String getEmbedHtml();

        public static OEmbed create(String newType, String newVersion, String newTitle, String newAuthorName,
                                    String newAuthorUrl, String newProviderName, String newProviderUrl,
                                    Integer newCacheAge, String newThumbnailUrl, Integer newThumbnailWidth,
                                    Integer newThumbnailHeight, String newUrl, Integer newWidth, Integer newHeight,
                                    String newEmbedHtml) {
            return new AutoValue_EmbeddedMedia_OEmbed(newType, newVersion, newTitle, newAuthorName, newAuthorUrl, newProviderName, newProviderUrl, newCacheAge, newThumbnailUrl, newThumbnailWidth, newThumbnailHeight, newUrl, newWidth, newHeight, newEmbedHtml);
        }

        public static JsonAdapter<OEmbed> jsonAdapter(Moshi moshi) {
            return new AutoValue_EmbeddedMedia_OEmbed.MoshiJsonAdapter(moshi);
        }
    }
}
