package net.dean.jraw.http.oauth

import com.fasterxml.jackson.module.kotlin.readValue
import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpAdapter
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.NetworkException

object OAuthHelper {
    const val HOST_WWW = "www.reddit.com"

    @JvmStatic fun script(creds: Credentials, http: HttpAdapter): RedditClient {
        if (creds.authenticationMethod != AuthenticationMethod.SCRIPT)
            throw IllegalArgumentException("This function is for script apps only")

        try {
            val data = http.executeSync(HttpRequest.Builder()
                .post(mapOf(
                    "grant_type" to "password",
                    "username" to creds.username!!,
                    "password" to creds.password!!
                ))
                .url("https://www.reddit.com/api/v1/access_token")
                .basicAuth(creds.clientId to creds.clientSecret)
                .build()).body

            return createRedditClient(http, creds, data)
        } catch (e: NetworkException) {
            if (e.res.code() == 401)
                throw IllegalArgumentException("Invalid credentials", e)
            throw e
        }
    }

    @JvmStatic fun installedApp(creds: Credentials, http: HttpAdapter): StatefulAuthHelper {
        if (creds.authenticationMethod != AuthenticationMethod.APP)
            throw IllegalArgumentException("This function is for installed apps only")
        return StatefulAuthHelper(http, creds)
    }

    @JvmStatic fun applicationOnly(creds: Credentials, http: HttpAdapter): RedditClient {
        if (!creds.authenticationMethod.isUserless)
            throw IllegalArgumentException("${creds.authenticationMethod} is not a userless authentication method")

        val grantType = if (creds.authenticationMethod == AuthenticationMethod.USERLESS_APP)
            "https://oauth.reddit.com/grants/installed_client"
        else
            "client_credentials"

        val postBody = mutableMapOf("grant_type" to grantType)
        if (creds.authenticationMethod == AuthenticationMethod.USERLESS_APP)
            postBody.put("device_id", creds.deviceId.toString())

        val data = http.executeSync(HttpRequest.Builder()
            .host(HOST_WWW)
            .path("/api/v1/access_token")
            .post(postBody)
            .basicAuth(creds.clientId to creds.clientSecret)
            .build()).body

        return createRedditClient(http, creds, data)
    }

    private fun createRedditClient(http: HttpAdapter, creds: Credentials, oauthData: String) =
        RedditClient(http, creds.authenticationMethod, JrawUtils.jackson.readValue<OAuthData>(oauthData))
}
