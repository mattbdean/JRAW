@file:JvmName("Extensions")

package net.dean.jraw

internal fun <K, V> Map<K, V?>.filterValuesNotNull(): Map<K, V> {
    return this
        .filterValues { it != null }
        .mapValues { it.value ?: throw IllegalStateException("should not have been thrown") }
}
