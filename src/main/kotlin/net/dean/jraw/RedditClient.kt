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
    fun me(): JsonNode =
        http.executeSync(HttpRequest.Builder()
            .url("https://oauth.reddit.com/api/v1/me")
            .addHeader("Authorization", "bearer ${oauthData.accessToken}")
            .build()).json
}
