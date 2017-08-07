package net.dean.jraw.docs

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
            actualLines = codeSample.content.toMutableList()

            // Attempt to find all JRAW classes used in the code sample
            val typesAndPositions = ProjectTypeFinder.find(codeSample)

            // Add a link to all JRAW types to their respective documentations
            for (range in typesAndPositions.keys.sortedBy { it.begin }.reversed()) {
                // Assume that the Range begins and ends on the same line. There shouldn't be a case where a name is on
                // two separate lines. Also note that Ranges are 1-indexed, so the first character of the first line is
                // represented as Position(1, 1).
                val lineIndex = range.begin.line - 1
                var line = actualLines[lineIndex]

                // Replace the range with a generated documentation link
                line = line.substring(0, range.begin.column - 1) +
                    doc.linkFor(typesAndPositions[range]!!) +
                    line.substring(range.end.column)
                actualLines[lineIndex] = line
            }
        }

        // Use 'nohighlight' when no language is specified to prevent highlight.js from guessing
        val arguments = if (meta.isBlank()) listOf("nohighlight") else meta.trim().split("|")

        val lang = if (codeSample != null) "java" else arguments[0]
        val escapeHtml = arguments.size > 1 && arguments[1] == "escapeHtml"

        // Write the code block
        out.append("""<div class="code-container"><pre><code class="$lang">""")
        actualLines
            // Only escape if the language is XML or HTML
            .map { if (escapeHtml) naiveHtmlEscape(it) else it }
            .forEach { out.appendln(it) }
        out.appendln("""</code></pre></div>""")
    }

    private fun naiveHtmlEscape(str: String) =
        // It works I guess
        str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
}

