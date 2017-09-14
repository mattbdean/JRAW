package net.dean.jraw.docs.integration

import com.winterbe.expekt.should
import net.dean.jraw.docs.BookBuilder
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.File
import kotlin.properties.Delegates

class BookBuilderTest : Spek({
    // Not proud of this code but it works
    var projectRoot: File = File(".").canonicalFile
    if (projectRoot.name == "docs")
        projectRoot = projectRoot.parentFile

    val contentDir = File(projectRoot, "docs/src/main/resources/content")
    val samplesDir = File(projectRoot, "docs/src/main/java/net/dean/jraw/docs/samples")

    var outDir: File by Delegates.notNull()

    beforeEachTest {
        outDir = createTempDir("jraw_test_")
    }

    it("should compile the docs in a GitBook-ready format") {
        // Build the book
        BookBuilder(samplesDir, contentDir).build(outDir)

        // Run gitbook to make sure there are no build errors
        val exitCode = ProcessBuilder("gitbook", "build")
            .directory(outDir)
            .inheritIO()
            .start()
            .waitFor()

        exitCode.should.equal(0)

        // If a Markdown file is referenced in the table of contents, it doesn't get turned into a .html file but
        // instead gets copied wholesale with the rest of the HTML files. Make sure we don't have any stragglers
        val builtDir = File(outDir, "_book")
        val leftoverFiles = builtDir.listFiles { f: File -> f.extension == "md" }.toList()
        leftoverFiles.should.be.empty
    }

    afterEachTest {
        outDir.deleteRecursively()
    }
})
