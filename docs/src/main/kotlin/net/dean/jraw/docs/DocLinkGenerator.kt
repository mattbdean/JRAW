package net.dean.jraw.docs

class DocLinkGenerator {
    fun generate(simpleName: String): String {
        val clazz = ProjectTypeFinder.fromSimpleName(simpleName) ?:
            throw IllegalArgumentException("No JRAW classes with simple name '$simpleName'")

        return BASE + clazz.name.replace('.', '/') + ".html"
    }

    companion object {
        // TODO update when we actually update docs
        private const val BASE = "#"
    }
}
