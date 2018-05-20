package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.Serializable;

@AutoValue
public abstract class SubredditSearchResult implements Serializable {
    /** The approximate number of active users right now */
    @Json(name = "active_user_count") public abstract int getActiveUserCount();

    /** A full URL that points to the subreddit's icon */
    @Json(name = "icon_img") public abstract String getIconUrl();

    /** A hex color with the "#" included, or an empty string */
    @Json(name = "key_color") public abstract String getKeyColor();

    /** The subreddit's display name, e.g. "RocketLeague" */
    public abstract String getName();

    @Json(name = "subscriber_count") public abstract int getSubscriberCount();
    @Json(name = "allow_images") public abstract boolean isAllowImages();

    public static JsonAdapter<SubredditSearchResult> jsonAdapter(Moshi moshi) {
        return new AutoValue_SubredditSearchResult.MoshiJsonAdapter(moshi);
    }
}
