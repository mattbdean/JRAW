package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import net.dean.jraw.databind.UnixTimeDeserializer
import java.util.*

data class LiveThread(
    @JsonProperty("created_utc")
    @JsonDeserialize(using = UnixTimeDeserializer::class)
    override val created: Date,

    val description: String,

    @JsonProperty("name")
    override val fullName: String,

    override val id: String,

    /** True if the content in this thread is NSFW (not safe for work) */
    val nsfw: Boolean,

    /** One of 'live' or 'complete' */
    val state: String,

    val title: String,

    /** The amount of people viewing the thread, or null if it's already completed */
    val viewerCount: Int?,

    /** If the viewer count is randomly skewed, or null if it's already completed */
    val viewerCountFuzzed: Boolean?,

    /** The `ws://` URL for new live updates, or null if it's already completed */
    val websocketUrl: String?,

    /** Any additional resources provided by the moderators of the thread */
    val resources: String
) : RedditObject(KindConstants.LIVE_THREAD), Created, Identifiable
