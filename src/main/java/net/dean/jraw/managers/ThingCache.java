package net.dean.jraw.managers;

import net.dean.jraw.models.Thing;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Maintains a map of Thing names and Things inside of WeakReferences.
 * ThingManager is a singleton and can be accessed anywhere.
 *
 * @author Phani Gaddipati
 */
public class ThingCache {

    private static ThingCache instance = null;

    /**
     * Whether the manager should be storing references.
     */
    private boolean enabled = false;

    /**
     * ThingManager is a singleton.
     */
    private ThingCache() {
    }

    /**
     * A map associating Things to thing full names.
     * Soft references used to avoid a build up of unused objects.
     */
    private HashMap<String, WeakReference<Thing>> thingMap = new HashMap<>();

    /**
     * Add a Thing to the map if the map is enabled,
     * and the thing has a full name.
     *
     * @param thing The thing to add
     * @return whether the Thing was added
     */
    public boolean addThing(Thing thing) {
        if (this.isEnabled()) {
            if (thing.getDataNode() != null
                    && thing.getDataNode().has("name")) {
                this.thingMap.put(thing.getFullName(), new WeakReference<>(thing));
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a Thing's WeakReference, if it exists.
     *
     * @param thing The thing to remove
     */
    public void removeThing(Thing thing) {
        this.thingMap.remove(thing.getFullName());
    }

    /**
     * If it exists, return the Thing with the given full name.
     *
     * @param fullName The name to look for
     * @return The associated Thing or null if it doesn't exist
     */
    public Thing getThing(String fullName) {
        if (this.thingMap.containsKey(fullName)) {
            if (this.thingMap.get(fullName).get() == null) {
                //SoftReference no longer references anything, remove key
                this.thingMap.remove(fullName);
            } else {
                return this.thingMap.get(fullName).get();
            }
        }
        return null;
    }

    /**
     * Clear the map of all references.
     */
    public void clearMap() {
        this.thingMap.clear();
    }

    /**
     * Check if ThingManager is updated.
     *
     * @return Whether ThingManager is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enable or disable the updating of ThingMap.
     *
     * @param isEnabled Enable or disable ThingMap
     */
    public void setEnabled(boolean isEnabled) {
        this.enabled = isEnabled;
    }

    /**
     * Obtain an instance of ThingManager.
     *
     * @return An instance of ThingManager
     */
    public static ThingCache get() {
        if (instance == null) {
            instance = new ThingCache();
        }
        return instance;
    }

}
