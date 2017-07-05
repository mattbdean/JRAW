package net.dean.jraw.oauth

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import net.dean.jraw.databind.OAuthDataDeserializer

@JsonDeserialize(using = OAuthDataDeserializer::class)
data class OAuthData(
    val accessToken: String,

    val tokenType: String,

    /** The time in milliseconds this OAuth token will be valid for */
    val shelfLife: Int,

    /** A list in scopes the access token has permission for */
    val scopes: List<String>,

    val refreshToken: String?
)
