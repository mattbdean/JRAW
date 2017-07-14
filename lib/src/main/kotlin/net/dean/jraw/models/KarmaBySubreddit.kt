package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonProperty

data class KarmaBySubreddit(
    @JsonProperty("sr")
    val subreddit: String,
    val commentKarma: Int,
    val linkKarma: Int
)
