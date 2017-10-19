package net.dean.jraw.oauth

import net.dean.jraw.models.OAuthData

/**
 * A TokenStore does exactly what you think it does: it stores tokens. More specifically, it stores refresh tokens and
 * OAuthData instances (which contains access tokens). A fully fleshed out TokenStore stores, fetches, and deletes
 * OAuthData instances and refresh tokens from an arbitrary data source. For an example, see [JsonFileTokenStore].
 *
 * In this class, "latest" ([fetchLatest], [storeLatest], [deleteLatest] refers to the most recently acquired
 * [OAuthData] instance. This OAuthData does not necessarily have to be unexpired.
 */
interface TokenStore {
    /**
     * Stores the most recently acquired OAuthData instance. [OAuthData.getRefreshToken] should be ignored. Consumers
     * will call [storeRefreshToken] directly when necessary.
     */
    fun storeLatest(username: String, data: OAuthData)
    /** Stores a refresh token for a given user. */
    fun storeRefreshToken(username: String, token: String)

    /** Attempts to fetch the most recently stored OAuthData instance, or null if there is none. */
    fun fetchLatest(username: String): OAuthData?
    /** Attempts to fetch the most recently stored refresh token, or null if there is none. */
    fun fetchRefreshToken(username: String): String?

    /** Deletes the OAuthData tied to this user. Does nothing if there was no data to begin with. */
    fun deleteLatest(username: String)
    /** Deletes the refresh token tied to this user. Does nothing if there was no refresh token to begin with. */
    fun deleteRefreshToken(username: String)
}
