package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.Enveloped;
import net.dean.jraw.databind.RedditModel;
import net.dean.jraw.models.Trophy;

import java.util.List;

@AutoValue
@RedditModel
public abstract class TrophyList {
    @Enveloped
    public abstract List<Trophy> getTrophies();

    public static JsonAdapter<TrophyList> jsonAdapter(Moshi moshi) {
        return new AutoValue_TrophyList.MoshiJsonAdapter(moshi);
    }
}
