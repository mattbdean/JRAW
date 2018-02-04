package net.dean.jraw.test.unit

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.io.Serializable
import kotlin.reflect.full.isSubclassOf

/*
These tests don't really test any functionality, but rather test some properties about the code reflectively
 */

class IntrospectiveTest : Spek({
    describe("net.dean.jraw.models") {
        it("should all be serializable") {
            val reflections = Reflections(ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("net.dean.jraw"))
                .setScanners(SubTypesScanner(false)))

            val manuallyExcluded = listOf(
                "net.dean.jraw.models.KindConstants"
            )

            // It might be a better practice to combine all these filters into one using conjunctions, but this has
            // better readability
            val models = reflections.getSubTypesOf(Any::class.java)
                // Only looking for non-internal models
                .filter { it.`package`.name == "net.dean.jraw.models" }
                // We don't care about interfaces
                .filter { !it.isInterface }
                // AutoValue classes are taken care of as long as their parent classes are as well
                .filter { !it.simpleName.matches(Regex("^\\\$*AutoValue_.+"))}
                // We don't care about builders
                .filter { !it.name.endsWith("\$Builder")}
                // Find only the classes that don't implement Serializable
                .filter { !it.kotlin.isSubclassOf(Serializable::class) }
                // Ignore the classes that we've manually OK'd
                .filter { it.name !in manuallyExcluded }

            if (models.isNotEmpty()) {
                val limit = 10

                // Show up to the first 10 classes
                val csv = models.take(limit).joinToString(", ") { it.name }

                // Also show how many more left
                val more = if (limit >= models.size) "" else " (${models.size - limit} more)"

                throw AssertionError("Expected ${models.size} classes to implement Serializable: $csv $more")
            }
        }
    }
})
