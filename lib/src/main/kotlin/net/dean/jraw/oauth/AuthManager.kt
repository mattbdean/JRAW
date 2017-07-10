package net.dean.jraw.oauth

import net.dean.jraw.http.HttpAdapter
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.NetworkException
import java.util.*

/**
 * This class is responsible for maintaining and refreshing access tokens for the reddit API.
 *
 * Only installed and web apps may request refresh tokens. To do this, set `permanent = true` when calling
 * [StatefulAuthHelper.getAuthorizationUrl]. Script apps and apps using application-only (userless) authentication do
 * not receive a refresh token since refreshing may be done non-interactively.
 *
 * Script apps and those using application-only authentication do not need a refresh token.
 */
class AuthManager(
    private val http: HttpAdapter,
    private val credentials: Credentials
) {
    /**
     * Current OAuthData. Set to `internal` visibility instead of `private` for testing purposes. Setting this property
     * also sets [tokenExpiration]. If [refreshToken] is null and the new OAuthData's refresh token isn't,
     * [refreshToken] gets updated as well.
     */
    private var _current: OAuthData? = null

    internal var currentUsername: String? = null

    var tokenStore: TokenStore = NoopTokenStore()
    var tokenPersistenceStrategy = TokenPersistenceStrategy.ALL

    /**
     * The token provided by reddit to be used to request more access tokens. Only applies to installed and web apps.
     */
    internal var _refreshToken: String? = null

    val refreshToken: String? get() = _refreshToken

    /**
     * The token used to access the reddit API.
     *
     * @throws IllegalStateException If there is no current OAuthData
     */
    val accessToken: String
        get() = (current ?: throw IllegalStateException("No current OAuthData")).accessToken

    /** Alias to [credentials].authMethod */
    internal val authMethod = credentials.authMethod

    /** The most up-to-date auth data as understood by this manager. */
    val current: OAuthData? get() = _current

    /**
     * Tries to obtain more up-to-date authentication data.
     *
     * If using a script app or application-only authentication, renewal can be done automatically (by simply requesting
     * a new token). For web and installed apps, a non-null [_refreshToken] is required. See the [StatefulAuthHelper]
     * class documentation for more.
     */
    fun renew() {
        val newData: OAuthData = if (authMethod == AuthMethod.SCRIPT) {
            OAuthHelper.scriptOAuthData(http, credentials)
        } else if (authMethod.isUserless) {
            OAuthHelper.applicationOnlyOAuthData(http, credentials)
        } else if (_refreshToken != null) {
            sendRenewalRequest(_refreshToken!!)
        } else {
            throw IllegalStateException("Cannot refresh current OAuthData (no refresh token or not a script app)")
        }

        update(newData)
    }

    /** Returns true if there is no current OAuthData or it has already expired */
    fun needsRenewing(): Boolean {
        return (_current?.expiration ?: return true).before(Date())
    }

    /**
     * Returns true if using a script app or userless authentication. Otherwise, returns true if [refreshToken] is
     * non-null.
     */
    fun canRenew(): Boolean {
        // Script apps and userless apps can simply request a new token since the user doesn't have to authorize it.
        // Otherwise, we need a refresh token.
        return if (credentials.authMethod == AuthMethod.SCRIPT ||
            credentials.authMethod.isUserless) {
            true
        } else {
            _refreshToken != null
        }
    }

    internal fun update(newData: OAuthData?) {
        if (newData != null) {
            if (_refreshToken == null && newData.refreshToken != null) {
                _refreshToken = newData.refreshToken
                if (tokenPersistenceStrategy == TokenPersistenceStrategy.REFRESH_ONLY ||
                    tokenPersistenceStrategy == TokenPersistenceStrategy.ALL) {
                    tokenStore.storeRefreshToken(currentUsername(), _refreshToken!!)
                }
            }

            if (tokenPersistenceStrategy == TokenPersistenceStrategy.ALL) {
                tokenStore.storeCurrent(currentUsername(), newData)
            }

            this._current = newData
        }
    }

    private fun sendRenewalRequest(refreshToken: String): OAuthData {
        val res = http.execute(HttpRequest.Builder()
            .url("https://www.reddit.com/api/v1/access_token")
            .post(mapOf(
                "grant_type" to "refresh_token",
                "refresh_token" to refreshToken
            ))
            .basicAuth(credentials.clientId to credentials.clientSecret)
            .build())

        if (!res.successful) {
            val e = NetworkException(res)
            if (res.code == 401)
                throw OAuthException("Incorrect client ID and/or client secret", e)
            throw e
        }

        return res.deserialize()
    }

    fun currentUsername(): String =
        if (credentials.authMethod.isUserless) {
            USERNAME_USERLESS
        } else {
            currentUsername ?: USERNAME_UNKOWN
        }

    companion object {
        const val USERNAME_USERLESS = "<userless>"
        const val USERNAME_UNKOWN = "<unknown>"
    }
}
