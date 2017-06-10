package net.dean.jraw.references

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.RedditClient
import net.dean.jraw.models.Listing
import net.dean.jraw.models.Thing

class PaginatorReference<out T : Thing>(reddit: RedditClient, baseUrl: String) : AbstractReference<String>(reddit, baseUrl) {
    @EndpointImplementation(Endpoint.GET_HOT)
    fun hot(): Listing<T> {
        return reddit.request { it.path("$subject/hot") }.deserialize()
    }
}
