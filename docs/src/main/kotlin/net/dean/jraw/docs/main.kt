package net.dean.jraw.docs

import com.github.rjeschke.txtmark.BlockEmitter
import com.github.rjeschke.txtmark.Configuration
import com.github.rjeschke.txtmark.Processor
import org.jtwig.JtwigModel
import org.jtwig.JtwigTemplate
import java.io.File
import java.lang.StringBuilder

private const val SAMPLES_DIR_ARG = "--samples-dir"
private const val OUT_DIR_ARG = "--output-dir"
private const val RESOURCES_DIR_ARG = "--resources-dir"

fun main(args: Array<String>) {
    val cliArgs = parseArgs(args)

    // Establish a base of operations
    val samplesDir = File(cliArgs[SAMPLES_DIR_ARG])
    val outDir = File(cliArgs[OUT_DIR_ARG])
    val resourcesDir = File(cliArgs[RESOURCES_DIR_ARG])

    if (!outDir.isDirectory && !outDir.mkdirs())
        failAndExit("Could not `mkdir -p` for ${outDir.absolutePath}")

    // Find all Java source files in all subdirectories of the given source root
    val sourceFiles = walkRecursive(samplesDir).filter { it.name.endsWith(".java") }

    val samples = sourceFiles
        // Identify all CodeSamples from each Java source file
        .map { CodeSampleFinder.find(it) }
        // Merge the List<List<CodeSampleRef>> into a List<CodeSampleRef>
        .flatten()

    // Copy our assets wholesale to the build dir
    val assetsDir = File(resourcesDir, "assets")
    if (assetsDir.isDirectory)
        assetsDir.copyRecursively(outDir, overwrite = true, onError = {
            f, e -> failAndExit("Failed to copy asset $f: ${e.message}")
        })
    else
        System.err.println("Warning: no assets found or not a directory: ${assetsDir.absolutePath}")

    val conf = Configuration.builder()
        // Use our custom code block emitter
        .setCodeBlockEmitter(CodeBlockEmitter(samples))
        // Force txtmark to recognize fenced code blocks
        .forceExtentedProfile()
        .build()

    // Load our Jtwig template
    val template = JtwigTemplate.fileTemplate(File(resourcesDir, "template.twig"))

    val contentDir = File(resourcesDir, "content")

    // Attempt to render each markdown file into memory to catch any errors before persisting to disk
    val outputMapping = contentDir.listFiles { _, name -> name.endsWith(".md") }.map {
        // Render the markdown file into a String
        val html = Processor.process(it, conf)

        val model = JtwigModel.newModel()
            .with("content", html)

        // Return a Pair mapping the Jtwig model to the final output destination
        model to File(outDir, it.nameWithoutExtension + ".html")
    }

    // Write the files to disk
    for ((model, outFile) in outputMapping) {
        template.render(model, outFile.outputStream())
        println("Wrote file $outFile")
    }
}

private fun parseArgs(args: Array<String>): Map<String, String> {
    if (args.size % 2 != 0)
        failAndExit("Expected an even number of arguments")

    val required = listOf(SAMPLES_DIR_ARG, OUT_DIR_ARG, RESOURCES_DIR_ARG)

    // Create a map where all even indexes (and 0) represent keys and all odd indexes represent values for the element
    // before it
    val allArgs = mapOf(*(args.indices step 2).map { args[it] to args[it + 1] }.toTypedArray())
    val filtered = mutableMapOf<String, String>()

    // Only return values we care about
    for (arg in required) {
        if (arg !in allArgs)
            failAndExit("Expected argument '$arg' to have a value")
        filtered.put(arg, allArgs[arg]!!)
    }

    return filtered
}

private fun failAndExit(msg: String, code: Int = 1): Nothing {
    System.err.println(msg)
    System.exit(code)

    // JVM will have exited by now, this is just for Kotlin
    throw Error()
}

/**
 * Simple function to recursively fina all files starting from a given access point
 */
fun walkRecursive(base: File): List<File> {
    val files = mutableListOf<File>()
    if (base.isDirectory) {
        base.listFiles()?.forEach {
            files.addAll(walkRecursive(it))
        } ?: throw IllegalStateException("Encounted I/O exception when walking directory $base")
    } else if (base.isFile) {
        files.add(base)
    }

    return files
}

private class CodeBlockEmitter(private val codeSamples: List<CodeSampleRef>) : BlockEmitter {
    override fun emitBlock(out: StringBuilder, lines: MutableList<String>, meta: String) {
        var codeSample: CodeSampleRef? = null

        // Test to see if the code block is referencing a CodeSample
        if (meta.trim().startsWith("@")) {
            val name = meta.trim().substring(1)
            codeSample = codeSamples.firstOrNull { it.name == name } ?:
                failAndExit("No code sample with name '$name'")
        }

        // Use 'nohighlight' when no language is specified to prevent highlight.js from guessing
        val lang = if (meta.isBlank()) "nohighlight" else if (codeSample != null) "java" else meta

        // Use the code sample's lines if applicable
        val actualLines = if (codeSample == null) lines else codeSample.content

        with (out) {
            // Write the code block
            append("""<div class="code-container"><pre><code class="$lang">""")
            for (line in actualLines) {
                appendln(naiveHtmlEscape(line))
            }
            append("""</code></pre></div>""")
        }
    }

    private fun naiveHtmlEscape(str: String) =
        // It works I guess
        str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
}
