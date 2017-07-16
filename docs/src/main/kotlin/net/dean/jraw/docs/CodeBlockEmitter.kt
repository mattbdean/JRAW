package net.dean.jraw.docs

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.rjeschke.txtmark.BlockEmitter
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder

class CodeBlockEmitter(private val codeSamples: List<CodeSampleRef>) : BlockEmitter {
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
                    line = line.replace(type.simpleName, """<a href="$JAVADOC_BASE${type.name.replace('.', '/')}.html" class="doc-link" title="Documentation for ${type.name}">${type.simpleName}</a>""")
                line
            }.toMutableList()
        }

        // Use 'nohighlight' when no language is specified to prevent highlight.js from guessing
        val lang = if (meta.isBlank()) "nohighlight" else if (codeSample != null) "java" else meta

        with (out) {
            // Write the code block
            append("""<div class="code-container"><pre><code class="$lang">""")
            actualLines
                // Only escape if the language is XML or HTML
                .map { if (lang == "xml" || lang == "html") naiveHtmlEscape(it) else it }
                .forEach { appendln(it) }
            appendln("""</code></pre></div>""")
        }
    }

    private fun naiveHtmlEscape(str: String) =
        // It works I guess
        str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    private fun findJrawTypesIncludedIn(code: CodeSampleRef): List<Class<*>> {
        val block = JavaParser.parseBlock("{" + code.content.joinToString("\n") + "}")
        val ni = JavaTypeFinder()
        ni.visit(block, null)

        return jrawTypes.filter { it.simpleName in ni.foundNames }
    }

    companion object {
        private val jrawTypes by lazy { findJrawClasses() }

        private val reflections = Reflections(ConfigurationBuilder()
            .setScanners(SubTypesScanner(false))
            .setUrls(ClasspathHelper.forPackage("net.dean.jraw"))
        )

        private val ignoredPackages = listOf(
            "net.dean.jraw.docs",
            "net.dean.jraw.docs.samples"
        )

        private fun findJrawClasses(): List<Class<*>> {
            val list = reflections.getSubTypesOf(Object::class.java)
            return list.filter { it.`package`.name !in ignoredPackages }.sortedBy { it.name }
        }
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

