package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class SubredditElement {
    public abstract String getName();

    public static SubredditElement create(String name) { return new AutoValue_SubredditElement(name); }
    public static JsonAdapter<SubredditElement> jsonAdapter(Moshi moshi) {
        return new AutoValue_SubredditElement.MoshiJsonAdapter(moshi);
    }
}
