package net.dean.jraw.docs

import kotlin.reflect.KClass

fun <T : Exception> expectException(clazz: KClass<T>, doWork: () -> Unit) {
    val message = "Should have thrown ${clazz.java.name}"
    try {
        doWork()
        throw IllegalStateException(message)
    } catch (e: Exception) {
        // Make sure rethrow the Exception we created here
        if (e.message == message) throw e
        // Make sure we got the right kind of Exception
        if (e::class != clazz)
            throw IllegalStateException("Expecting function to throw ${clazz.java.name}, instead threw ${e::class.java.name}", e)
    }
}
