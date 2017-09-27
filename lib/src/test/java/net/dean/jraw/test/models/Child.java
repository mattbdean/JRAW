package net.dean.jraw.test.models;

import net.dean.jraw.databind.RedditModel;

import java.util.Objects;

@RedditModel
public final class Child implements Parent {
    public final String a;

    public Child(String a) {
        this.a = a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Child foo = (Child) o;

        return Objects.equals(a, foo.a);
    }

    @Override
    public int hashCode() {
        return a.hashCode();
    }

    @Override
    public String toString() {
        return "Child{" +
            "a=" + a +
            '}';
    }
}
