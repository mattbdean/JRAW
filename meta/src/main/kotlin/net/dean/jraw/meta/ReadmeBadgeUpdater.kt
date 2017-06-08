package net.dean.jraw.meta

import java.io.File

object ReadmeBadgeUpdater {
    fun update(allEndpoints: List<ParsedEndpoint>, readme: File): Boolean {
        val implemented = allEndpoints.filter { EndpointAnalyzer.getFor(it) != null }
        val percent = Math.round(implemented.size.toFloat() / allEndpoints.size.toFloat())

        var replaced: Boolean = false

        readme.writeText(readme.readLines().map {
            if (it.startsWith("[![API coverage]")) {
                replaced = true
                url(percent)
            } else {
                it
            }
        }.joinToString("\n"))

        return replaced
    }

    private fun url(percent: Int) =
        "[![API coverage](https://img.shields.io/badge/API_coverage-$percent%-9C27B0.svg)]" +
            "(https://github.com/thatJavaNerd/JRAW/blob/kotlin/ENDPOINTS.md)"
}
