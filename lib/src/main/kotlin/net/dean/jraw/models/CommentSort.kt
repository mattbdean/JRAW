package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonProperty

enum class CommentSort {
    @JsonProperty("confidence") CONFIDENCE,
    @JsonProperty("top") TOP,
    @JsonProperty("new") NEW,
    @JsonProperty("controversial") CONTROVERSIAL,
    @JsonProperty("old") OLD,
    @JsonProperty("random") RANDOM,
    @JsonProperty("qa") QA,
    @JsonProperty("live") LIVE
}
