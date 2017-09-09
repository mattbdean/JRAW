package net.dean.jraw.models

import net.dean.jraw.filterValuesNotNull

data class LiveThreadPatch(
    val description: String? = null,
    val nsfw: Boolean? = null,
    val resources: String? = null,
    val title: String? = null
) {
    private constructor(b: Builder) : this(
        description = b.description,
        nsfw = b.nsfw,
        resources = b.resources,
        title = b.title
    )

    internal fun toRequestMap(): Map<String, String> = mapOf(
        "api_type" to "json",
        "description" to description,
        "nsfw" to nsfw?.toString(),
        "resources" to resources,
        "title" to title
    ).filterValuesNotNull()

    class Builder {
        internal var description: String? = null
        internal var nsfw: Boolean? = null
        internal var resources: String? = null
        internal var title: String? = null

        fun description(description: String?): Builder { this.description = description; return this }
        fun nsfw(nsfw: Boolean?): Builder { this.nsfw = nsfw; return this }
        fun resources(resources: String?): Builder { this.resources = resources; return this }
        fun title(title: String?): Builder { this.title = title; return this }

        fun build() = LiveThreadPatch(this)
    }
}
