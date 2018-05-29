package net.dean.jraw.references

import net.dean.jraw.Endpoint
import net.dean.jraw.EndpointImplementation
import net.dean.jraw.RedditClient
import net.dean.jraw.models.internal.GenericJsonResponse

abstract class ReplyableReference(reddit: RedditClient) : AbstractReference(reddit) {
    /**
     * Replies to a submission, comment, or private message. Returns any data provided by the API.
     */
    @EndpointImplementation(Endpoint.POST_COMMENT)
    protected fun reply(parentFullName: String, text: String): List<*> {
        val res = reddit.request {
            it.endpoint(Endpoint.POST_COMMENT)
                .post(mapOf(
                    "api_type" to "json",
                    "text" to text,
                    "thing_id" to parentFullName
                ))
        }.deserialize<GenericJsonResponse>()

        return (res.json?.data?.get("things") as? List<*>) ?:
            throw IllegalArgumentException("Unexpected JSON structure")
    }
}
