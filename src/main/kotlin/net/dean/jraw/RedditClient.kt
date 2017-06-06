package net.dean.jraw

import com.fasterxml.jackson.databind.JsonNode
import net.dean.jraw.http.HttpAdapter
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.oauth.AuthenticationMethod
import net.dean.jraw.http.oauth.OAuthData

class RedditClient(
    val http: HttpAdapter,
    val authMethod: AuthenticationMethod,
    val oauthData: OAuthData
) {
    /**
     * Creates a [HttpRequest.Builder], setting `secure(true)`, `host("oauth.reddit.com")`, and the Authorization header
     */
    fun requestStub() = HttpRequest.Builder()
        .secure(true)
        .host("oauth.reddit.com")
        .addHeader("Authorization", "bearer ${oauthData.accessToken}")

    fun me(): JsonNode =
        http.executeSync(requestStub()
            .path("/api/v1/me")
            .build()).json
}
