package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import net.dean.jraw.databind.UnixTimeDeserializer
import java.util.*

data class LiveUpdate(
    /** The markdown-formatted textual body of the update */
    val body: String,

    @JsonProperty("name")
    override val fullName: String,

    override val id: String,

    /**
     * A list of embedded media in the body of the text. For example, any links to Tweets, YouTube videos, etc. show up
     * here.
     */
    val embeds: List<Embed>,

    /** The name of the user who created the update */
    val author: String,

    @JsonProperty("created_utc")
    @JsonDeserialize(using = UnixTimeDeserializer::class)
    override val created: Date,

    /**
     * If the update has been stricken. A stricken update appears on the website with the strikethrough effect applied
     * to its body.
     */
    val stricken: Boolean
) : RedditObject(KindConstants.LIVE_UPDATE), Created, Identifiable {
    /** Embedded media inside of a live update. */
    data class Embed(
        val url: String,
        val width: Int,
        val height: Int
    )
}
