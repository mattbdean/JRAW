package net.dean.jraw.meta

import java.io.File

object ReadmeBadgeUpdater {
    fun update(overview: EndpointOverview, readme: File): Boolean {
        var replaced = false

        readme.writeText(readme.readLines().joinToString("\n") {
            if (it.startsWith("[![API coverage]")) {
                replaced = true
                url(overview.completionPercentage(decimals = 0))
            } else {
                it
            }
        } + '\n') // add a newline at the end of the file

        return replaced
    }

    private fun url(percent: String) =
        "[![API coverage](https://img.shields.io/badge/API_coverage-$percent%25-9C27B0.svg)]" +
            "(https://github.com/thatJavaNerd/JRAW/blob/master/ENDPOINTS.md)"
}
