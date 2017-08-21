package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import net.dean.jraw.models.LiveWebSocketUpdate.Activity
import net.dean.jraw.models.LiveWebSocketUpdate.Settings

data class LiveWebSocketUpdate(
    /**
     * - `update` — a new update has been posted in the thread. the payload is a [LiveUpdate].
     * - `activity` — periodic update on the viewer count, the payload is an [Activity].
     * - `settings` — change in the thread's settings, the payload is a [Settings].
     * - `delete` — an update has been deleted (removed from the thread), the payload is the ID of the deleted update
     * - `strike` — an update has been stricken, the payload is the ID of the stricken update
     * - `embeds_ready` — a previously posted update has been parsed and embedded media has been found in it. The
     *    payload is an [EmbedsReady]
     * - `complete` — the thread has been marked as complete, no further updates will be sent. No payload.
     */
    val type: String,

    /** See [type] */
    val payload: Any
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Settings(
        val description: String,
        val title: String,
        val nsfw: Boolean,
        val resources: String
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class EmbedsReady(
        @JsonProperty("media_embeds")
        val embeds: List<LiveUpdate.Embed>,

        @JsonProperty("liveupdate_id")
        val updateId: String
    )

    data class Activity(
        @JsonProperty("count")
        val usersActive: Int,
        val fuzzed: Boolean
    )
}
