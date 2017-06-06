package net.dean.jraw.http.oauth

import com.fasterxml.jackson.module.kotlin.readValue
import net.dean.jraw.JrawUtils
import net.dean.jraw.RedditClient
import net.dean.jraw.http.HttpAdapter
import net.dean.jraw.http.HttpRequest
import net.dean.jraw.http.NetworkException

object OAuthHelper {
    @JvmStatic fun script(creds: Credentials, http: HttpAdapter): RedditClient {
        if (creds.authenticationMethod !== AuthenticationMethod.SCRIPT)
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
                .build()).json.toString()
            return RedditClient(http, creds.authenticationMethod, JrawUtils.jackson.readValue<OAuthData>(data))
        } catch (e: NetworkException) {
            if (e.res.code() == 401)
                throw IllegalArgumentException("Invalid credentials", e)
            throw e
        }
    }
}
