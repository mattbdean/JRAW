package net.dean.jraw.meta

import net.steppschuh.markdowngenerator.link.Link
import net.steppschuh.markdowngenerator.table.Table
import net.steppschuh.markdowngenerator.text.code.Code
import net.steppschuh.markdowngenerator.text.heading.Heading
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object MarkdownOverviewCreator {
    fun create(endpoints: EndpointOverview, f: File) = f.writeText(create(endpoints))

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z")

    /** In which order to display endpoints. Statuses that come first will appear first. */
    private val implementationOrder = listOf(
        ImplementationStatus.IMPLEMENTED,
        ImplementationStatus.PLANNED,
        ImplementationStatus.NOT_PLANNED
    )

    private fun create(overview: EndpointOverview): String {
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
            block("So far, API completion is at **${overview.completionPercentage(decimals = 2)}%**. " +
                "${overview.implemented} out of ${overview.effectiveTotal} endpoints (ignoring ${overview.notPlanned} " +
                "endpoints not planned) have been implemented ")

            val oauthScopes = overview.scopes()
            for (scope in oauthScopes) {
                val section = overview.byOAuthScope(scope)

                heading(if (scope == "any") "(any scope)" else scope, 2)

                block("${section.completionPercentage(2)}% completion (${section.implemented} implemented, ${section.planned} planned, and " +
                    "${section.notPlanned} not planned)")

                val table = Table.Builder()
                    .withAlignments(Table.ALIGN_CENTER, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
                    .addRow("Method", "Endpoint", "Implementation")

                val sorted = section.endpoints.sortedWith(compareBy(
                    // Show sorted endpoints first, then planned, then not planned
                    { implementationOrder.indexOf(it.status) },
                    // Then sort ascending alphabetically by path
                    { it.path },
                    // Then sort ascending alphabetically by HTTP method
                    { it.method }
                ))

                for (e in sorted) {
                    table.addRow(
                        Code(e.method),
                        Link(Code(e.displayPath), e.redditDocLink),
                        implString(e))
                }

                block(table.build())
            }

            toString()
        }
    }

    private fun StringBuilder.heading(text: String, level: Int = 1) = block(Heading(text, level))

    private fun StringBuilder.block(obj: Any) {
        append(obj)
        append("\n\n")
    }

    private fun implString(e: EndpointMeta): String {
        return when (e.status) {
            ImplementationStatus.IMPLEMENTED -> {
                val method = e.implementation!!
                Link(Code("${method.declaringClass.simpleName}.${method.name}()"), e.sourceUrl).toString()
            }
            ImplementationStatus.NOT_PLANNED -> "Not planned"
            ImplementationStatus.PLANNED -> "None"
        }
    }
}
