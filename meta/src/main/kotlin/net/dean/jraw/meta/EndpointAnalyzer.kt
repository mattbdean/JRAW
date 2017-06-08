package net.dean.jraw.meta

import javassist.ClassPool
import net.dean.jraw.EndpointImplementation
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Method

/**
 * Singleton that creates instances of [EndpointMeta] using reflection and bytecode manipulation libraries Reflections
 * and javassist.
 */
object EndpointAnalyzer {
    /** A lazily-initialized set of methods that implement [EndpointImplementation] */
    private val implementations: Set<Method> by lazy {
        val reflections = Reflections(ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage("net.dean.jraw"))
            .setScanners(MethodAnnotationsScanner()))

        reflections.getMethodsAnnotatedWith(EndpointImplementation::class.java)
    }

    /** Default javassist class pool */
    private val classPool = ClassPool.getDefault()

    /**
     * Gets an EndpointMeta object for the given [ParsedEndpoint]
     */
    fun getFor(e: ParsedEndpoint): EndpointMeta? {
        val method = implementations.firstOrNull {
            val other = it.getAnnotation(EndpointImplementation::class.java).endpoint
            other.method == e.method && other.path == e.path
        } ?: return null


        return EndpointMeta(
            implementation = method,
            sourceUrl = "https://github.com/thatJavaNerd/JRAW/tree/kotlin/lib/src/main/kotlin/" +
                method.declaringClass.name.replace(".", "/") + ".kt#L" + lineNumber(method)
        )
    }

    private fun lineNumber(m: Method) =
        classPool.getMethod(m.declaringClass.name, m.name).methodInfo.getLineNumber(0)
}
