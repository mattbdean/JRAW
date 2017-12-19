package net.dean.jraw.oauth

import java.util.*

/** This is a data class that represents the credentials to an OAuth2 app */
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
    /** Factory construction methods */
    companion object {
        /** Creates credentials for a script app */
        @JvmStatic fun script(username: String, password: String, clientId: String, clientSecret: String) =
            Credentials(AuthMethod.SCRIPT,
                username = username,
                password = password,
                clientId = clientId,
                clientSecret = clientSecret)

        /** Creates credentials for an installed app (Android, iOS, etc.) */
        @JvmStatic fun installedApp(clientId: String, redirectUrl: String) =
            Credentials(AuthMethod.APP,
                clientId = clientId,
                clientSecret = "",
                redirectUrl = redirectUrl)

        /** Creates credentials for a web app */
        @JvmStatic fun webapp(clientId: String, clientSecret: String, redirectUrl: String) =
            Credentials(AuthMethod.WEBAPP,
                clientId = clientId,
                clientSecret = clientSecret,
                redirectUrl = redirectUrl)

        /** Creates credentials for a script or web app running without the context of a user */
        @JvmStatic fun userless(clientId: String, clientSecret: String, deviceId: UUID) =
            Credentials(AuthMethod.USERLESS,
                clientId = clientId,
                clientSecret = clientSecret,
                deviceId = deviceId)

        /** Creates credentials for an installed app running without the context of a user */
        @JvmStatic fun userlessApp(clientId: String, deviceId: UUID) =
            Credentials(AuthMethod.USERLESS_APP,
                clientId = clientId,
                clientSecret = "",
                deviceId = deviceId)
    }
}
