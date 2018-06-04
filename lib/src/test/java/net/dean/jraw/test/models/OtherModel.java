package net.dean.jraw.test.models;

import java.util.Objects;

public final class OtherModel {
    public final String a;

    public OtherModel(String a) {
        this.a = a;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtherModel that = (OtherModel) o;
        return Objects.equals(a, that.a);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a);
    }

    @Override
    public String toString() {
        return "OtherModel {" +
            "a='" + a + '\'' +
            '}';
    }
}
