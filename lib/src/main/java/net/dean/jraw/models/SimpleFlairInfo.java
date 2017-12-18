package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.RedditModel;
import org.jetbrains.annotations.Nullable;

@AutoValue
@RedditModel(enveloped = false)
public abstract class SimpleFlairInfo {

    public abstract String getUser();

    @Nullable
    @Json(name = "flair_css_class") public abstract String getCssClass();

    @Nullable
    @Json(name = "flair_text") public abstract String getText();

    public static JsonAdapter<SimpleFlairInfo> jsonAdapter(Moshi moshi) {
        return new AutoValue_SimpleFlairInfo.MoshiJsonAdapter(moshi);
    }

    public static SimpleFlairInfo create(String user, String cssClass, String text) {
        return new AutoValue_SimpleFlairInfo(user, cssClass, text);
    }

    public String toCsvLine() {
        String text = getText() != null ? getText() : "";
        String cssClass = getCssClass() != null ? getCssClass() : "";
        return getUser() + "," + text + "," + cssClass;
    }
}
