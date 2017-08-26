package net.dean.jraw.models;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import net.dean.jraw.databind.Enveloped;
import net.dean.jraw.databind.RedditModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;

/**
 * A Listing is how reddit handles pagination.
 *
 * A Listing has three main parts: the fullnames of the items before and after, and its children. As a convenience,
 * Listing delegates the methods inherited from {@link java.util.List} to {@link #getChildren()}. That means that
 * {@code listing.indexOf(foo)} is the same as {@code listing.children.indexOf(foo)}.
 */
@AutoValue
@RedditModel(kind = KindConstants.LISTING)
public abstract class Listing<T> implements List<T> {
    /** Gets the fullname of the model at the top of the next page, if it exists */
    @Json(name = "after")
    @Nullable
    public abstract String getNextName();

    // We have to write this in Java instead of Kotlin since Kotlin apparently doesn't attach the @Enveloped annotations
    // to the backing field it creates. Attaching it to a Java field or method (like we do here) DOES work.

    /** Gets the objects contained on this page */
    @Enveloped
    public abstract List<T> getChildren();

    public static <T> JsonAdapter<Listing<T>> jsonAdapter(Moshi moshi, Type[] types) {
        return new AutoValue_Listing.MoshiJsonAdapter<>(moshi, types);
    }

    public static <T> Listing<T> empty() {
        return create(null, new ArrayList<T>());
    }

    public static <T> Listing<T> create(@Nullable String nextName, List<T> children) {
        return new AutoValue_Listing<>(nextName, children);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // java.util.List inherited methods

    @Override
    public int size() {
        return getChildren().size();
    }

    @Override
    public boolean isEmpty() {
        return getChildren().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getChildren().contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return getChildren().iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return getChildren().toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return getChildren().toArray(a);
    }

    @Override
    public boolean add(T t) {
        return getChildren().add(t);
    }

    @Override
    public boolean remove(Object o) {
        return getChildren().remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return getChildren().containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return getChildren().addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return getChildren().addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return getChildren().removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return getChildren().retainAll(c);
    }

    @Override
    public void clear() {
        getChildren().clear();
    }

    @Override
    public T get(int index) {
        return getChildren().get(index);
    }

    @Override
    public T set(int index, T element) {
        return getChildren().set(index, element);
    }

    @Override
    public void add(int index, T element) {
        getChildren().add(index, element);
    }

    @Override
    public T remove(int index) {
        return getChildren().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return getChildren().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getChildren().lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return getChildren().listIterator();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return getChildren().listIterator(index);
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return getChildren().subList(fromIndex, toIndex);
    }
}
