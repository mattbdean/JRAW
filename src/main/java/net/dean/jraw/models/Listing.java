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

    /**
     * Instantiates a new Listing
     *
     * @param dataNode   The node to get data from
     * @param thingClass The class which will be the type of the children in this listing
     */
    public Listing(JsonNode dataNode, Class<T> thingClass) {
        super(dataNode);

        this.thingClass = thingClass;
        this.children = initChildren();
        this.more = initMore();
    }

    protected ImmutableList<T> initChildren() {
        ImmutableList.Builder<T> children = ImmutableList.<T>builder();

        // children is a JSON array
        for (JsonNode childNode : data.get("children")) {
            if (!childNode.get("kind").getTextValue().equalsIgnoreCase("more")) {
                children.add(JrawUtils.parseJson(childNode, thingClass));
            }
        }

        return children.build();
    }

    protected More initMore() {
        for (JsonNode childNode : data.get("children")) {
            if (childNode.get("kind").getTextValue().equalsIgnoreCase("more")) {
                return new More(childNode.get("data"));
            }
        }

        return null;
    }

    public ImmutableList<T> getChildren() {
        return children;
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
        return result;
    }

    // java.util.List methods below

    @Override
    public int size() {
        return getChildren().size();
    }

    @Override
    public boolean isEmpty() {
        return getChildren().isEmpty();
    }

    @Override
    public boolean contains(Object object) {
        return getChildren().contains(object);
    }

    @Override
    public Iterator<T> iterator() {
        return getChildren().iterator();
    }

    @Override
    public Object[] toArray() {
        return getChildren().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] array) {
        return getChildren().toArray(array);
    }

    @Override
    public boolean add(T object) {
        return getChildren().add(object);
    }

    @Override
    public boolean remove(Object object) {
        return getChildren().remove(object);
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return getChildren().containsAll(objects);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return getChildren().addAll(collection);
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {
        return getChildren().addAll(i, collection);
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        return getChildren().removeAll(objects);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        return getChildren().removeAll(objects);
    }

    @Override
    public void clear() {
        getChildren().clear();
    }

    @Override
    public T get(int index) {
        return getChildren().get(index);
    }

    @Override
    public T set(int index, T object) {
        return getChildren().set(index, object);
    }

    @Override
    public void add(int index, T object) {
        getChildren().add(index, object);
    }

    @Override
    public T remove(int index) {
        return getChildren().remove(index);
    }

    @Override
    public int indexOf(Object object) {
        return getChildren().indexOf(object);
    }

    @Override
    public int lastIndexOf(Object object) {
        return getChildren().lastIndexOf(object);
    }

    @Override
    public ListIterator<T> listIterator() {
        return getChildren().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return getChildren().listIterator(index);
    }

    @Override
    public List<T> subList(int start, int end) {
        return getChildren().subList(start, end);
    }

}
