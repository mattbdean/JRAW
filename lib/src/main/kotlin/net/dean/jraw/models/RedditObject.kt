package net.dean.jraw.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
abstract class RedditObject(val kind: String) {
    override fun toString(): String {
        return "RedditObject(kind='$kind')"
    }
}
