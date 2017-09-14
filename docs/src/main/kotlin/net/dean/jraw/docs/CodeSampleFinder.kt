package net.dean.jraw.docs

import com.thoughtworks.qdox.JavaProjectBuilder
import java.io.File
import java.io.StringReader

/** Uses qdox to find all methods annotated with [CodeSample] */
object CodeSampleFinder {
    /** Finds code samples from a source root (e.g. docs/src/main/java) */
    fun findAll(sourceRoot: File): List<CodeSampleRef> {
        val builder = JavaProjectBuilder()
        builder.addSourceTree(sourceRoot)
        return findCodeSamples(builder)
    }

    /** Finds code samples from a String value. Mostly for testing. */
    fun findOne(source: String): List<CodeSampleRef> {
        val builder = JavaProjectBuilder()
        builder.addSource(StringReader(source))
        return findCodeSamples(builder)
    }

    private fun findCodeSamples(builder: JavaProjectBuilder): List<CodeSampleRef> {
        return builder.classes
            // Find all methods from all classes
            .flatMap { it.methods }
            // We only care about methods with the CodeSample annotation
            .filter { it.annotations.firstOrNull { a -> a.type.simpleName == CodeSample::class.simpleName } != null }
            // Map the JavaMethod object to a CodeSample
            .map {
                // Split the source code up into lines and remove any blank lines at the beginning and end
                var lines = it.sourceCode.split("\n").dropWhile { it.isBlank() }.dropLastWhile { it.isBlank() }
                if (lines.isNotEmpty()) {
                    val first = lines[0]
                    val indent = first.length - first.trimStart().length
                    lines = lines.map { it.drop(indent) }
                }
                CodeSampleRef(it.declaringClass.simpleName + "." + it.name, lines)
            }
    }
}
