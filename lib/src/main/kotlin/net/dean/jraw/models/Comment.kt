package net.dean.jraw.models

data class Comment(
    val body: String
) : Thing(ThingType.COMMENT)
