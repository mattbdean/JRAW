package net.dean.jraw.models;

import com.google.common.collect.ImmutableList;
import net.dean.jraw.JrawUtils;
import org.codehaus.jackson.JsonNode;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Represents a listing of Things. A Listing has four main keys: before, after, modhash, and its children. Listing uses
 * an {@link ImmutableList} to implement the method inherited by {@link java.util.List}. Any method that attempts to
 * change the data (such as {@link List#remove(Object)}) will throw an UnsupportedOperationException.
 *
 * @param <T> The type of elements that will be in this listing
 * @author Matthew Dean
 */
@SuppressWarnings("deprecation")
public class Listing<T extends RedditObject> extends RedditObject implements List<T> {

    private final Class<T> thingClass;
    private final ImmutableList<T> children;
    private final More more;
    private final boolean hasChildren;

    /**
     * Instantiates a new Listing
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

    private ImmutableList<T> initChildren() {
        ImmutableList.Builder<T> children = ImmutableList.<T>builder();

        // children is a JSON array
        for (JsonNode childNode : data.get("children")) {
            if (!childNode.get("kind").getTextValue().equalsIgnoreCase("more")) {
                children.add(JrawUtils.parseJson(childNode, thingClass));
            }
        }

        return children.build();
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
    public boolean contains(Object object) {
        return children.contains(object);
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
    public <T1> T1[] toArray(T1[] array) {
        return children.toArray(array);
    }

    @Override
    public boolean add(T object) {
        return children.add(object);
    }

    @Override
    public boolean remove(Object object) {
        return children.remove(object);
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return children.containsAll(objects);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return children.addAll(collection);
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {
        return children.addAll(i, collection);
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        return children.removeAll(objects);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        return children.removeAll(objects);
    }

    @Override
    public void clear() {
        children.clear();
    }

    @Override
    public T get(int index) {
        return children.get(index);
    }

    @Override
    public T set(int index, T object) {
        return children.set(index, object);
    }

    @Override
    public void add(int index, T object) {
        children.add(index, object);
    }

    @Override
    public T remove(int index) {
        return children.remove(index);
    }

    @Override
    public int indexOf(Object object) {
        return children.indexOf(object);
    }

    @Override
    public int lastIndexOf(Object object) {
        return children.lastIndexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() {
        return children.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return children.listIterator(index);
    }

    @Override
    public List<T> subList(int start, int end) {
        return children.subList(start, end);
    }

}
