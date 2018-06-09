package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class SubredditName {
    @Json(name = "sr_name")
    public abstract String getName();

    public static JsonAdapter<SubredditName> jsonAdapter(Moshi moshi) {
        return new AutoValue_SubredditName.MoshiJsonAdapter(moshi);
    }
}
