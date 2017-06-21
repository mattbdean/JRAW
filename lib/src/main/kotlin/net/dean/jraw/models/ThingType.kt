package net.dean.jraw.models

enum class ThingType(val prefix: String) {
    COMMENT("t1"),
    ACCOUNT("t2"),
    SUBMISSION("t3"),
    MESSAGE("t4"),
    SUBREDDIT("t5"),
    TROPHY("t6"),
    PROMO_CAMPAIGN("t8")
}
