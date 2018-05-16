@file:JvmName("Extensions")

package net.dean.jraw

import okhttp3.HttpUrl

internal fun <K, V> Map<K, V?>.filterValuesNotNull(): Map<K, V> {
    return this
        .filterValues { it != null }
        .mapValues { it.value ?: throw IllegalStateException("should not have been thrown") }
}

/**
 * Adds the key-value-pairs with non-null values to the query of this builder
 */
fun HttpUrl.Builder.addQueryParameters(query: Map<String, String?>): HttpUrl.Builder {
    for ((k, v) in query.filterValuesNotNull()) {
        addQueryParameter(k, v)
    }

    return this
}
