package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@AutoValue
public abstract class CurrentFlair implements Serializable {
    @Nullable
    @Json(name = "flair_css_class") public abstract String getCssClass();

    @Nullable
    @Json(name = "flair_template_id") public abstract String getId();

    @Nullable
    @Json(name = "flair_text") public abstract String getText();

    /** Either 'right' or 'left' */
    @Json(name = "flair_position") public abstract String getPosition();

    /**
     * Reddit doesn't represent the current flair as a null object, but rather as an object will all-null properties.
     * This method checks if there is a current flair for user/submission.
     */
    public final boolean isPresent() {
        return getCssClass() != null && getId() != null && getText() != null && getPosition() != null;
    }

    public static JsonAdapter<CurrentFlair> jsonAdapter(Moshi moshi) {
        return new AutoValue_CurrentFlair.MoshiJsonAdapter(moshi);
    }

    public static CurrentFlair create(String cssClass, String id, String text, String position) {
        return new AutoValue_CurrentFlair(cssClass, id, text, position);
    }
}
