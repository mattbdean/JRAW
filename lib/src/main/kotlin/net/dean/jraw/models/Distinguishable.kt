package net.dean.jraw.models

/**
 * Common interface for models that can show the status of the user that created them.
 *
 * @see DistinguishedStatus
 */
interface Distinguishable {
    // Because of how the way the Kotlin Jackson module works, this property has to be marked as nullable even though
    // its deserializer always returns a non-null value. Jackson sees that the "distinguished" property is null and
    // immediately throws an Exception
    /** The status of the person who created this Submission. Always non-null */
    val distinguished: DistinguishedStatus?
}
