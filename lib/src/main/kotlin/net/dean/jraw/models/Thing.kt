package net.dean.jraw.models

import com.fasterxml.jackson.databind.JsonNode

abstract class Thing(val type: ThingType) : RedditObject(type.prefix) {
    lateinit var data: JsonNode
}
