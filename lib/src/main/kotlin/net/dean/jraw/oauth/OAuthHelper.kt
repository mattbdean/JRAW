package net.dean.jraw.oauth

import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.NetworkAdapter
import net.dean.jraw.http.NetworkException
import net.dean.jraw.models.OAuthData
import net.dean.jraw.models.internal.OAuthDataJson

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
    /**
     * Authenticates a client without user approval. This is only applicable to scripts apps and those using userless
     * mode. This is possible because reddit implicitly grants access to the app to the account that created the script,
     * and userless mode by definition has no user to authenticate. The NetworkAdapter, Credentials, and TokenStore are
     * eventually given to the RedditClient.
     */
    @JvmStatic @JvmOverloads fun automatic(http: NetworkAdapter, creds: Credentials, tokenStore: TokenStore = NoopTokenStore()): RedditClient {
        return when {
            creds.authMethod.isUserless ->
                RedditClient(http, applicationOnlyOAuthData(http, creds), creds, tokenStore, overrideUsername = AuthManager.USERNAME_USERLESS)
            creds.authMethod == AuthMethod.SCRIPT ->
                RedditClient(http, scriptOAuthData(http, creds), creds, tokenStore, overrideUsername = creds.username)
            else -> throw IllegalArgumentException("AuthMethod ${creds.authMethod} is not eligible for automatic authentication")
        }
    }

    /**
     * Starts the authentication process for an installed or web app. Like with [automatic], the NetworkAdapter,
     * Credentials, and TokenStore are eventually given to the RedditClient after authentication.
     */
    @JvmStatic @JvmOverloads fun interactive(http: NetworkAdapter, creds: Credentials, tokenStore: TokenStore = NoopTokenStore()) =
        interactive(http, creds, tokenStore, onAuthenticated = {})

    @JvmStatic internal fun interactive(http: NetworkAdapter, creds: Credentials,
                                             tokenStore: TokenStore = NoopTokenStore(),
                                             onAuthenticated: (r: RedditClient) -> Unit = {}): StatefulAuthHelper {
        if (creds.authMethod != AuthMethod.APP && creds.authMethod != AuthMethod.WEBAPP) {
            throw IllegalArgumentException("AuthMethod ${creds.authMethod} should use automatic authentication")
        }

        return StatefulAuthHelper(http, creds, tokenStore, onAuthenticated)
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
                .build()).deserialize<OAuthDataJson>().toOAuthData()
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
            .build()).deserialize<OAuthDataJson>().toOAuthData()
    }
}
