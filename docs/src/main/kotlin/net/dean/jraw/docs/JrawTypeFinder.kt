package net.dean.jraw.docs

import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder

object JrawTypeFinder {
    val jrawTypes by lazy { findJrawClasses() }

    private val reflections = Reflections(ConfigurationBuilder()
        .setScanners(SubTypesScanner(false))
        .setUrls(ClasspathHelper.forPackage("net.dean.jraw"))
    )

    private val ignoredPackages = listOf(
        "net.dean.jraw.docs",
        "net.dean.jraw.docs.samples"
    )

    private fun findJrawClasses(): List<Class<*>> {
        val list = reflections.getSubTypesOf(Object::class.java)
        return list.filter { it.`package`.name !in ignoredPackages }.sortedBy { it.name }
    }
}
