package net.dean.jraw.docs

import com.github.rjeschke.txtmark.BlockEmitter

class CodeBlockEmitter(private val codeSamples: List<CodeSampleRef>) : BlockEmitter {
    override fun emitBlock(out: StringBuilder, lines: MutableList<String>, meta: String) {
        var codeSample: CodeSampleRef? = null

        // Test to see if the code block is referencing a CodeSample
        if (meta.trim().startsWith("@")) {
            val name = meta.trim().substring(1)
            codeSample = codeSamples.firstOrNull { it.name == name } ?:
                throw IllegalStateException("No code sample with name '$name'")
        }

        // Use 'nohighlight' when no language is specified to prevent highlight.js from guessing
        val lang = if (meta.isBlank()) "nohighlight" else if (codeSample != null) "java" else meta

        // Use the code sample's lines if applicable
        val actualLines = if (codeSample == null) lines else codeSample.content

        with (out) {
            // Write the code block
            appendln("""<div class="code-container"><pre><code class="$lang">""")
            for (line in actualLines) {
                appendln(naiveHtmlEscape(line))
            }
            appendln("""</code></pre></div>""")
        }
    }

    private fun naiveHtmlEscape(str: String) =
        // It works I guess
        str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
}
