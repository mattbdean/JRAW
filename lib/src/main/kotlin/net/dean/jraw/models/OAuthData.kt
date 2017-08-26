package net.dean.jraw.models

import java.util.*

data class OAuthData(
    /** A token that can be sent with an Authorization header to access oauth.reddit.com */
    val accessToken: String,

    /** A list in scopes the access token has permission for */
    val scopes: List<String>,

    /** A token that can be used to request a new access token after the current one has expired, if one was requested */
    val refreshToken: String?,

    /** The date at which the access token will expire */
    val expiration: Date
)
