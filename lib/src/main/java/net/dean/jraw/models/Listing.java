package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.Enveloped;
import net.dean.jraw.databind.RedditModel;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A Listing is how reddit handles pagination.
 *
 * A Listing has two main parts: the fullnames of the item that comes next, and current page's children. As a
 * convenience, Listing delegates the methods inherited from {@link java.util.List} to {@link #getChildren()}. That
 * means that {@code listing.indexOf(foo)} is the same as {@code listing.children.indexOf(foo)}.
 */
@AutoValue
@RedditModel
public abstract class Listing<T> extends DelegatedList<T> implements Serializable {
    /** Gets the fullname of the model at the top of the next page, if it exists */
    @Json(name = "after")
    @Nullable
    public abstract String getNextName();

    // We have to write this in Java instead of Kotlin since Kotlin apparently doesn't attach the @Enveloped annotations
    // to the backing field it creates. Attaching it to a Java field or method (like we do here) DOES work.

    /** Gets the objects contained on this page */
    @Enveloped
    public abstract List<T> getChildren();

    @Override
    protected List<T> getDelegatedList() {
        return getChildren();
    }

    public static <T> JsonAdapter<Listing<T>> jsonAdapter(Moshi moshi, Type[] types) {
        return new AutoValue_Listing.MoshiJsonAdapter<>(moshi, types);
    }

    public static <T> Listing<T> empty() {
        return create(null, new ArrayList<T>());
    }

    public static <T> Listing<T> create(@Nullable String nextName, List<T> children) {
        return new AutoValue_Listing<>(nextName, children);
    }
}
