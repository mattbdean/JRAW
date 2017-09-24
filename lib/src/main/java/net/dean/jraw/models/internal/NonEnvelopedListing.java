package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.RedditModel;
import net.dean.jraw.models.Listing;

import java.lang.reflect.Type;
import java.util.List;

@AutoValue
@RedditModel
public abstract class NonEnvelopedListing<T> {
    @Json(name = "after") public abstract String getNextName();
    @Json(name = "children") public abstract List<T> getChildren();

    public final Listing<T> toListing() {
        return Listing.create(getNextName(), getChildren());
    }

    public static <T> NonEnvelopedListing<T> create(String newNextName, List<T> newChildren) {
        return new AutoValue_NonEnvelopedListing<>(newNextName, newChildren);
    }

    public static <T> JsonAdapter<NonEnvelopedListing<T>> jsonAdapter(Moshi moshi, Type[] types) {
        return new AutoValue_NonEnvelopedListing.MoshiJsonAdapter<>(moshi, types);
    }
}
