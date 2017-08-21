package net.dean.jraw.oauth

import net.dean.jraw.RedditClient
import net.dean.jraw.http.NetworkAdapter
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.NetworkException
import java.util.*

/**
 * This class helps create authenticated RedditClient instances.
 *
 * There are two main types of authentication: automatic and interactive. Automatic authentication can be done without
 * asking the user, while interactive authentication requires the user to log in to their account and authorize your
 * reddit OAuth2 app to access their account.
 *
 * Script apps and those using application-only (userless) mode are the only apps that qualify for automatic
 * authentication.
 */
object OAuthHelper {
    @JvmStatic @JvmOverloads fun automatic(http: NetworkAdapter, creds: Credentials, tokenStore: TokenStore = NoopTokenStore()): RedditClient {
        return when {
            creds.authMethod.isUserless ->
                RedditClient(http, applicationOnlyOAuthData(http, creds), creds, tokenStore, overrideUsername = AuthManager.USERNAME_USERLESS)
            creds.authMethod == AuthMethod.SCRIPT ->
                RedditClient(http, scriptOAuthData(http, creds), creds, tokenStore, overrideUsername = creds.username)
            else -> throw IllegalArgumentException("AuthMethod ${creds.authMethod} is not eligible for automatic authentication")
        }
    }

    @JvmStatic @JvmOverloads fun interactive(http: NetworkAdapter, creds: Credentials, tokenStore: TokenStore = NoopTokenStore()): StatefulAuthHelper {
        return when (creds.authMethod) {
            AuthMethod.APP -> StatefulAuthHelper(http, creds, tokenStore)
            AuthMethod.WEBAPP -> TODO("Web apps aren't supported yet")
            else -> throw IllegalArgumentException("AuthMethod ${creds.authMethod} should use automatic authentication")
        }
    }

    @Throws(IllegalStateException::class)
    @JvmStatic fun fromTokenStore(http: NetworkAdapter, creds: Credentials, tokenStore: TokenStore, username: String): RedditClient {
        var reddit: RedditClient? = null
        val current = tokenStore.fetchCurrent(username)
        if (current != null && current.expiration.after(Date()))
            reddit = RedditClient(http, current, creds, tokenStore, username)

        val refreshToken = tokenStore.fetchRefreshToken(username)
        if (refreshToken != null) {
            val emptyData = OAuthData("", "", -1, listOf(), refreshToken, Date(0L))
            reddit = RedditClient(http, emptyData, creds, tokenStore, username)
        }

        if (reddit == null)
            throw IllegalStateException("No unexpired OAuthData or refresh token for user '$username'")
        return reddit
    }

    @JvmStatic internal fun scriptOAuthData(http: NetworkAdapter, creds: Credentials): OAuthData {
        if (creds.authMethod != AuthMethod.SCRIPT)
            throw IllegalArgumentException("This function is for script apps only")

        try {
            return http.execute(HttpRequest.Builder()
                .post(mapOf(
                    "grant_type" to "password",
                    "username" to creds.username!!,
                    "password" to creds.password!!
                ))
                .url("https://www.reddit.com/api/v1/access_token")
                .basicAuth(creds.clientId to creds.clientSecret)
                .build()).deserialize()
        } catch (e: NetworkException) {
            if (e.res.code == 401)
                throw IllegalArgumentException("Invalid credentials", e)
            throw e
        }
    }

    @JvmStatic internal fun applicationOnlyOAuthData(http: NetworkAdapter, creds: Credentials): OAuthData {
        if (!creds.authMethod.isUserless)
            throw IllegalArgumentException("${creds.authMethod} is not a userless authentication method")

        val grantType = if (creds.authMethod == AuthMethod.USERLESS_APP)
            "https://oauth.reddit.com/grants/installed_client"
        else
            "client_credentials"

        val postBody = mutableMapOf("grant_type" to grantType)
        if (creds.authMethod == AuthMethod.USERLESS_APP)
            postBody.put("device_id", creds.deviceId.toString())

        return http.execute(HttpRequest.Builder()
            .url("https://www.reddit.com/api/v1/access_token")
            .post(postBody)
            .basicAuth(creds.clientId to creds.clientSecret)
            .build()).deserialize()
    }
}
