package net.dean.jraw.databind;

import com.ryanharter.auto.value.moshi.MoshiAdapterFactory;
import com.squareup.moshi.JsonAdapter;

/**
 * Add this factory to your {@code Moshi.Builder} to enable serializing all {@code @AutoValue} models. See
 * <a href="https://github.com/rharter/auto-value-moshi#factory">here</a> for more details.
 */
@MoshiAdapterFactory
public abstract class ModelAdapterFactory implements JsonAdapter.Factory {
    public static JsonAdapter.Factory create() {
        return new AutoValueMoshi_ModelAdapterFactory();
    }
}
