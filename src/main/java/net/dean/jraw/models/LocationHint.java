package net.dean.jraw.models;

import java.util.EnumMap;

/**
 * Hints at where some data in a tree structure might be. The "top" of the tree is the root node, while the "bottom" is
 * the deepest level in the tree. Instances of this class are immutable.
 */
public final class LocationHint {
    private final TraversalMethod method;
    private static final EnumMap<TraversalMethod, LocationHint> instances = new EnumMap<>(TraversalMethod.class);

    private LocationHint(TraversalMethod method) {
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
}
