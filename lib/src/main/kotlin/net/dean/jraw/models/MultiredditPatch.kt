package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class MultiredditPatch private constructor(b: Builder) {
    @JsonProperty("description_md")
    val description: String? = b.description
    val displayName: String? = b.displayName
    val iconName: String? = b.iconName
    val keyColor: String? = b.keyColor
    val subreddits: List<SubredditElement> = b.subreddits.map { SubredditElement(it) }
    val visibility: String? = b.visibility
    val weightingScheme: String? = b.weightingScheme

    class Builder {
        internal var description: String? = null
        internal var displayName: String? = null
        internal var iconName: String? = null
        internal var keyColor: String? = null
        internal var subreddits: List<String> = emptyList()
        internal var visibility: String? = null
        internal var weightingScheme: String? = null

        /** Markdown-formatted text */
        fun description(description: String): Builder { this.description = description; return this }

        /** The name this multireddit will go by */
        fun displayName(displayName: String): Builder { this.displayName = displayName; return this }

        /**
         * According to the API, one of :
         *  - `art and design`
         *  - `ask`
         *  - `books`
         *  - `business`
         *  - `cars`
         *  - `comics`
         *  - `cute animals`
         *  - `diy`
         *  - `entertainment`
         *  - `food and drink`
         *  - `funny`
         *  - `games`
         *  - `grooming`
         *  - `health`
         *  - `life advice`
         *  - `military`
         *  - `models pinup`
         *  - `music`
         *  - `news`
         *  - `philosophy`
         *  - `pictures and gifs`
         *  - `science`
         *  - `shopping`
         *  - `sports`
         *  - `style`
         *  - `tech`
         *  - `travel`
         *  - `unusual stories`
         *  - `video`
         *  - `` *(empty string)*
         *  - `None`
         */
        fun iconName(iconName: String): Builder { this.iconName = iconName; return this }

        /**
         * A hex-formatted hex string, like `#CEE3F8`. This color is primarily used when viewing the multireddit on the
         * mobile site.
         */
        fun keyColor(keyColor: String): Builder { this.keyColor = keyColor; return this }

        /** A list of subreddits to include in this multireddit. Do not include the `/r/` prefix. */
        fun subreddits(subreddits: List<String>): Builder { this.subreddits = subreddits; return this }

        /** One of `public`, `private`, or `hidden` */
        fun visibility(visibility: String): Builder { this.visibility = visibility; return this }

        /** Either `classic` or `fresh` */
        fun weightingScheme(weightingScheme: String): Builder { this.weightingScheme = weightingScheme; return this }

        fun build() = MultiredditPatch(this)
    }

    class SubredditElement(val name: String)
}
