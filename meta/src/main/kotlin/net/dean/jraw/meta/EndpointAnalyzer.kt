package net.dean.jraw.meta

import javassist.ClassPool
import net.dean.jraw.EndpointImplementation
import org.jsoup.Jsoup
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Method
import java.net.URL

/**
 * Singleton that parses https://reddit.com/dev/api/oauth and creates instances of [EndpointMeta] using reflection and
 * bytecode manipulation libraries Reflections and javassist.
 */
class EndpointAnalyzer(private val notPlanned: List<Pair<String, String>> = listOf()) {

    /** A lazily-initialized set of methods that implement [EndpointImplementation] */
    private val implementations: Set<Method> by lazy {
        val reflections = KotlinReflections(ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage("net.dean.jraw"))
            .setScanners(MethodAnnotationsScanner()))

        reflections.getMethodsAnnotatedWith(EndpointImplementation::class.java)
    }

    /** Default javassist class pool */
    private val classPool = ClassPool.getDefault()

    fun fetch(): EndpointOverview {
        val doc = Jsoup.connect(BASE_URL).get()

        val containers = doc.select("div.endpoint")

        val endpoints = containers.map {
            // Remove &nbsp; characters
            val method = it.select(".method").first().text().replace("\u00a0", "").trim()
            val oauthScope = it.select(".oauth-scope").first().text()
            val redditDocLink = BASE_URL + it.select(".links a").first { it.attr("href").startsWith("#") }.attr("href")

            // This will be something like "POST /api/commentany" where method is "POST", path is "/api/comment" and
            // oauthScope is "any"
            var path = it.select("h3").text()

            // Remove method
            path = path.substring(method.length + 1)

            // Remove paths that start with "[/r/subreddit]"
            val result = removeSubredditPrefix(path)
            path = result.first
            val subredditPrefix = result.second

            path = fixPathParams(trimPath(path, oauthScope), redditDocLink)

            val displayPath = if (subredditPrefix) "[/r/{subreddit}]$path" else path

            val implDetails = implDetails(method, displayPath)

            EndpointMeta(
                method = method,
                path = path,
                displayPath = displayPath,
                oauthScope = oauthScope,
                redditDocLink = redditDocLink,
                subredditPrefix = subredditPrefix,
                implementation = implDetails.method,
                sourceUrl = implDetails.sourceUrl,
                status = implDetails.status
            )
        }

        return EndpointOverview(endpoints)
    }

    /**
     * Gets an EndpointMeta object for the endpoint described by the given
     */
    fun implDetails(endpointMethod: String, endpointDisplayPath: String): EndpointImplDetails {
        val javaMethod = implementations.firstOrNull {
            val other = it.getAnnotation(EndpointImplementation::class.java).endpoints
            other.find { it.method == endpointMethod && it.path == endpointDisplayPath } != null
        }

        val isNotPlanned by lazy {
            notPlanned.firstOrNull { (method, path) ->
                method == endpointMethod && path == endpointDisplayPath
            } != null
        }

        val implStatus: ImplementationStatus = when {
            javaMethod != null -> ImplementationStatus.IMPLEMENTED
            isNotPlanned -> ImplementationStatus.NOT_PLANNED
            else -> ImplementationStatus.PLANNED
        }

        val sourceUrl = if (javaMethod != null) sourceUrl(javaMethod) else null

        return EndpointImplDetails(
            method = javaMethod,
            sourceUrl = sourceUrl,
            status = implStatus
        )
    }

    private fun removeSubredditPrefix(path: String): Pair<String, Boolean> {
        return if (path.startsWith("[/r/subreddit]")) {
            path.substring("[/r/subreddit]".length) to true
        } else {
            path to false
        }
    }

    private fun trimPath(path: String, oauthScope: String): String {
        var newPath = path

        // path might also include the string "rss support" if the endpoints supports RSS
        if (newPath.endsWith("rss support"))
            newPath = newPath.substring(0, newPath.length - "rss support".length)

        // the newPath will include the oauth scope at the very end
        if (newPath.endsWith(oauthScope))
            newPath = newPath.substring(0, newPath.length - oauthScope.length)

        return newPath
    }

    /**
     * Transforms colon parameters into brace parameters and inserts missing braces
     */
    private fun fixPathParams(path: String, redditDocLink: String): String {
        var newPath = path

        if (newPath.contains(":"))
            newPath = transformColonParameters(newPath)

        if (redditDocLink.contains('{'))
            newPath = injectPathParams(newPath, redditDocLink)

        return newPath
    }

    /**
     * Some API endpoint paths use colon parameters instead of brace parameters, like this:
     *
     * `/api/mod/conversations/:conversation_id`
     *
     * This function transforms those into brace path parameters, so
     *
     * `transformColonParameters("/api/mod/conversations/:conversation_id")`
     *
     * will output this:
     *
     * `/api/mod/conversations/{conversation_id}`
     */
    private fun transformColonParameters(path: String): String =
        path.split("/").map { if (it.startsWith(":")) "{${it.substring(1)}}" else it }.joinToString("/")

    /**
     * Inserts braces around path parameters. Parameter names are located through the [redditDocLink] fragment
     * (something like "GET_subreddits_mine_{where}") and braces are injected into the given path.
     */
    private fun injectPathParams(path: String, redditDocLink: String): String {
        var newPath = path

        val linkFragment = URL(redditDocLink).ref
        val params = pathParamRegex.findAll(linkFragment)
        params.forEach {
            // groups[0].value is the parameter with braces, (e.g. "{foo}"), groups[1].value is the parameter without
            // braces (e.g. "foo")
            newPath = newPath.replaceFirst(it.groups[1]!!.value, it.groups[0]!!.value)
        }

        return newPath
    }

    private fun lineNumber(m: Method) =
        classPool.getMethod(m.declaringClass.name, m.name).methodInfo.getLineNumber(0)

    private fun sourceUrl(m: Method) =
        "https://github.com/mattbdean/JRAW/tree/master/lib/src/main/kotlin/" +
            m.declaringClass.name.replace(".", "/") + ".kt#L" + lineNumber(m)

    data class EndpointImplDetails(
        val method: Method? = null,
        val sourceUrl: String? = null,
        val status: ImplementationStatus
    )

    companion object {
        private const val BASE_URL = "https://www.reddit.com/dev/api/oauth"

        /** Matches path parameters */
        private val pathParamRegex = Regex("\\{(.*?)}")
    }
}
