package net.dean.jraw.models.core;

import net.dean.jraw.JrawUtils;
import net.dean.jraw.RedditObjectParser;
import net.dean.jraw.models.JsonInteraction;
import net.dean.jraw.models.RedditObject;
import net.dean.jraw.models.ThingType;
import org.codehaus.jackson.JsonNode;

import java.util.*;

/**
 * Represents a listing of Things. A Listing has four main keys: before, after, modhash, and its children. Listing uses
 * an ArrayList to implement the method inherited by {@link java.util.List}. Any method that attempts to change the data
 * (such as {@link List#remove(Object)}) will throw an UnsupportedOperationException.
 *
 * @param <T> The type of elements that will be in this listing
 * @author Matthew Dean
 */
public class Listing<T extends RedditObject> extends RedditObject implements List<T> {
    /**
     * The RedditObjectParser which will be used to parse JSON values into RedditObjects
     */
    private static final RedditObjectParser PARSER = new RedditObjectParser();

    /**
     * The class of the contents of the listing
     */
    private final Class<T> thingClass;

    private final List<T> children;
    private final More more;

    /**
     * Whether this listing contains a "more" element in its children
     */
    private final boolean hasChildren;

    /**
     * Instantiates a new listing
     *
     * @param dataNode   The node to get data from
     * @param thingClass The class which will be the type of the children in this listing
     */
    public Listing(JsonNode dataNode, Class<T> thingClass) {
        super(dataNode);

        this.thingClass = thingClass;
        this.hasChildren = data.has("children");
        this.children = initChildren();
        this.more = initMore();
    }

    private List<T> initChildren() {
        List<T> children = new ArrayList<>();

        // children is a JSON array
        try {
            for (JsonNode childNode : data.get("children")) {
                children.add(JrawUtils.parseJson(childNode, thingClass));
            }
        } catch (NullPointerException e) {
            JrawUtils.logger().error("NullPointerException", e);
        }

        return children;
    }

    private More initMore() {
        for (JsonNode childNode : data.get("children")) {
            if (childNode.get("kind").getTextValue().equalsIgnoreCase("more")) {
                return new More(childNode.get("data"));
            }
        }

        return null;
    }

    /**
     * Gets the "more" element (the last element in the children)
     *
     * @return A More object
     */
    @JsonInteraction(nullable = true)
    public More getMoreChildren() {
        return more;
    }

    /**
     * The full name of the Thing that follows before this page, or null if there is no previous page
     * @return The full name of the Thing that comes before this one
     */
    @JsonInteraction(nullable = true)
    public String getBefore() {
        return data("before");
    }

    /**
     * The full name of the Thing that follows after this page, or null if there is no following page
     * @return The full name of the Thing that comes after this page
     */
    @JsonInteraction(nullable = true)
    public String getAfter() {
        return data("after");
    }

    /**
     * Not the same modhash provided upon login. You can reuse the modhash given upon login
     * @return A modhash
     */
    @JsonInteraction
    public String getModhash() {
        return data("modhash");
    }

    @Override
    public ThingType getType() {
        return ThingType.LISTING;
    }

    private void onDataModified() {
        throw new UnsupportedOperationException("Listing children are not modifiable.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Listing listing = (Listing) o;

        return data.equals(listing.data);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + thingClass.hashCode();
        result = 31 * result + children.hashCode();
        result = 31 * result + (hasChildren ? 1 : 0);
        return result;
    }

    // java.util.List methods below

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return children.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return children.iterator();
    }

    @Override
    public Object[] toArray() {
        return children.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        return children.toArray(t1s);
    }

    @Override
    public boolean add(T t) {
        onDataModified();
        return false;
    }

    @Override
    public boolean remove(Object o) {
        onDataModified();
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return children.containsAll(objects);
    }

    @Override
    public boolean addAll(Collection<? extends T> ts) {
        onDataModified();
        return false;
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> ts) {
        onDataModified();
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        onDataModified();
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        onDataModified();
        return false;
    }

    @Override
    public void clear() {
        onDataModified();
    }

    @Override
    public T get(int i) {
        return children.get(i);
    }

    @Override
    public T set(int i, T t) {
        onDataModified();
        return null;
    }

    @Override
    public void add(int i, T t) {
        onDataModified();
    }

    @Override
    public T remove(int i) {
        onDataModified();
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return children.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return children.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return children.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return children.listIterator(i);
    }

    @Override
    public List<T> subList(int i, int i2) {
        return children.subList(i, i2);
    }

}
