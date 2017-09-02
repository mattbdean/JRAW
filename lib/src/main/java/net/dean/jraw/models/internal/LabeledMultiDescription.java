package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.RedditModel;

@AutoValue
@RedditModel
public abstract class LabeledMultiDescription {
    @Json(name = "body_md")
    public abstract String getBody();

    public static JsonAdapter<LabeledMultiDescription> jsonAdapter(Moshi moshi) {
        return new AutoValue_LabeledMultiDescription.MoshiJsonAdapter(moshi);
    }
}
