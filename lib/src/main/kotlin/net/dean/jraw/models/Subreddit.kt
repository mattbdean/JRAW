package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Subreddit(
    @JsonProperty("display_name") val displayName: String
) : Thing(ThingType.SUBREDDIT)

