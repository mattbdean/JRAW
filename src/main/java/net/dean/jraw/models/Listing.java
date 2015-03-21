package net.dean.jraw.models;

import com.fasterxml.jackson.databind.JsonNode;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import net.dean.jraw.models.meta.ModelManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Represents a Listing: How Reddit returns paginated data. A Listing has three main keys: the fullnames of the items
 * before and after, and its children. Listing uses an {@link ArrayList} to implement the method inherited by
 * {@link java.util.List}. For all intents and purposes, Listing properties are immutable.
 *
 * @param <T> The type of elements that will be in this listing
 * @author Matthew Dean
 */
@Model(kind = Model.Kind.LISTING)
public class Listing<T extends RedditObject> extends RedditObject implements List<T> {
    private final Class<T> thingClass;
    private final List<T> children;
    private final String before;
    private final String after;
    private final MoreChildren moreChildren;

    /**
     * Instantiates a new Listing
     *
     * @param thingClass The class which will be the type of the children in this listing
     */
    public Listing(JsonNode dataNode, Class<T> thingClass) {
        this(thingClass,
                initChildren(dataNode, thingClass),
                property(dataNode, "before"),
                property(dataNode, "after"),
                initMore(dataNode));
    }

    /**
     * Instantiates a new empty Listing
     *
     * @param thingClass The class which will be the type of the children in this listing
     */
    public Listing(Class<T> thingClass) {
        this(thingClass, new ArrayList<T>(), null, null, null);
    }

    protected Listing(Class<T> thingClass, List<T> children, String before, String after, MoreChildren more) {
        super(null);
        this.thingClass = thingClass;
        this.children = children;
        this.before = before;
        this.after = after;
        this.moreChildren = more;
    }

    protected static String property(JsonNode data, String key) {
        if (!data.has(key)) {
            return null;
        }

        JsonNode val = data.get(key);
        if (val.isNull()) {
            return null;
        }

        return val.asText();
    }

    protected static <T extends JsonModel> List<T> initChildren(JsonNode data, Class<T> thingClass) {
        List<T> children = new ArrayList<>();

        // children is a JSON array
        for (JsonNode childNode : data.get("children")) {
            if (!childNode.get("kind").asText().equalsIgnoreCase("more")) {
                children.add(ModelManager.create(childNode, thingClass));
            }
        }

        return children;
    }

    protected static MoreChildren initMore(JsonNode data) {
        for (JsonNode childNode : data.get("children")) {
            if (childNode.get("kind").textValue().equalsIgnoreCase("more")) {
                return new MoreChildren(childNode.get("data"));
            }
        }

        return null;
    }

    public List<T> getChildren() {
        return children;
    }

    /** Gets the "more" element (the last element in the children) */
    @JsonProperty(nullable = true)
    public MoreChildren getMoreChildren() {
        return moreChildren;
    }

    /** Gets the fullname of the Thing that comes before this page, or null if there is no previous page */
    @JsonProperty(nullable = true)
    public String getBefore() {
        return before;
    }

    /** Gets the fullname of the Thing that follows after this page, or null if there is no following page */
    @JsonProperty(nullable = true)
    public String getAfter() {
        return after;
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

    @SuppressWarnings("unckecked")
    @Override
    public <T1> T1[] toArray(T1[] array) {
        return getChildren().toArray(array);
    }

    @Override
    public boolean add(T object) {
        throw new UnsupportedOperationException("A listing cannot be modified!");
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException("A listing cannot be modified!");
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return getChildren().containsAll(objects);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        throw new UnsupportedOperationException("A listing cannot be modified!");
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {
        throw new UnsupportedOperationException("A listing cannot be modified!");
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        throw new UnsupportedOperationException("A listing cannot be modified!");
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        throw new UnsupportedOperationException("A listing cannot be modified!");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("A listing cannot be modified!");
    }

    @Override
    public T get(int index) {
        return getChildren().get(index);
    }

    @Override
    public T set(int index, T object) {
        throw new UnsupportedOperationException("A listing cannot be modified!");
    }

    @Override
    public void add(int index, T object) {
        throw new UnsupportedOperationException("A listing cannot be modified!");
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException("A listing cannot be modified!");
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
