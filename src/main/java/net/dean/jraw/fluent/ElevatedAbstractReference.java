package net.dean.jraw.fluent;

/**
 * This class provides a constructor that requires a {@link ManagerAggregation} instead of a
 * {@link net.dean.jraw.RedditClient}, like what {@link AbstractReference} does. This aggregation is visible to
 * subclasses through the {@link #managers} field.
 */
public class ElevatedAbstractReference extends AbstractReference {
    protected final ManagerAggregation managers;

    /**
     * Instantiates a new ElevatedAbstractReference
     * @param managers A manager aggregation. Must not be null.
     */
    protected ElevatedAbstractReference(ManagerAggregation managers) {
        super(managers.reddit());
        if (managers == null)
            throw new NullPointerException("managers cannot be null");
        this.managers = managers;
    }
}
