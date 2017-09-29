package net.dean.jraw.docs

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okio.Okio
import java.io.File
import java.io.IOException

class BookBuilder(samplesDir: File, private val contentDir: File) {
    private val samples = CodeSampleFinder.findAll(samplesDir)
    private val moshi = Moshi.Builder().build()

    private val compiler = PageCompiler(DocLinkGenerator(), samples)

    val unusedSamples: List<CodeSampleRef>
        get() = compiler.unusedSamples

    private fun compile(pages: List<Chapter>): Map<String, List<String>> {
        val data: HashMap<String, List<String>> = HashMap()
        data.put("SUMMARY.md", createSummaryFileContents(pages))

        pages
            .map { it.file + ".md" }
            .forEach { data.put(it, compiler.compile(File(contentDir, it))) }

        return data
    }

    private fun createSummaryFileContents(chapters: List<Chapter>): List<String> {
        val sb = StringBuilder()
        sb.appendln("# Summary\n")

        for (chapter in chapters) {
            sb.appendln("* [${chapter.title}](${chapter.file}.md)")
        }

        return sb.split("\n")
    }

    fun build(outputDir: File) {
        // Read the table of contents
        val tocFile = File(contentDir, "toc.json")
        if (!tocFile.isFile)
            throw IOException("Does not exist or not a file: ${tocFile.absolutePath}")

        val chapterAdapter = moshi.adapter<List<Chapter>>(Types.newParameterizedType(List::class.java, Chapter::class.java))
            .failOnUnknown()
            .nullSafe()

        val pages = chapterAdapter.fromJson(Okio.buffer(Okio.source(tocFile)))!!
        val compiled = compile(pages)

        if (!outputDir.isDirectory && !outputDir.mkdirs())
            throw IOException("Unable to create directories ${outputDir.absolutePath}")

        for ((fileName, contents) in compiled)
            File(outputDir, fileName).writeText(contents.joinToString("\n"))
    }
}
