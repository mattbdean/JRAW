package net.dean.jraw.meta

import net.dean.jraw.EndpointImplementation
import org.jsoup.Jsoup
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Method
import java.net.URL

class EndpointParser {
    fun fetch(): List<Endpoint> {
        val doc = Jsoup.connect(BASE_URL).get()

        val containers = doc.select("div.endpoint")

        return containers.map {
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

            Endpoint(method, path, oauthScope, redditDocLink, subredditPrefix)
        }
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
            newPath = newPath.replace(it.groups[1]!!.value, it.groups[0]!!.value)
        }

        return newPath
    }

    companion object {
        private const val BASE_URL = "https://www.reddit.com/dev/api/oauth"

        /** Matches path parameters */
        private val pathParamRegex = Regex("\\{(.*?)}")
    }
}
