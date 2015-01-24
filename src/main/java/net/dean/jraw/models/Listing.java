package net.dean.jraw.models;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.meta.JsonProperty;
import net.dean.jraw.models.meta.Model;
import net.dean.jraw.models.meta.ModelManager;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.rmi.UnexpectedException;
import java.util.*;

/**
 * Represents a listing of Things. A Listing has four main keys: before, after, modhash, and its children. Listing uses
 * an {@link ArrayList} to implement the method inherited by {@link java.util.List}. Any method that attempts to
 * change the data externally (such as {@link List#remove(Object)}) will throw an UnsupportedOperationException.
 * List is only modifiable internally via loadMoreChildren.
 *
 * @param <T> The type of elements that will be in this listing
 * @author Matthew Dean
 */
@SuppressWarnings("deprecation")
@Model(kind = Model.Kind.LISTING)
public class Listing<T extends RedditObject> extends RedditObject implements List<T> {

    private final Class<T> thingClass;
    private final List<T> children;
    private More more;

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

    /**
     * Instantiates a new empty listing
     *
     * @param thingClass The class which will be the type of the children in this listing
     */
    public Listing(Class<T> thingClass) {
        super(getEmptyListingJSON());

        this.thingClass = thingClass;
        this.children = new ArrayList<>();
        this.more = null;
    }

    protected List<T> initChildren() {
        List<T> children = new ArrayList<>();

        // children is a JSON array
        for (JsonNode childNode : data.get("children")) {
            if (!childNode.get("kind").getTextValue().equalsIgnoreCase("more")) {
                children.add(ModelManager.create(childNode, thingClass));
            }
        }

        return children;
    }

    protected More initMore() {
        for (JsonNode childNode : data.get("children")) {
            if (childNode.get("kind").getTextValue().equalsIgnoreCase("more")) {
                return new More(childNode.get("data"));
            }
        }

        return null;
    }

    public List<T> getChildren() {
        return children;
    }

    /**
     * Loads more children into the Listing. The highest level modified will be commentRoot, or
     * parentSubmission is commentRoot is null. All loaded children are inserted into the comment tree.
     *
     * @param parentSubmission The submission all comments are under
     * @param commentRoot      If loading more comments, the parent comment of this listing or null if the parent is the submission
     * @param sort             How to sort the recieved comments
     */
    public void loadMoreChildren(RedditClient client, Submission parentSubmission
            , Comment commentRoot, CommentSort sort)
            throws NetworkException, ApiException, IllegalArgumentException {
        if (commentRoot != null) {
            //We're loading more comments in a thread
            if (commentRoot.getReplies() != this) {
                //The parent's replies should be this listing
                throw new IllegalArgumentException("commentRoot should be the direct parent of this listing!");
            }
        }

        List<Thing> loadedThings = client.getMoreThings(parentSubmission, sort, getMoreChildren());
        List<Comment> loadedComments = new ArrayList<>();
        List<More> loadedMores = new ArrayList<>();

        for (Thing t : loadedThings) {
            if (t instanceof Comment) {
                loadedComments.add((Comment) t);
            } else {
                loadedMores.add((More) t);
            }
        }

        formCommentTree(loadedComments, loadedMores);

        if (commentRoot == null) {
            //Add all of the comments to the submission
            for (Comment c : loadedComments) {
                parentSubmission.getComments().addLoaded(c);
            }
            if (loadedMores.size() > 0) {
                parentSubmission.getComments().setMoreChildren(loadedMores.get(0));
            } else {
                //More was just loaded
                parentSubmission.getComments().setMoreChildren(null);
            }
        } else {
            for (Comment c : loadedComments) {
                commentRoot.getReplies().addLoaded(c);
            }
            if (loadedMores.size() > 0) {
                commentRoot.getReplies().setMoreChildren(loadedMores.get(0));
            } else {
                //More was just loaded
                commentRoot.getReplies().setMoreChildren(null);
            }
        }
    }

    /**
     * Merge the Comments and Mores into a tree as far as possible.
     * There should only be at most 1 more object left at completion,
     * the more for the root of all the trees formed.
     *
     * @param comments The comments to organize into a tree
     * @param mores    The mores to add into the comment tree
     * @throws UnexpectedException More than 1 more was left over, should only be one for the root
     */
    public static void formCommentTree(List<Comment> comments, List<More> mores) throws IllegalArgumentException {
        List<Comment> toAdd = new ArrayList<>(comments);
        comments.clear();
        HashMap<String, Comment> commentMap = new HashMap<>();

        for (Comment c : toAdd) {
            commentMap.put(c.getFullName(), c);
        }

        while (toAdd.size() > 0) {
            Comment c = toAdd.get(0);
            Comment parent = commentMap.get(c.getParentId());
            if (parent == null) {
                //It's a base comment
                comments.add(c);
            } else {
                parent.getReplies().addLoaded(c);
            }
            toAdd.remove(c);
        }

        List<More> toRemove = new ArrayList<>();
        for (More more : mores) {
            Comment parent = commentMap.get(more.getParentId());
            if (parent != null) {
                parent.getReplies().setMoreChildren(more);
                toRemove.add(more);
            }
        }
        for (More more : toRemove) {
            mores.remove(more);
        }
        if (mores.size() > 1) {
            throw new IllegalArgumentException("Only 1 more object should be left");
        }
    }

    /**
     * Gets the "more" element (the last element in the children)
     *
     * @return A More object
     */
    @JsonProperty(nullable = true)
    public More getMoreChildren() {
        return more;
    }

    /**
     * Set the "more" element, for use when another loaded from the previous more
     *
     * @param more A more object
     */
    public void setMoreChildren(More more) {
        this.more = more;
    }

    /**
     * The full name of the Thing that follows before this page, or null if there is no previous page
     *
     * @return The full name of the Thing that comes before this one
     */
    @JsonProperty(nullable = true)
    public String getBefore() {
        return data("before");
    }

    /**
     * The full name of the Thing that follows after this page, or null if there is no following page
     *
     * @return The full name of the Thing that comes after this page
     */
    @JsonProperty(nullable = true)
    public String getAfter() {
        return data("after");
    }

    /**
     * Not the same modhash provided upon login. You can reuse the modhash given upon login
     *
     * @return A modhash
     */
    @JsonProperty
    public String getModhash() {
        return data("modhash");
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

    private void addLoaded(T object) {
        children.add(object);
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

    private static JsonNode getEmptyListingJSON() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode dataTable = mapper.createObjectNode();
        dataTable.putArray("children");
        dataTable.put("after", "");
        dataTable.put("before", "");
        dataTable.put("modhash", "");
        return dataTable;
    }

}
