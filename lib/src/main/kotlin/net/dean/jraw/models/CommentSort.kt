package net.dean.jraw.models

import com.squareup.moshi.Json

enum class CommentSort {
    @Json(name = "confidence") CONFIDENCE,
    @Json(name = "top") TOP,
    @Json(name = "new") NEW,
    @Json(name = "controversial") CONTROVERSIAL,
    @Json(name = "old") OLD,
    @Json(name = "random") RANDOM,
    @Json(name = "qa") QA,
    @Json(name = "live") LIVE
}
