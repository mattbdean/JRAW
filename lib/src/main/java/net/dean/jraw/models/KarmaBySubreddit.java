package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.RedditModel;

import java.io.Serializable;

@AutoValue
@RedditModel
public abstract class KarmaBySubreddit implements Serializable {
    @Json(name = "sr") public abstract String getSubreddit();
    @Json(name = "comment_karma") public abstract int getCommentKarma();
    @Json(name = "link_karma") public abstract int getLinkKarma();

    public static JsonAdapter<KarmaBySubreddit> jsonAdapter(Moshi moshi) {
        return new AutoValue_KarmaBySubreddit.MoshiJsonAdapter(moshi);
    }
}
