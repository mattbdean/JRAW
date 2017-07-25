package net.dean.jraw.docs

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.rjeschke.txtmark.BlockEmitter

class CodeBlockEmitter(private val codeSamples: List<CodeSampleRef>, private val doc: DocLinkGenerator) : BlockEmitter {
    override fun emitBlock(out: StringBuilder, lines: MutableList<String>, meta: String) {
        var codeSample: CodeSampleRef? = null
        var actualLines = lines

        // Test to see if the code block is referencing a CodeSample
        if (meta.trim().startsWith("@")) {
            val name = meta.trim().substring(1)
            codeSample = codeSamples.firstOrNull { it.name == name } ?:
                throw IllegalStateException("No code sample with name '$name'")

            // Attempt to find all JRAW classes used in the code sample
            val types = findJrawTypesIncludedIn(codeSample)

            // Add a link to all JRAW types to their respective documentations
            actualLines = codeSample.content.map {
                var line = it
                for (type in types)
                    line = line.replace(type.simpleName, doc.linkFor(type))
                line
            }.toMutableList()
        }

        // Use 'nohighlight' when no language is specified to prevent highlight.js from guessing
        val arguments = if (meta.isBlank()) listOf("nohighlight") else meta.trim().split("|")

        val lang = if (codeSample != null) "java" else arguments[0]
        val escapeHtml = arguments.size > 1 && arguments[1] == "escapeHtml"

        with (out) {
            // Write the code block
            append("""<div class="code-container"><pre><code class="$lang">""")
            actualLines
                // Only escape if the language is XML or HTML
                .map { if (escapeHtml) naiveHtmlEscape(it) else it }
                .forEach { appendln(it) }
            appendln("""</code></pre></div>""")
        }
    }

    private fun naiveHtmlEscape(str: String) =
        // It works I guess
        str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    private fun findJrawTypesIncludedIn(code: CodeSampleRef): List<Class<*>> {
        // Surround the code in braces so that we can parse it as a block of code. Make sure the closing brace is on a
        // new line so that a comment on the last line doesn't accidentally comment it out
        val block = JavaParser.parseBlock("{" + code.content.joinToString("\n") + "\n}")
        val ni = JavaTypeFinder()
        ni.visit(block, null)
        return JrawTypeFinder.jrawTypes.filter { it.simpleName in ni.foundNames }
    }

    /**
     * Records all SimpleNames that are likely to represent Java classes or interfaces encountered when visiting an AST
     */
    private class JavaTypeFinder : VoidVisitorAdapter<Void>() {
        val foundNames: MutableSet<String> = mutableSetOf()

        override fun visit(n: SimpleName, arg: Void?) {
            if (isProbableType(n))
                foundNames.add(n.asString())
            super.visit(n, arg)
        }

        /**
         * Performs a simple check to see if the given SimpleName is likely a Java type and not a variable name. Returns
         * true if and only if the first character of the name is uppercase. This only works if the code is following
         * Java conventions of using UpperCamelCase for type names and lowerCamelCase for variable names.
         */
        private fun isProbableType(n: SimpleName) = n.asString().first().isUpperCase()
    }
}

