package net.dean.jraw.models

/** Exists solely for the purpose of [net.dean.jraw.pagination.Stream] */
interface UniquelyIdentifiable {
    val uniqueId: String
}
