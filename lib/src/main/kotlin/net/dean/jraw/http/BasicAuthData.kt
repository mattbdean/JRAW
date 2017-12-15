package net.dean.jraw.http

/**
 * HTTP basic access authentication credentials. See [here](https://en.wikipedia.org/wiki/Basic_access_authentication)
 * for more.
 */
data class BasicAuthData(
    /** Plaintext username */
    val username: String,
    /** Plaintext password */
    val password: String
)
