package net.dean.jraw.docs

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
}
