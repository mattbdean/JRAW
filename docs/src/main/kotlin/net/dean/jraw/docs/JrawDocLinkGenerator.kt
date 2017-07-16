package net.dean.jraw.docs

class JrawDocLinkGenerator : DocLinkGenerator {
    // TODO: Update when we actually generate Javadoc
    override val base: String = "#"

    override fun linkFor(clazz: Class<*>): String {
        return """<a href="$base${clazz.name.replace('.', '/')}.html" class="doc-link" title="Documentation for ${clazz.name}">${clazz.simpleName}</a>"""
    }
}
