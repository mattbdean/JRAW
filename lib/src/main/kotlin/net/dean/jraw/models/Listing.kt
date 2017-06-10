package net.dean.jraw.models

data class Listing<out T : Thing>(
    val after: String?,
    val before: String?,
    val children: List<T>
)
