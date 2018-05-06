package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.Enveloped;
import net.dean.jraw.databind.RedditModel;
import net.dean.jraw.databind.UnixTime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Date;

@AutoValue
@RedditModel(enveloped = false)
public abstract class WikiRevision implements Serializable, UniquelyIdentifiable {
    @UnixTime public abstract Date getTimestamp();
    @Nullable public abstract String getReason();
    @Nullable @Enveloped public abstract Account getAuthor();
    public abstract String getPage();
    public abstract String getId();

    @NotNull
    @Override
    public String getUniqueId() {
        return String.valueOf(hashCode());
    }

    public static WikiRevision create(Date newTimestamp, String newReason, Account newAuthor, String newPage, String newId) {
        return new AutoValue_WikiRevision(newTimestamp, newReason, newAuthor, newPage, newId);
    }

    public static JsonAdapter<WikiRevision> jsonAdapter(Moshi moshi) {
        return new AutoValue_WikiRevision.MoshiJsonAdapter(moshi);
    }

}
