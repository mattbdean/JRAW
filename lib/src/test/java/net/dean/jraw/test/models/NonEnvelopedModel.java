package net.dean.jraw.test.models;

import net.dean.jraw.databind.RedditModel;

@RedditModel(enveloped = false)
public final class NonEnvelopedModel {
    public final String a;

    public NonEnvelopedModel(String a) {
        this.a = a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NonEnvelopedModel that = (NonEnvelopedModel) o;

        return a != null ? a.equals(that.a) : that.a == null;
    }

    @Override
    public int hashCode() {
        return a != null ? a.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "NonEnvelopedModel{" +
            "a='" + a + '\'' +
            '}';
    }
}
