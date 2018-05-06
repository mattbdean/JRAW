package net.dean.jraw.pagination

/**
 * This class is a very simple data structure which functions similarly to a LRU cache with no max age, but with a fixed
 * size. Until capacity is reached, adding new data functions exactly like a normal list. However, once the capacity has
 * been reached, new data is added at index 0, then to 1, 2, etc, overwriting older data.
 *
 * @param capacity The maximum number of elements to store
 */
internal class RotatingSearchList<T>(val capacity: Int) {
    // All are internal for testing purposes only
    internal val backingArray: Array<Any?> = arrayOfNulls(capacity)
    internal var currentIndex = 0
    private var _size = 0

    /** The amount of elements currently being stored */
    val size: Int
        get() = _size

    /**
     * Adds some data. Returns whatever data was overwritten by this call.
     */
    fun add(data: T): T? {
        @Suppress("UNCHECKED_CAST")
        val overwrittenData: T? = backingArray[currentIndex] as T?

        backingArray[currentIndex] = data

        if (++currentIndex >= backingArray.size)
            currentIndex = 0

        // If we haven't overwritten anything, then we've added new data
        if (overwrittenData == null)
            _size++

        return overwrittenData
    }

    /**
     * Checks if some data is currently being stored. This function assumes the data is likely to have been inserted
     * recently
     */
    fun contains(data: T): Boolean {
        // Start at currentIndex because in our case it's more likely that the data we're looking for (if it's in here)
        // is going to be added more recently
        for (i in 0 until size) {
            // We have to add backingArray.size here because Java does not do the mod operation properly (at least
            // according to the mathematical definition of mod). In Python: -1 % 5 --> 4. In Java: -1 % 5 --> -1. We
            // want the Python result, and to ensure that, we have to add backingArray.size (5 in the previous example).
            // (-1 + 5) % 5 --> 4 in both languages.
            val index = (currentIndex - 1 - i + backingArray.size) % backingArray.size
            if (backingArray[index] == data) {
                return true
            }
        }

        return false
    }
}
