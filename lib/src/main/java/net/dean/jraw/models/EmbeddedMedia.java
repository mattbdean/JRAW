package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Some media that is viewable inline when browsing reddit through the website. There are generally two shapes this
 * class can take. When the media is from an external source, {@link #getOEmbed()} and {@link #getType()} will both be
 * non-null. When the media is hosted by reddit, {@link #getRedditVideo()} will be non-null.
 */
@AutoValue
public abstract class EmbeddedMedia implements Serializable {
    /**
     * This is generally the OEmbed provider. Can also be the string {@code liveupdate} for a live reddit thread. Null
     * when {@link #getRedditVideo() is not}
     */
    @Nullable
    public abstract String getType();

    @Nullable
    @Json(name = "oembed") public abstract OEmbed getOEmbed();

    /** The ID of the live thread referenced by the submission. Only present when {@link #getType()} is "liveupdate". */
    @Nullable
    @Json(name = "event_id") public abstract String getLiveThreadId();

    @Nullable
    @Json(name = "reddit_video") public abstract RedditVideo getRedditVideo();

    public static EmbeddedMedia create(String type, OEmbed newOEmbed) {
        return create(type, newOEmbed, null, null);
    }

    public static EmbeddedMedia create(String liveThreadId) {
        return create("liveupdate", null, liveThreadId, null);
    }

    public static EmbeddedMedia create(String newType, OEmbed newOEmbed, String newLiveThreadId, RedditVideo vid) {
        return new AutoValue_EmbeddedMedia(newType, newOEmbed, newLiveThreadId, vid);
    }

    public static JsonAdapter<EmbeddedMedia> jsonAdapter(Moshi moshi) {
        return new AutoValue_EmbeddedMedia.MoshiJsonAdapter(moshi);
    }

    /**
     * An object that models the JSON response for the oEmbed standard. Properties are made nullable if the spec deems
     * them optional. See [here](https://oembed.com/) for more.
     */
    @AutoValue
    public static abstract class OEmbed implements Serializable {
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

    @AutoValue
    public static abstract class RedditVideo implements Serializable {
        @Json(name = "fallback_url")
        public abstract String getFallbackUrl();

        public abstract int getHeight();
        public abstract int getWidth();

        @Json(name = "scrubber_media_url")
        public abstract String getScrubberMediaUrl();

        @Json(name = "dash_url")
        public abstract String getDashUrl();

        /** Length of the video in seconds */
        public abstract int getDuration();

        @Json(name = "hls_url")
        public abstract String getHlsUrl();

        public static JsonAdapter<RedditVideo> jsonAdapter(Moshi moshi) {
            return new AutoValue_EmbeddedMedia_RedditVideo.MoshiJsonAdapter(moshi);
        }
    }
}
