package net.dean.jraw.models.internal;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RedditEnvelope<T> {
    public abstract String getKind();
    public abstract T getData();
}
