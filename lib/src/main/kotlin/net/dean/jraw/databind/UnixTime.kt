package net.dean.jraw.databind

import com.squareup.moshi.JsonQualifier
import java.util.concurrent.TimeUnit

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class UnixTime(val precision: TimeUnit = TimeUnit.SECONDS)
