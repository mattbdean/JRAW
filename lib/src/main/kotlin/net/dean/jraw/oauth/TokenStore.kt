package net.dean.jraw.oauth

import net.dean.jraw.models.OAuthData

/**
 * A TokenStore does exactly what you think it does: it stores tokens. More specifically, it stores refresh tokens and
 * OAuthData instances (which contains access tokens). A fully fleshed out TokenStore stores, fetches, and deletes
 * OAuthData instances and refresh tokens from an arbitrary data source. For an example, see [JsonFileTokenStore].
 */
interface TokenStore {
    fun storeCurrent(username: String, data: OAuthData)
    fun storeRefreshToken(username: String, token: String)

    fun fetchCurrent(username: String): OAuthData?
    fun fetchRefreshToken(username: String): String?

    fun deleteCurrent(username: String)
    fun deleteRefreshToken(username: String)
}
