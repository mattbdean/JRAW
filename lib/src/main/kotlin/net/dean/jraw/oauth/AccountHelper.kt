package net.dean.jraw.oauth

import net.dean.jraw.RedditClient
import net.dean.jraw.http.NetworkAdapter
import net.dean.jraw.models.OAuthData
import java.util.*

/**
 * This class aims to make the lives of Android and web developers a bit easier when handling switching between
 * accounts and in to/out of userless mode.
 *
 * Think of AccountHelper as a factory for RedditClients. You give the factory some basic information (a NetworkAdapter,
 * OAuth2 app credentials, etc.) and the factory will produce authenticated RedditClients for you.
 *
 * To use this class we have to make a few assumptions
 *
 * 1. We are using either an "installed" OAuth2 app or a "web" OAuth2 app
 * 2. The given TokenStore will **ONLY** store data for one OAuth2 app
 * 3. The given Credentials are **NOT** for application-only (userless) mode (in other words, make sure to use
 *    [Credentials.installedApp] or [Credentials.webapp] instead of their userless counterparts)
 *
 * From here it's all smooth sailing. You can make use of [switchToUser], [switchToNewUser], and [switchToUserless] with
 * minimal effort. If you ever lose track of the most current [RedditClient], the [reddit] property contains the most
 * recently authenticated RedditClient. Keep in mind that after you use a `switch*` method you won't be able to use the
 * previous client!
 *
 * To use this class to its fullest potential, make sure to provide a [TokenStore] implementation that cache and
 * retrieve OAuthData and refresh tokens for seamless transitions from one account to the next.
 */
class AccountHelper(
    /** Passed on to eventually create RedditClients */
    private val http: NetworkAdapter,

    /** Passed on to eventually create RedditClients */
    private val creds: Credentials,

    /** Passed on to eventually create RedditClients */
    private val tokenStore: TokenStore,

    /** An identifier unique to each device */
    deviceId: UUID
) {
    init {
        if (creds.authMethod.isUserless || creds.authMethod == AuthMethod.SCRIPT)
            throw IllegalArgumentException("AccountManager cannot be used with userless or script credentials")
    }

    private var _reddit: RedditClient? = null

    private var configure: (r: RedditClient) -> Unit = {}

    /** The most up-to-date RedditClient */
    val reddit: RedditClient
        get() = _reddit ?: throw IllegalStateException("No current authenticated client")


    /** A userless version of [creds] */
    private val userlessCreds: Credentials = if (creds.authMethod == AuthMethod.APP)
        Credentials.userlessApp(creds.clientId, deviceId)
    else
        Credentials.userless(creds.clientId, creds.clientSecret, deviceId)

    /**
     * Switches the current client to one that is authenticated without the context of a user. An HTTP request will be
     * required if there is no unexpired OAuthData for userless mode.
     */
    fun switchToUserless(): RedditClient {
        val seamless = trySwitchToUser(AuthManager.USERNAME_USERLESS, creds = userlessCreds)
        if (seamless != null)
            // If trySwitchToUser returns a non-null value it will have already switched the current client to the
            // returned value
            return seamless

        return switch(OAuthHelper.automatic(http, userlessCreds, tokenStore))
    }

    /**
     * Attempts to switch the current client to one authenticated for the current user. Requires either an unexpired
     * OAuthData or a refresh token in the TokenStore under the given username.
     */
    @Throws(IllegalStateException::class)
    fun switchToUser(username: String): RedditClient {
        return trySwitchToUser(username) ?:
            throw IllegalStateException("No unexpired OAuthData or refresh token available for user '$username'")
    }

    /**
     * Tries to create a new RedditClient based on already-stored refresh token or unexpired OAuthData. Returns null if
     * neither are available.
     */
    fun trySwitchToUser(username: String) = trySwitchToUser(username, creds)

    private fun trySwitchToUser(username: String, creds: Credentials = this.creds): RedditClient? {
        val current = tokenStore.fetchLatest(username)
        if (current != null && !current.isExpired)
            return switch(RedditClient(http, current, creds, tokenStore, username))

        val refresh = tokenStore.fetchRefreshToken(username)
        if (refresh != null) {
            // Pass mock data to the RedditClient so it'll refresh the access token on the first request
            val emptyData = OAuthData.create("", listOf(), refresh, Date(0L))
            return switch(RedditClient(http, emptyData, creds, tokenStore, username), forceRenew = true)
        }

        return null
    }

    /**
     * Returns a [StatefulAuthHelper] to guide the user through the interactive OAuth2 app authorization.
     */
    fun switchToNewUser(): StatefulAuthHelper =
        OAuthHelper.interactive(http, creds, tokenStore, onAuthenticated = { newClient -> switch(newClient) })

    /**
     * Returns true if this AccountHelper has a RedditClient instance valid authentication data.
     *
     * More specifically, this method returns true if
     *
     *  1. There is a RedditClient instance
     *  2. That instance's AuthManager does not need renewing or it can renew it
     */
    fun isAuthenticated(): Boolean {
        // If _reddit is null, that means we haven't ever received a RedditClient
        val authManager = _reddit?.authManager ?: return false

        // If the access token isn't expired, we're fine. If it is, we're fine only if we can renew the access token.
        return !_reddit!!.loggedOut && (!authManager.needsRenewing() || authManager.canRenew())
    }

    /**
     * If there is a managed RedditClient, sets its [loggedOut][RedditClient.loggedOut] property to true and stops
     * managing that client. Note that all changes done here are purely local, the client's access tokens or refresh
     * aren't touched.
     *
     * @see AuthManager.revokeAccessToken
     */
    fun logout() {
        val r = _reddit ?: return
        r.loggedOut = true
        _reddit = null
    }

    /**
     * Sets a function to be executed every time a new RedditClient is switched to. The provided function is called with
     * the new client. [switchToUser], [switchToUserless], [switchToNewUser], and [trySwitchToUser] all trigger this
     * function.
     *
     * Here's an example:
     *
     * ```kt
     * accountHelper.onSwitch { redditClient -> println(redditClient) }
     * accountHelper.switchToUserless()
     * ```
     *
     * Output:
     *
     * ```kt
     * RedditClient(username=<userless>)
     * ```
     */
    fun onSwitch(configure: (r: RedditClient) -> Unit) {
        this.configure = configure
    }

    /**
     * Does all the housekeeping required to clean up the old client, assigns the new client instance to [_reddit] and
     * returns it.
     */
    private fun switch(new: RedditClient, forceRenew: Boolean = false): RedditClient {
        _reddit?.loggedOut = true

        new.forceRenew = forceRenew
        configure(new)
        _reddit = new
        return new
    }

    /** */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountHelper

        if (http != other.http) return false
        if (creds != other.creds) return false
        if (tokenStore != other.tokenStore) return false
        if (_reddit != other._reddit) return false
        if (userlessCreds != other.userlessCreds) return false

        return true
    }

    /** */
    override fun hashCode(): Int {
        var result = http.hashCode()
        result = 31 * result + creds.hashCode()
        result = 31 * result + tokenStore.hashCode()
        result = 31 * result + (_reddit?.hashCode() ?: 0)
        result = 31 * result + userlessCreds.hashCode()
        return result
    }
}
