package net.dean.jraw.docs

import java.io.File

class PageCompiler(private val linkGenerator: DocLinkGenerator, private val codeSamples: List<CodeSampleRef>) {
    private val _unusedSamples: MutableList<CodeSampleRef> = ArrayList(codeSamples)

    val unusedSamples: List<CodeSampleRef>
        get() = _unusedSamples

    fun compile(file: File): List<String> = compile(file.readLines())

    fun compile(text: List<String>): List<String> {
        return text.map {
            // Map each line to a list of lines. This is necessary since compiling a sample will produce multiple lines
            when {
                // Only one sample supported per line
                sampleRegex.matches(it.trim()) -> {
                    val result = sampleRegex.find(it)!!
                    val sampleName = result.groupValues[1]

                    val sample = codeSamples.firstOrNull { it.name == sampleName } ?:
                        throw IllegalArgumentException("No code sample named '$sampleName'")

                    _unusedSamples.remove(sample)

                    val lines: MutableList<String> = ArrayList(sample.content)
                    // Use GitHub flavored Markdown for code
                    lines.add(0, "```java")
                    lines.add("```")

                    lines
                }
                // Multiple links are supported on one line
                linkRegex.containsMatchIn(it) -> {
                    val replaced = linkRegex.replace(it) { match ->
                        // groupValues[0] is the input string
                        val name = match.groupValues[1]
                        val clazz = ProjectTypeFinder.from(name)
                            ?: throw IllegalArgumentException("No JRAW type with (simple) name $name")

                        val link = linkGenerator.generate(clazz)
                        "[${clazz.simpleName}]($link)"
                    }
                    listOf(replaced)
                }
                else -> listOf(it)
            }
        }.flatten() // Flatten the List<List<String>> into a List<String>
    }

    companion object {
        // https://regexr.com/3goec
        private val sampleRegex = Regex("\\{\\{ ?(\\w+\\.\\w+) ?}}")

        // https://regexr.com/3gto2
        private val linkRegex = Regex("\\[\\[@((?:\\w+\\.)*\\w+)]]")
    }
}
