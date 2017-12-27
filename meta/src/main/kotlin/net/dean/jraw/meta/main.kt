package net.dean.jraw.meta
import java.io.File

/**
 * This entry point carries out tasks on files based on some [ParsedEndpoint] objects. Every argument expects a file, so if we
 * run this program with the command line arguments `--foo /path/to/directory`, it will execute a pre-defined task
 * called "foo" providing it with a list of [ParsedEndpoint]s parsed from [https://www.reddit.com/dev/api/oauth]
 *
 * @see EndpointParser
 */
fun main(args: Array<String>) {
    val opts = parseArgsArray(args)
    val tasks = filterArguments(opts)
    var failed = 0

    if (tasks.isNotEmpty()) {
        val endpoints = EndpointParser().fetch()

        tasks.forEach {
            // Print to stdout if successful, otherwise print to stderr
            try {
                val message = it(endpoints, opts[it.argKey]!!)
                println("${it.name}: $message")
            } catch (e: Exception) {
                System.err.println("${it.name}: ${e.message}")
                failed++
            }
        }
    } else {
        println("Nothing to do")
    }

    System.exit(failed)
}

val tasks: List<Task> = listOf(
    // enum task -- create an enum class to list Endpoints
    Task("enum") { endpoints, args ->
        val location = File(args[0])
        EnumCreator(endpoints).writeTo(location)
        "Created file ${File(location, EnumCreator.RELATIVE_OUTPUT_FILE).absolutePath}"
    },
    Task("md-overview") { endpoints, args ->
        val location = File(args[0])
        MarkdownOverviewCreator.create(endpoints, File(args[0]))
        "Created file ${location.absolutePath}"
    },
    Task("readme-badge") { endpoints, args ->
        val location = File(args[0])
        ReadmeBadgeUpdater.update(endpoints, location)
        "Updated API coverage badge in file $location"
    },
    Task("version") { _, args ->
        val output = VersionCreator.create(File(args[0]), args[1])
        "Created file ${output.absoluteFile}"
    }
)

/**
 * Represents some work to be done with a list of Endpoints and a file system path. Tasks can be invoked in three ways:
 *
 * ```
 * task.doWork(...)
 * task.invoke(...)
 * task(...)
 * ```
 */
data class Task(
    val name: String,

    /**
     * Does some work with the parsed Endpoints. The List<String> is any additional arguments supplied specifically to
     * the task
     */
    val doWork: (List<ParsedEndpoint>, List<String>) -> String)
{
    /** Including this key in the command line triggers this task to run. See [main] for more. */
    val argKey: String = "--$name"

    // Propagate parameters to `doWork`
    operator fun invoke(endpoints: List<ParsedEndpoint>, args: List<String>): String = doWork(endpoints, args)
}

/**
 * Filters out any requests for unknown tasks, issuing warnings if necessary. Returns a list of [Task]s that the user
 * specified
 */
fun filterArguments(opts: Map<String, List<String>>): List<Task> {
    val enabledTasks: MutableList<Task> = ArrayList()

    // Add all tasks if their argKey was included in the program arguments. For example, if we defined a task that takes
    // the argKey --foo, then
    tasks.filterTo(enabledTasks) {
        it.argKey in opts
    }

    // Find all keys that aren't an `argKey` in our `tasks` list
    val unknownTasks = opts.keys.filter { arg -> tasks.firstOrNull { task -> task.argKey == arg } == null }
    if (unknownTasks.isNotEmpty()) {
        System.err.println("Warning: Unknown tasks: ${unknownTasks.joinToString(", ") { "'$it'" }}")
        System.err.println("         Available tasks: ${tasks.joinToString(", ") { "'${it.argKey}'" }}")
    }

    return enabledTasks
}

/**
 * Parses the `args` array provided by a JVM program's main method into a Map usable by this program. If `args` does not
 * have an even size, the process is terminated via [fail]
 */
fun parseArgsArray(args: Array<String>): Map<String, List<String>> {
    if (args.isEmpty()) return emptyMap()

    if (args.size % 2 != 0) fail("Expecting one key for every value, got odd number of arguments")

    val map = HashMap<String, List<String>>()
    for (i in 0 until (args.size / 2)) {
        map.put(args[i * 2], args[(i * 2) + 1].split(";"))
    }

    return map
}

/** Prints the given message to stderr and exits with code 1 */
fun fail(reason: String) {
    System.err.println(reason)
    System.exit(1)
}
