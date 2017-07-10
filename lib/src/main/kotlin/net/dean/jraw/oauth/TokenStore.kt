package net.dean.jraw.oauth

interface TokenStore {
    fun storeCurrent(username: String, data: OAuthData)
    fun storeRefreshToken(username: String, token: String)

    fun fetchCurrent(username: String): OAuthData?
    fun fetchRefreshToken(username: String): String?
}
