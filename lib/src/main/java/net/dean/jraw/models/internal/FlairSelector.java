package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.models.CurrentFlair;

@AutoValue
public abstract class FlairSelector {
    public abstract CurrentFlair getCurrent();

    public static JsonAdapter<FlairSelector> jsonAdapter(Moshi moshi) {
        return new AutoValue_FlairSelector.MoshiJsonAdapter(moshi);
    }

    public static FlairSelector create(CurrentFlair current) {
        return new AutoValue_FlairSelector(current);
    }
}
