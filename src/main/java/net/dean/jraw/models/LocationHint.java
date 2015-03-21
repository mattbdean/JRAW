package net.dean.jraw.models;

import java.util.EnumMap;

/**
 * Hints at where some data in a tree structure might be. The "top" of the tree is the root node, while the "bottom" is
 * the deepest level in the tree. There will only be (at maximum) one instance of this class for every value in
 * {@link TraversalMethod}. Instances of this class are immutable.
 */
public final class LocationHint {
    private final TraversalMethod method;
    private static final EnumMap<TraversalMethod, LocationHint> instances = new EnumMap<>(TraversalMethod.class);

    private LocationHint(TraversalMethod method) {
        if (method == null)
            throw new NullPointerException("Traversal method cannot be null");
        this.method = method;
    }

    /** Creates a LocationHint with a custom TraversalMethod */
    public static LocationHint of(TraversalMethod method) {
        if (!instances.containsKey(method)) {
            LocationHint hint = new LocationHint(method);
            instances.put(method, hint);
            return hint;
        }

        return instances.get(method);
    }

    /** The data is near the top of the tree structure. Uses breadth-first traversal. */
    public static LocationHint nearTop() {
        return of(TraversalMethod.BREADTH_FIRST);
    }

    /** The data is near the bottom of the tree structure. Uses post-order traversal. */
    public static LocationHint nearBottom() {
        return of(TraversalMethod.POST_ORDER);
    }

    /** The data's location is unknown. Uses pre-order traversal. */
    public static LocationHint anywhere() {
        return of(TraversalMethod.PRE_ORDER);
    }

    TraversalMethod getTraversalMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationHint that = (LocationHint) o;

        return method == that.method;
    }

    @Override
    public int hashCode() {
        return method.hashCode();
    }

    @Override
    public String toString() {
        return "LocationHint {" +
                "method=" + method +
                '}';
    }
}
