package net.dean.jraw.references

import com.fasterxml.jackson.module.kotlin.readValue
import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.databind.ListingDeserializer
import net.dean.jraw.models.Account

class UserReference internal constructor(reddit: RedditClient, username: String) :
    AbstractReference<String>(reddit, username) {

    val isSelf = username == NAME_SELF

    @EndpointImplementation(arrayOf(Endpoint.GET_ME, Endpoint.GET_USER_USERNAME_ABOUT))
    fun about(): Account {
        val body = reddit.request {
            it.path(if (isSelf) "/api/v1/me" else "/user/$subject/about")
        }.body

        // /api/v1/me doesn't encapsulate the data with a "kind" and "data" node, use our custom ObjectMapper instance
        // when calling that endpoint
        return (if (isSelf) jackson else JrawUtils.jackson).readValue(body)
    }

    companion object {
        const val NAME_SELF = "me"

        private val jackson = JrawUtils.defaultObjectMapper()
            .registerModule(ListingDeserializer.Module)
    }
}
