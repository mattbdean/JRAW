package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.lang.reflect.Type;

/**
 * Class that models a typical JSON structure returned by the reddit API.
 */
@AutoValue
public abstract class RedditModelEnvelope<T> {
    /**
     * Describes the type of the encapsulated data. For example, "t1" for comments, "t2" for accounts. See
     * {@link net.dean.jraw.models.KindConstants} for more.
     */
    public abstract String getKind();
    public abstract T getData();

    public static <T> JsonAdapter<RedditModelEnvelope<T>> jsonAdapter(Moshi moshi, Type[] types) {
        return new AutoValue_RedditModelEnvelope.MoshiJsonAdapter<>(moshi, types);
    }

    public static <T> RedditModelEnvelope create(String kind, T data) {
        return new AutoValue_RedditModelEnvelope<>(kind, data);
    }
}
