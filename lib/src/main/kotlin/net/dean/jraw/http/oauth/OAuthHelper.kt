package net.dean.jraw.http.oauth

import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpAdapter
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.NetworkException

object OAuthHelper {
    const val HOST_WWW = "www.reddit.com"

    @JvmStatic fun script(creds: Credentials, http: HttpAdapter): RedditClient {
        return RedditClient(http, scriptOAuthData(creds, http), creds)
    }

    @JvmStatic fun applicationOnly(creds: Credentials, http: HttpAdapter): RedditClient {
        return RedditClient(http, applicationOnlyOAuthData(creds, http), creds)
    }

    @JvmStatic fun installedApp(creds: Credentials, http: HttpAdapter): StatefulAuthHelper {
        if (creds.authenticationMethod != AuthenticationMethod.APP)
            throw IllegalArgumentException("This function is for installed apps only")
        return StatefulAuthHelper(http, creds)
    }

    @JvmStatic internal fun scriptOAuthData(creds: Credentials, http: HttpAdapter): OAuthData {
        if (creds.authenticationMethod != AuthenticationMethod.SCRIPT)
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

    @JvmStatic internal fun applicationOnlyOAuthData(creds: Credentials, http: HttpAdapter): OAuthData {
        if (!creds.authenticationMethod.isUserless)
            throw IllegalArgumentException("${creds.authenticationMethod} is not a userless authentication method")

        val grantType = if (creds.authenticationMethod == AuthenticationMethod.USERLESS_APP)
            "https://oauth.reddit.com/grants/installed_client"
        else
            "client_credentials"

        val postBody = mutableMapOf("grant_type" to grantType)
        if (creds.authenticationMethod == AuthenticationMethod.USERLESS_APP)
            postBody.put("device_id", creds.deviceId.toString())

        return http.execute(HttpRequest.Builder()
            .host(HOST_WWW)
            .path("/api/v1/access_token")
            .post(postBody)
            .basicAuth(creds.clientId to creds.clientSecret)
            .build()).deserialize()
    }
}
