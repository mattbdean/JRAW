package net.dean.jraw.databind

import com.squareup.moshi.JsonQualifier
import java.util.concurrent.TimeUnit

/**
 * Attach this annotation to a class property and the [UnixDateAdapterFactory] will pick it up.
 *
 * @property precision Source/target time unit. Defaults to seconds because that's what reddit uses.
 */
@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class UnixTime(val precision: TimeUnit = TimeUnit.SECONDS)
