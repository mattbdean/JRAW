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

    private fun compile(pages: List<Page>): Map<String, List<String>> {
        val actualPages: MutableList<Page> = ArrayList(pages)
        actualPages.add(Page("README", null))

        val data: HashMap<String, List<String>> = HashMap()
        data.put("SUMMARY.md", createSummaryPage(pages))

        actualPages
            .map { it.file + ".md" }
            .forEach { data.put(it, compiler.compile(File(contentDir, it))) }

        return data
    }

    private fun createSummaryPage(pages: List<Page>): List<String> {
        val sb = StringBuilder()
        sb.appendln("# Summary\n")

        for (page in pages) {
            sb.appendln("* [${page.title}](${page.file}.md)")
        }

        return sb.split("\n")
    }

    fun build(outputDir: File) {
        // Read the table of contents
        val tocFile = File(contentDir, "toc.json")
        if (!tocFile.isFile)
            throw IOException("Does not exist or not a file: ${tocFile.absolutePath}")

        val pageAdapter = moshi.adapter<List<Page>>(Types.newParameterizedType(List::class.java, Page::class.java))
            .failOnUnknown()
            .nullSafe()

        val pages = pageAdapter.fromJson(Okio.buffer(Okio.source(tocFile)))!!
        val compiled = compile(pages)

        if (!outputDir.isDirectory && !outputDir.mkdirs())
            throw IOException("Unable to create directories ${outputDir.absolutePath}")

        for ((fileName, contents) in compiled)
            File(outputDir, fileName).writeText(contents.joinToString("\n"))
    }
}
