package net.dean.jraw.models

/**
 * A Listing is how reddit handles pagination.
 *
 * A Listing has three main parts: the fullnames of the items before and after, and its children. As a convenience,
 * Listing inherits from `kotlin.collections.List` and those methods are delegated to [children]. That means that
 * `listing.indexOf(foo)` is the same as `listing.children.indexOf(foo)`. Note that Kotlin lists are immutable by
 * default.
 */
data class Listing<T : RedditObject>(
    val after: String?,
    val before: String?,
    val children: List<T>
) : List<T> {
    // kotlin.collections.List inherited methods and properties
    override val size: Int = children.size
    override fun contains(element: T) = children.contains(element)
    override fun containsAll(elements: Collection<T>) = children.containsAll(elements)
    override operator fun get(index: Int): T = children[index]
    override fun indexOf(element: T) = children.indexOf(element)
    override fun isEmpty() = children.isEmpty()
    override fun iterator(): Iterator<T> = children.iterator()
    override fun lastIndexOf(element: T) = children.lastIndexOf(element)
    override fun listIterator(): ListIterator<T> = children.listIterator()
    override fun listIterator(index: Int): ListIterator<T> = children.listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int): List<T> = children.subList(fromIndex, toIndex)

    override fun toString(): String {
        return "Listing(after=$after, before=$before, children=List(size=${children.size}), size=$size)"
    }
}
