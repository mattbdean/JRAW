package net.dean.jraw.docs

import com.github.rjeschke.txtmark.SpanEmitter
import java.lang.StringBuilder

class TypeReferenceLinkEmitter(private val doc: DocLinkGenerator) : SpanEmitter {
    override fun emitSpan(out: StringBuilder, content: String) {
        // Content will be something like "@ClassName", see if "ClassName" is a JRAW type
        val name = content.trim().substring(1)
        val clazz = ProjectTypeFinder.fromSimpleName(name) ?:
            throw IllegalStateException("No JRAW class has simple name matching '$name'")
        out.append(doc.linkFor(clazz))
    }
}
