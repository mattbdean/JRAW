package net.dean.jraw.meta

import org.reflections.Configuration
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.Utils.getMethodsFromDescriptors
import java.lang.reflect.Method

/**
 * Same as [Reflections] but overrides the method [getMethodsAnnotatedWith] in order to skip Kotlin compiler created
 * methods for functions with default parameters. Such functions would not be handled correctly and result in exception
 * thrown inside [org.reflections.util.Utils.getMemberFromDescriptor].
 *
 * As the filtered out methods don't actually exist in the source code, the tasks execution is unaffected.
 */
class KotlinReflections(configuration: Configuration) : Reflections(configuration) {

    /**
     * Overridden implementation that skips Kotlin default methods overloads (created by Kotlin compiler).
     */
    override fun getMethodsAnnotatedWith(annotation: Class<out Annotation>): Set<Method> {
        val methods = store.get(MethodAnnotationsScanner::class.java.simpleName, annotation.name)
        val methodsWithoutKotlinDefaultOverloads = methods.filter {
            val className = it.substringBefore('(').substringBeforeLast('.')
            val methodName = it.substringBefore('(').substringAfterLast('.')
            val firstParam = it.substringAfter('(').substringBefore(',').trim()
            !(className == firstParam && methodName.endsWith("\$default"))
        }
        return getMethodsFromDescriptors(methodsWithoutKotlinDefaultOverloads, *(configuration.classLoaders ?: emptyArray()))
    }
}
