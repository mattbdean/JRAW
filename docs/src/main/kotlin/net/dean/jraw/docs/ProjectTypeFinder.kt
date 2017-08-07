package net.dean.jraw.docs

import com.github.javaparser.JavaParser
import com.github.javaparser.Range
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder

/**
 * Singleton class dedicated to "project" types. A project type is any type that is included in the main project source
 * code (the `:lib` Gradle project).
 */
object ProjectTypeFinder {
    private val jrawTypes by lazy {
        val list = reflections.getSubTypesOf(Object::class.java)
        list.filter { it.`package`.name !in ignoredPackages }.sortedBy { it.name }
    }

    private val reflections = Reflections(ConfigurationBuilder()
        .setScanners(SubTypesScanner(false))
        .setUrls(ClasspathHelper.forPackage("net.dean.jraw"))
    )

    private val ignoredPackages = listOf(
        "net.dean.jraw.docs",
        "net.dean.jraw.docs.samples"
    )

    /**
     * Attempts to find any project classes included in the given CodeSampleRef. Note that the Map values are javaparser
     * Ranges, which uses 1-indexed values. In other words, the fifth character of the first line has a Position
     * equivalent to `Position(1, 5)`
     */
    fun find(ref: CodeSampleRef): Map<Range, Class<*>> {
        // Surround the code in braces so that we can parse it as a block of code. Make sure the closing brace is on a
        // new line so that a comment on the last line doesn't accidentally comment it out. The block of code we give to
        // JavaParser will look like this:
        //
        // {
        // statement one;
        // statement two;
        // ...
        // }
        val block = JavaParser.parseBlock("{\n" + ref.content.joinToString("\n") + "\n}")
        val visitor = Visitor()
        visitor.visit(block, null)

        return visitor.typesAndPositions.mapKeys {
            val range = it.key
            // The only thing on the first line is the opening brace, but the user of this method expects that the first
            // statement falls on line 1, adjust for that.
            Range.range(range.begin.line - 1, range.begin.column, range.end.line - 1, range.end.column)
        }
    }

    /**
     * Returns true if there is a project class that has the same simple name as the one provided. For example, if there
     * was a project class `net.dean.jraw.Foo`, `isProjectType("Foo")` would return true, but
     * `isProjectType("net.dean.jraw.Foo")` would return false.
     */
    fun isProjectType(simpleName: String) = fromSimpleName(simpleName) != null

    /**
     * Gets a Class that represents the given project simple name, or null if there is no project class with that
     * simple name.
     */
    fun fromSimpleName(simpleName: String): Class<*>? {
        return jrawTypes.firstOrNull { it.simpleName == simpleName }
    }

    private class Visitor : VoidVisitorAdapter<Void?>() {
        val typesAndPositions: MutableMap<Range, Class<*>> = HashMap()

        override fun visit(n: SimpleName, arg: Void?) {
            val jrawType = fromSimpleName(n.asString())
            if (jrawType != null) {
                typesAndPositions.put(n.range.orElseThrow { IllegalStateException("Expected range to be present") }, jrawType)
            }

            super.visit(n, arg)
        }
    }
}
