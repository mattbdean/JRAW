package net.dean.jraw.models

/** A model is identifiable if it has a full name (like "t1_abc123") and an ID (like "abc123") */
interface Identifiable : UniquelyIdentifiable {
    val fullName: String

    /** The unique base 36 identifier given to this model by reddit */
    val id: String
}
