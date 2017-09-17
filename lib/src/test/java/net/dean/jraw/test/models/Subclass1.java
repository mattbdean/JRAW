package net.dean.jraw.test.models;

import net.dean.jraw.databind.RedditModel;

@RedditModel
public class Subclass1 implements GenericParentType {
    public int foo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subclass1 subclass1 = (Subclass1) o;

        return foo == subclass1.foo;
    }

    @Override
    public int hashCode() {
        return foo;
    }
}
