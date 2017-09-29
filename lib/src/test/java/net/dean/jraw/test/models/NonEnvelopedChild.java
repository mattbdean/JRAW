package net.dean.jraw.test.models;

import net.dean.jraw.databind.RedditModel;

@RedditModel(enveloped = false)
public final class NonEnvelopedChild {
    public final String a;

    public NonEnvelopedChild(String a) {
        this.a = a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NonEnvelopedChild that = (NonEnvelopedChild) o;

        return a != null ? a.equals(that.a) : that.a == null;
    }

    @Override
    public int hashCode() {
        return a != null ? a.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "NonEnvelopedChild{" +
            "a='" + a + '\'' +
            '}';
    }
}
