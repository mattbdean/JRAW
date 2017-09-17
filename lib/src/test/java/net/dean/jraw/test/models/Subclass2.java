package net.dean.jraw.test.models;

import net.dean.jraw.databind.Enveloped;
import net.dean.jraw.databind.RedditModel;
import net.dean.jraw.models.Listing;

@RedditModel
public class Subclass2 implements GenericParentType {
    public String baz;
    public String qux;

    @Enveloped
    public Listing<Subclass1> replies;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Subclass2 subclass2 = (Subclass2) o;

        if (baz != null ? !baz.equals(subclass2.baz) : subclass2.baz != null) return false;
        if (qux != null ? !qux.equals(subclass2.qux) : subclass2.qux != null) return false;
        return replies != null ? replies.equals(subclass2.replies) : subclass2.replies == null;
    }

    @Override
    public int hashCode() {
        int result = baz != null ? baz.hashCode() : 0;
        result = 31 * result + (qux != null ? qux.hashCode() : 0);
        result = 31 * result + (replies != null ? replies.hashCode() : 0);
        return result;
    }
}
