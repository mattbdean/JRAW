package net.dean.jraw.docs

import com.thoughtworks.qdox.JavaProjectBuilder
import java.io.File
import java.io.StringReader

object CodeSampleFinder {
    fun findAll(sourceRoot: File): List<CodeSampleRef> {
        val builder = JavaProjectBuilder()
        builder.addSourceTree(sourceRoot)
        return findCodeSamples(builder)
    }

    fun findOne(source: String): List<CodeSampleRef> {
        val builder = JavaProjectBuilder()
        builder.addSource(StringReader(source))
        return findCodeSamples(builder)
    }

    private fun findCodeSamples(builder: JavaProjectBuilder): List<CodeSampleRef> {
        return builder.classes.flatMap {
            it.methods
        }.filter {
            it.annotations.firstOrNull { a -> a.type.simpleName == CodeSample::class.simpleName } != null
        }.map {
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
