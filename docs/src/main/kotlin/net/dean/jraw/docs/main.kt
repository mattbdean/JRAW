package net.dean.jraw.docs

import java.io.File

private const val SAMPLES_DIR_ARG = "--samples-dir"
private const val OUT_DIR_ARG = "--output-dir"
private const val RESOURCES_DIR_ARG = "--resources-dir"

fun main(args: Array<String>) {
    val cliArgs = parseArgs(args)

    // Establish a base of operations
    val samplesDir = File(cliArgs[SAMPLES_DIR_ARG])
    val outDir = File(cliArgs[OUT_DIR_ARG])
    val resourcesDir = File(cliArgs[RESOURCES_DIR_ARG])

    if (!outDir.isDirectory && !outDir.mkdirs())
        failAndExit("Could not `mkdir -p` for ${outDir.absolutePath}")

    val contentDir = File(resourcesDir, "content")

    BookBuilder(samplesDir, contentDir).build(outDir)
}

private fun parseArgs(args: Array<String>): Map<String, String> {
    if (args.size % 2 != 0)
        failAndExit("Expected an even number of arguments")

    val required = listOf(SAMPLES_DIR_ARG, OUT_DIR_ARG, RESOURCES_DIR_ARG)

    // Create a map where all even indexes (and 0) represent keys and all odd indexes represent values for the element
    // before it
    val allArgs = mapOf(*(args.indices step 2).map { args[it] to args[it + 1] }.toTypedArray())
    val filtered = mutableMapOf<String, String>()

    // Only return values we care about
    for (arg in required) {
        if (arg !in allArgs)
            failAndExit("Expected argument '$arg' to have a value")
        filtered.put(arg, allArgs[arg]!!)
    }

    return filtered
}

private fun failAndExit(msg: String, code: Int = 1): Nothing {
    System.err.println(msg)
    System.exit(code)

    // JVM will have exited by now, this is just for Kotlin
    throw Error()
}

/**
 * Simple function to recursively fina all files starting from a given access point
 */
private fun walkRecursive(base: File): List<File> {
    val files = mutableListOf<File>()
    if (base.isDirectory) {
        base.listFiles()?.forEach {
            files.addAll(walkRecursive(it))
        } ?: throw IllegalStateException("Encounted I/O exception when walking directory $base")
    } else if (base.isFile) {
        files.add(base)
    }

    return files
}
