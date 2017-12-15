package net.dean.jraw.meta.test

import com.winterbe.expekt.should
import net.dean.jraw.meta.EnumCreator
import net.dean.jraw.meta.EnumCreator.Companion.SUBREDDIT_PREFIX_CONSTANT_NAME
import net.dean.jraw.meta.ParsedEndpoint
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.util.*

class EnumCreatorTest : Spek({
    val endpoints = listOf(
        ParsedEndpoint(
            "GET",
            "/api/v1/foo/{bar}",
            oauthScope = "fooscope",
            redditDocLink = "<reddit doc url>",
            subredditPrefix = false
        ),
        ParsedEndpoint(
            "POST",
            "/api/v1/foo/{bar}",
            oauthScope = "fooscope",
            redditDocLink = "<reddit doc url>",
            subredditPrefix = true
        )
    )

    it("should generate an enum with unique identifiers") {
        val out = StringBuilder()
        EnumCreator(endpoints).writeTo(out)

        val identifiers = out.toString().split("\n").filter {
            it.trim().matches(Regex("[A-Z_]{3}+.*?[,;]"))
        }.map { it.trim() }

        identifiers.should.have.size(endpoints.size)
        identifiers[0].should.equal("""GET_FOO_BAR("GET", "/api/v1/foo/{bar}", "fooscope"),""")
        identifiers[1].should.equal("""POST_FOO_BAR("POST", $SUBREDDIT_PREFIX_CONSTANT_NAME + "/api/v1/foo/{bar}", "fooscope");""")
    }

    it("should generate compilable code") {
        val tmpDir = createTempDir("jraw")

        try {
            EnumCreator(endpoints).writeTo(tmpDir)

            // Create a Process to compile the generated code
            val process = ProcessBuilder("javac", EnumCreator.RELATIVE_OUTPUT_FILE)
                .directory(tmpDir)
                .redirectErrorStream(true)
                .start()

            // Read the entire output as a String
            // https://stackoverflow.com/a/5445161/1275092
            val s = Scanner(process.inputStream).useDelimiter("\\A")
            val output = if (s.hasNext()) s.next() else ""

            // Wait for the process to exit
            val exitCode = process.waitFor()

            // Print the output of the compiler so we know what went wrong
            if (exitCode != 0) {
                System.err.println("Failed to compile:")
                output.split("\n").forEach(System.err::println)
            }

            // Make the assertion
            exitCode.should.equal(0)
        } finally {
            // Clean up
            tmpDir.deleteRecursively()
        }
    }
})
