package net.dean.jraw.oauth

import net.dean.jraw.models.OAuthData

interface TokenStore {
    fun storeCurrent(username: String, data: OAuthData)
    fun storeRefreshToken(username: String, token: String)

    fun fetchCurrent(username: String): OAuthData?
    fun fetchRefreshToken(username: String): String?

    fun deleteCurrent(username: String)
    fun deleteRefreshToken(username: String)
}
