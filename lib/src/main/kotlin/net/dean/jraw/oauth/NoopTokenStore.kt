package net.dean.jraw.oauth

class NoopTokenStore : TokenStore {
    override fun storeCurrent(username: String, data: OAuthData) {}
    override fun storeRefreshToken(username: String, token: String) {}
    override fun fetchCurrent(username: String) = null
    override fun fetchRefreshToken(username: String) = null
}
