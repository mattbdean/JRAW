package net.dean.jraw.http.oauth

import java.util.*

class Credentials private constructor(
    /** How these credential were meant to be used */
    val authMethod: AuthMethod,

    /** Reddit username. Only non-null for script Credentials */
    val username: String? = null,

    /** Reddit password. Only non-null for script Credentials */
    val password: String? = null,

    /** OAuth2 app client ID. */
    val clientId: String,

    /** OAuth2 app client secret. This is an empty string for app or userless app Credentials. */
    val clientSecret: String,

    /** A unique, per-device ID */
    val deviceId: UUID? = null,

    /**
     * The URL that users will be redirected to when authorizing your application. Only non-null for installed or web
     * apps.
     */
    val redirectUrl: String? = null
) {
    companion object {
        @JvmStatic fun script(username: String, password: String, clientId: String, clientSecret: String) =
            Credentials(AuthMethod.SCRIPT,
                username = username,
                password = password,
                clientId = clientId,
                clientSecret = clientSecret)

        @JvmStatic fun installedApp(clientId: String, redirectUrl: String) =
            Credentials(AuthMethod.APP,
                clientId = clientId,
                clientSecret = "",
                redirectUrl = redirectUrl)

        @JvmStatic fun webapp(clientId: String, clientSecret: String, redirectUrl: String) =
            Credentials(AuthMethod.WEBAPP,
                clientId = clientId,
                clientSecret = clientSecret,
                redirectUrl = redirectUrl)

        @JvmStatic fun userless(clientId: String, clientSecret: String, deviceId: UUID) =
            Credentials(AuthMethod.USERLESS,
                clientId = clientId,
                clientSecret = clientSecret,
                deviceId = deviceId)

        @JvmStatic fun userlessApp(clientId: String, deviceId: UUID) =
            Credentials(AuthMethod.USERLESS_APP,
                clientId = clientId,
                clientSecret = "",
                deviceId = deviceId)

    }
}
