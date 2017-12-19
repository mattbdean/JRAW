package net.dean.jraw.oauth

/** Ways of persisting refresh and access tokens */
enum class TokenPersistenceStrategy {
    /** Both refresh and access tokens will be persisted */
    ALL,

    /** Only refresh tokens will be persisted */
    REFRESH_ONLY,

    /** No tokens will be persisted */
    NONE
}
