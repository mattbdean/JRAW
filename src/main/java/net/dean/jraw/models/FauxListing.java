package net.dean.jraw.models;

import com.google.common.collect.ImmutableList;

/**
 * This class is a "hacky" way to represent a Listing
 * @param <T> The type of elements that will be in this listing
 */
public final class FauxListing<T extends RedditObject> extends Listing<T> {
    private final ImmutableList<T> children;
    private final String before;
    private final String after;
    private final String modhash;
    private final More more;

    public FauxListing(ImmutableList<T> children, String before, String after, String modhash) {
        this(children, before, after, modhash, null);
    }

    public FauxListing(ImmutableList<T> children, String before, String after, String modhash, More more) {
        super(null, null);
        this.children = children;
        this.before = before;
        this.after = after;
        this.more = more;
        this.modhash = modhash;
    }

    @Override
    protected ImmutableList<T> initChildren() {
        return children;
    }

    @Override
    protected More initMore() {
        return more;
    }

    @Override
    public ImmutableList<T> getChildren() {
        return children;
    }

    @Override
    public String getBefore() {
        return before;
    }

    @Override
    public String getAfter() {
        return after;
    }

    @Override
    public More getMoreChildren() {
        return more;
    }

    @Override
    public String getModhash() {
        return modhash;
    }
}
