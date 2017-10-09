package net.dean.jraw.docs

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
        // TODO update when we actually update docs
        private const val BASE = "#"
    }
}
