package net.dean.jraw.http.oauth

import com.fasterxml.jackson.module.kotlin.readValue
import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpAdapter
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.NetworkException
import java.math.BigInteger
import java.net.URL
import java.security.SecureRandom

class StatefulAuthHelper internal constructor(private val http: HttpAdapter, private val creds: Credentials) {
    private var state: String? = null
    private var _authStatus: Status = Status.INIT
    val authStatus: Status
        get() = _authStatus

    fun getAuthorizationUrl(permanent: Boolean = true, useMobileSite: Boolean = false, vararg scopes: String): String {
        // Generate a random alpha-numeric string
        // http://stackoverflow.com/a/41156
        state = BigInteger(130, rand).toString(32)

        this._authStatus = Status.WAITING_FOR_CHALLENGE

        // Use HttpRequest.Builder as an interface to create a URL
        return HttpRequest.Builder()
            .secure(true)
            .host(OAuthHelper.HOST_WWW)
            .path("/api/v1/authorize${if (useMobileSite) ".compact" else ""}")
            .query(mapOf(
                "client_id" to creds.clientId,
                "response_type" to "code",
                "state" to state!!,
                "redirect_uri" to creds.redirectUrl!!,
                "duration" to if (permanent) "permanent" else "temporary",
                "scope" to scopes.joinToString(separator = " ")
            )).build().url
    }

    @Throws(NetworkException::class, OAuthException::class, IllegalStateException::class)
    fun onUserChallenge(finalUrl: String): RedditClient {
        if (authStatus != Status.WAITING_FOR_CHALLENGE)
            throw IllegalStateException("Expecting auth status ${Status.WAITING_FOR_CHALLENGE}, got $authStatus")

        val query = parseQuery(URL(finalUrl).query)
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
            val response = http.executeSync(HttpRequest.Builder()
                .secure(true)
                .host(OAuthHelper.HOST_WWW)
                .path("/api/v1/access_token")
                .post(mapOf(
                    "grant_type" to "authorization_code",
                    "code" to code,
                    "redirect_uri" to creds.redirectUrl!!
                ))
                .basicAuth(creds.clientId to creds.clientSecret)
                .build()).body

            this._authStatus = Status.AUTHORIZED
            return RedditClient(
                http = http,
                authMethod = creds.authenticationMethod,
                oauthData = JrawUtils.jackson.readValue<OAuthData>(response)
            )
        } catch (ex: NetworkException) {
            if (ex.res.code() == 401)
                throw OAuthException("Invalid client ID/secret", ex)
            throw ex
        }
    }

    private fun parseQuery(query: String): Map<String, String> =
        // Neat little one-liner. This function splits the query string by '&', then maps each value to a Pair of
        // Strings, converts it to a typed array, and uses the spread operator ('*') to call mapOf()
        mapOf(*query.split("&").map { val parts = it.split("="); parts[0] to parts[1] }.toTypedArray())

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
