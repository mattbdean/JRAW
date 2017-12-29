package net.dean.jraw.meta

import net.steppschuh.markdowngenerator.link.Link
import net.steppschuh.markdowngenerator.table.Table
import net.steppschuh.markdowngenerator.text.code.Code
import net.steppschuh.markdowngenerator.text.heading.Heading
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


object MarkdownOverviewCreator {
    fun create(endpoints: List<ParsedEndpoint>, f: File) = f.writeText(create(endpoints))

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z")
    private fun create(allEndpoints: List<ParsedEndpoint>): String {
        return with (StringBuilder()) {
            // Comment to those curious enough to open the file
            block("<!--- Generated ${dateFormat.format(Date())}. Use `./gradlew :meta:update` to update. DO NOT " +
                "MODIFY DIRECTLY -->")

            // Main header
            heading("Endpoints")

            // Purpose of this file
            block(
                "This file contains a list of all the endpoints (regardless of if they have been implemented) that " +
                    "can be found at the [official reddit API docs](https://www.reddit.com/dev/api/oauth). " +
                    "To update this file, run `./gradlew :meta:update`"
            )

            // Summary
            block("So far, **${countImplemented(allEndpoints)}** endpoints (out of ${allEndpoints.size}) have been implemented.")

            val grouped = allEndpoints.groupBy { it.oauthScope }
            for ((scope, endpoints) in grouped) {
                heading(if (scope == "any") "(any scope)" else scope, 2)

                val table = Table.Builder()
                    .withAlignments(Table.ALIGN_CENTER, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                    .addRow("Method", "Endpoint", "Implementation")

                val sorted = endpoints.sortedWith(compareBy(
                    // Show sorted endpoints first
                    { EndpointAnalyzer.getFor(it) == null },
                    // Then sort ascending alphabetically by path
                    { it.path },
                    // Then sort ascending alphabetically by HTTP method
                    { it.method }
                ))

                for (e in sorted) {
                    table.addRow(
                        Code(e.method),
                        Link(Code(markdownPath(e)), e.redditDocLink),
                        implString(e))
                }

                block(table.build())
            }

            toString()
        }
    }

    internal fun markdownPath(e: ParsedEndpoint) = (if (e.subredditPrefix) "[/r/{subreddit}]" else "") + e.path

    fun StringBuilder.heading(text: String, level: Int = 1) = block(Heading(text, level))

    fun StringBuilder.block(obj: Any) {
        append(obj)
        append("\n\n")
    }

    private fun implString(e: ParsedEndpoint): String {
        val meta = EndpointAnalyzer.getFor(e) ?: return "None"
        val method = meta.implementation
        return Link(Code("${method.declaringClass.simpleName}.${method.name}()"), meta.sourceUrl).toString()
    }

    private fun countImplemented(list: List<ParsedEndpoint>) =
        list.map { EndpointAnalyzer.getFor(it) }.count { it != null }
}
