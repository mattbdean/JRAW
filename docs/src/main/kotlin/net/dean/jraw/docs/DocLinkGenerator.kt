package net.dean.jraw.docs

interface DocLinkGenerator {
    val base: String
    fun linkFor(clazz: Class<*>): String
}
