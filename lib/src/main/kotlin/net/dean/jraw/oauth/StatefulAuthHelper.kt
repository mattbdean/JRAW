package net.dean.jraw.oauth

import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.NetworkAdapter
import net.dean.jraw.http.NetworkException
import net.dean.jraw.models.OAuthData
import net.dean.jraw.models.internal.OAuthDataJson
import okhttp3.HttpUrl
import java.math.BigInteger
import java.net.URL
import java.security.SecureRandom

class StatefulAuthHelper internal constructor(
    private val http: NetworkAdapter,
    private val creds: Credentials,
    private val tokenStore: TokenStore,
    private val onAuthenticated: (reddit: RedditClient) -> Unit
) {
    private var state: String? = null
    private var _authStatus: Status = Status.INIT
    val authStatus: Status
        get() = _authStatus

    fun getAuthorizationUrl(requestRefreshToken: Boolean = true, useMobileSite: Boolean = false, vararg scopes: String): String {
        // Generate a random alpha-numeric string
        // http://stackoverflow.com/a/41156
        state = BigInteger(130, rand).toString(32)

        this._authStatus = Status.WAITING_FOR_CHALLENGE

        // Use HttpRequest.Builder as an interface to create a URL
        return HttpRequest.Builder()
            .secure(true)
            .host("www.reddit.com")
            .path("/api/v1/authorize${if (useMobileSite) ".compact" else ""}")
            .query(mapOf(
                "client_id" to creds.clientId,
                "response_type" to "code",
                "state" to state!!,
                "redirect_uri" to creds.redirectUrl!!,
                "duration" to if (requestRefreshToken) "permanent" else "temporary",
                "scope" to scopes.joinToString(separator = " ")
            )).build().url
    }

    /**
     * Checks if the given URL is the URL that reddit redirects to when the user allows or denies access to the OAuth2
     * app.
     */
    fun isFinalRedirectUrl(url: String): Boolean {
        val httpUrl = HttpUrl.parse(url) ?: throw IllegalArgumentException("Malformed URL: $url")
        creds.redirectUrl ?: throw IllegalStateException("Given credentials have no redirect URL")

        return httpUrl.toString().startsWith(creds.redirectUrl) && httpUrl.queryParameter("state") != null
    }

    @Throws(NetworkException::class, OAuthException::class, IllegalStateException::class)
    fun onUserChallenge(finalUrl: String): RedditClient {
        if (authStatus != Status.WAITING_FOR_CHALLENGE)
            throw IllegalStateException("Expecting auth status ${Status.WAITING_FOR_CHALLENGE}, got $authStatus")

        val query = JrawUtils.parseUrlEncoded(URL(finalUrl).query)
        if ("error" in query)
            throw OAuthException("Reddit responded with error: ${query["error"]}")
        if ("state" !in query)
            throw IllegalArgumentException("Final redirect URL did not contain the 'state' query parameter")
        if (query["state"] != state)
            throw IllegalStateException("State did not match")
        if ("code" !in query)
            throw IllegalArgumentException("Final redirect URL did not contain the 'code' query parameter")

        val code = query["code"]!!

        try {
            val response: OAuthData = http.execute(HttpRequest.Builder()
                .secure(true)
                .host("www.reddit.com")
                .path("/api/v1/access_token")
                .post(mapOf(
                    "grant_type" to "authorization_code",
                    "code" to code,
                    "redirect_uri" to creds.redirectUrl!!
                ))
                .basicAuth(creds.clientId to creds.clientSecret)
                .build()).deserialize<OAuthDataJson>().toOAuthData()

            this._authStatus = Status.AUTHORIZED
            val r = RedditClient(
                http = http,
                initialOAuthData = response,
                creds = creds,
                tokenStore = tokenStore
            )
            onAuthenticated(r)
            return r
        } catch (ex: NetworkException) {
            if (ex.res.code == 401)
                throw OAuthException("Invalid client ID/secret", ex)
            throw ex
        }
    }

    companion object {
        private val rand: SecureRandom = SecureRandom()
    }

    enum class Status {
        /** An instance has been created by no action has been performed */
        INIT,

        /** An authorization URL has been created, but the user has not accepted/declined yet */
        WAITING_FOR_CHALLENGE,

        /** Authorized and ready to send requests */
        AUTHORIZED
    }
}
