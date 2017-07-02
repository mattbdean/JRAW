package net.dean.jraw.http

/**
 * HTTP basic access authentication credentials. See [here](https://en.wikipedia.org/wiki/Basic_access_authentication)
 * for more.
 */
data class BasicAuthData(
    val username: String,
    val password: String
)
