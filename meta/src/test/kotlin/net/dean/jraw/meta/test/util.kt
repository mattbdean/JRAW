package net.dean.jraw.meta.test

import com.winterbe.expekt.should
import java.io.File
import java.util.*

fun ensureCompilable(createFile: (tmpDir: File) -> File) {
    val tmpDir = createTempDir("jraw")

    try {
        val outFile = createFile(tmpDir)

        // Create a Process to compile the generated code
        val process = ProcessBuilder("javac", outFile.absolutePath)
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
