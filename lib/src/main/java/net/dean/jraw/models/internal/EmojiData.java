package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.Serializable;

@AutoValue
public abstract class EmojiData implements Serializable {
    public abstract String getUrl();

    /** The fullname of the account that created this emoji */
    @Json(name = "created_by") public abstract String getCreatedBy();

    public static EmojiData create(String newUrl, String newCreatedBy) {
        return new AutoValue_EmojiData(newUrl, newCreatedBy);
    }

    public static JsonAdapter<EmojiData> jsonAdapter(Moshi moshi) {
        return new AutoValue_EmojiData.MoshiJsonAdapter(moshi);
    }
}
