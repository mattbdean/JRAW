package net.dean.jraw.models;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class DelegatedList<T> implements List<T>, Serializable {
    protected abstract List<T> getDelegatedList();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // java.util.List inherited methods

    @Override
    public int size() {
        return getDelegatedList().size();
    }

    @Override
    public boolean isEmpty() {
        return getDelegatedList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getDelegatedList().contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return getDelegatedList().iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return getDelegatedList().toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return getDelegatedList().toArray(a);
    }

    @Override
    public boolean add(T t) {
        return getDelegatedList().add(t);
    }

    @Override
    public boolean remove(Object o) {
        return getDelegatedList().remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return getDelegatedList().containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return getDelegatedList().addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return getDelegatedList().addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return getDelegatedList().removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return getDelegatedList().retainAll(c);
    }

    @Override
    public void clear() {
        getDelegatedList().clear();
    }

    @Override
    public T get(int index) {
        return getDelegatedList().get(index);
    }

    @Override
    public T set(int index, T element) {
        return getDelegatedList().set(index, element);
    }

    @Override
    public void add(int index, T element) {
        getDelegatedList().add(index, element);
    }

    @Override
    public T remove(int index) {
        return getDelegatedList().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return getDelegatedList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getDelegatedList().lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return getDelegatedList().listIterator();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return getDelegatedList().listIterator(index);
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return getDelegatedList().subList(fromIndex, toIndex);
    }
}
