package net.dean.jraw.databind

import com.squareup.moshi.JsonQualifier

/**
 * Put this annotation on a class member to have it picked up by [RedditModelAdapterFactory].
 */
@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class Enveloped
