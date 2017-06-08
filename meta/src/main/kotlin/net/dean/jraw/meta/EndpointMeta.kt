package net.dean.jraw.meta

import java.lang.reflect.Method

data class EndpointMeta(
    val implementation: Method,
    val sourceUrl: String
)

