package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

@AutoValue
public abstract class Flair {
    public abstract String getText();

    @Json(name = "text_editable") public abstract boolean isTextEditable();

    public abstract String getId();

    @Json(name = "css_class") public abstract String getCssClass();

    public static JsonAdapter<Flair> jsonAdapter(Moshi moshi) {
        return new AutoValue_Flair.MoshiJsonAdapter(moshi);
    }

    public static Flair create(String text, boolean isTextEditable, String id, String cssClass) {
        return new AutoValue_Flair(text, isTextEditable, id, cssClass);
    }
}
