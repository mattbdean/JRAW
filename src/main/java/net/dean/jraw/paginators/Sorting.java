package net.dean.jraw.paginators;

/**
 * Represents how the reddit API chooses what it returns in a Paginator
 */
public enum Sorting {
    HOT,
    NEW,
    RISING,
    CONTROVERSIAL(true),
    TOP(true);

    private boolean requiresTimePeriod;

    Sorting() { this(false); }

    Sorting(boolean requiresTimePeriod) {
        this.requiresTimePeriod = requiresTimePeriod;
    }

    public boolean requiresTimePeriod() { return requiresTimePeriod; }
}
