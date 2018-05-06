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
    val reflections = Reflections(ConfigurationBuilder()
        .setUrls(ClasspathHelper.forPackage("net.dean.jraw"))
        .setScanners(SubTypesScanner(false)))

    fun assertAllImplement(superclass: Class<*>, pkg: String = "net.dean.jraw.models", manuallyExcluded: List<String> = listOf()) {
        val testedExclusions = ArrayList(manuallyExcluded)

        // It might be a better practice to combine all these filters into one using conjunctions, but this has
        // better readability
        val models = reflections.getSubTypesOf(Any::class.java)
            // Only looking for non-internal models
            .filter { it.`package`.name == pkg }
            // We don't care about interfaces
            .filter { !it.isInterface }
            // AutoValue classes are taken care of as long as their parent classes are as well
            .filter { !it.simpleName.matches(Regex("^\\\$*AutoValue_.+")) }
            // We don't care about builders or special Kotlin-generated stuff
            .filter { !it.name.endsWith("\$Builder") && !it.name.endsWith("\$DefaultImpls") }
            // Find only the classes that don't implement Serializable
            .filter { !it.kotlin.isSubclassOf(superclass.kotlin) }
            // Ignore the classes that we've manually OK'd
            .filter { !testedExclusions.remove(it.simpleName) }

        if (models.isNotEmpty()) {
            val limit = 10

            // Show up to the first 10 classes
            val csv = models.take(limit).joinToString(", ") { it.name }

            // Also show how many more left
            val more = if (limit >= models.size) "" else " (${models.size - limit} more)"

            throw AssertionError("Expected ${models.size} classes to implement ${superclass.name}: $csv $more")
        }

        if (testedExclusions.isNotEmpty()) {
            throw AssertionError("Class(es) was specified as manually excluded but never used: ${testedExclusions.joinToString(", ")}")
        }
    }

    describe("net.dean.jraw.models") {
        it("should all be serializable") {
            assertAllImplement(Serializable::class.java, manuallyExcluded = listOf("KindConstants"))
        }
    }
})
