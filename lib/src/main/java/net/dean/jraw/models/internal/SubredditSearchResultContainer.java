package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.models.SubredditSearchResult;

import java.util.List;

@AutoValue
public abstract class SubredditSearchResultContainer {
    public abstract List<SubredditSearchResult> getSubreddits();

    public static JsonAdapter<SubredditSearchResultContainer> jsonAdapter(Moshi moshi) {
        return new AutoValue_SubredditSearchResultContainer.MoshiJsonAdapter(moshi);
    }
}
