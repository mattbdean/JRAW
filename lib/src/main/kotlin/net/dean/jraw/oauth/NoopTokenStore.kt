package net.dean.jraw.oauth

import net.dean.jraw.models.OAuthData

class NoopTokenStore : TokenStore {
    override fun storeLatest(username: String, data: OAuthData) {}
    override fun storeRefreshToken(username: String, token: String) {}
    override fun fetchLatest(username: String) = null
    override fun fetchRefreshToken(username: String) = null
    override fun deleteLatest(username: String) {}
    override fun deleteRefreshToken(username: String) {}
}
