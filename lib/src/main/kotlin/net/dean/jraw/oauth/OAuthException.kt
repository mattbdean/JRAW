package net.dean.jraw.oauth

/** Thrown when JRAW encounters an OAuth2-related problem */
class OAuthException(message: String, cause: Exception? = null) : RuntimeException(message, cause)
