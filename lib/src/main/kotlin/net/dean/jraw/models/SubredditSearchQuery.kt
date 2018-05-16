package net.dean.jraw.models

data class SubredditSearchQuery @JvmOverloads constructor(
    val query: String,
    val exact: Boolean? = null,
    val includeNsfw: Boolean? = null,
    val includeUnadvertisable: Boolean? = null
) {
    class Builder(internal val query: String) {
        private var exact: Boolean? = null
        private var includeNsfw: Boolean? = null
        private var includeUnadvertisable: Boolean? = null

        /** Find only exact matches to the query */
        fun exact(exact: Boolean): Builder { this.exact = exact; return this }

        /** Include subreddits that primarily feature adult content */
        fun includeNsfw(includeNsfw: Boolean): Builder { this.includeNsfw = includeNsfw; return this }

        /** Include subreddits that have turned off ads or have been explicitly bladklisted by reddit for ad-showing */
        fun includeUnadvertisable(includeUnadvertisable: Boolean): Builder { this.includeUnadvertisable = includeUnadvertisable; return this }

        fun build() = SubredditSearchQuery(
            query,
            exact = exact,
            includeNsfw = includeNsfw,
            includeUnadvertisable = includeUnadvertisable
        )
    }
}
