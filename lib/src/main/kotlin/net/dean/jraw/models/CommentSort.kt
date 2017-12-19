package net.dean.jraw.models

import com.squareup.moshi.Json

/** A list of sorting methods that can be used when examining comments */
enum class CommentSort {
    /** What reddit thinks is best. Factors in score, age, and a few other variables. */
    @Json(name = "confidence") CONFIDENCE,

    /** Comments with the highest score are shown first. */
    @Json(name = "top") TOP,

    /** Newest comments are shown first */
    @Json(name = "new") NEW,

    /** The most controversial comments are shown first (usually this means the comments with the most downvotes) */
    @Json(name = "controversial") CONTROVERSIAL,

    /** Comments appear in the order they were created */
    @Json(name = "old") OLD,

    /** Self explanatory */
    @Json(name = "random") RANDOM,

    /**
     * A special sorting made for Q&A-style (questions and answers) posts. Also known as AMA's (ask me anything). Puts
     * comments where the submission author replied first, then sorts by [CONFIDENCE].
     */
    @Json(name = "qa") QA,

    /** As of the time of writing (15 Dec 2017), this sort is in beta. When disabled by reddit, functions like [NEW]. */
    @Json(name = "live") LIVE
}
