package net.dean.jraw.docs

import net.dean.jraw.Version

class DocLinkGenerator {
    fun generate(name: String): String {
        val clazz = ProjectTypeFinder.from(name) ?:
            throw IllegalArgumentException("No JRAW classes with (simple) name '$name'")
        return generate(clazz)
    }

    fun generate(clazz: Class<*>): String {
        return BASE + clazz.name.replace('.', '/') + ".html"
    }

    companion object {
        private val BASE = "https://jitpack.io/com/github/mattbdean/JRAW/v${Version.get()}/javadoc/"
    }
}
