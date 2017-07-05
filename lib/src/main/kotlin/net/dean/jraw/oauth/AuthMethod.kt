package net.dean.jraw.oauth

/**
 * A list of ways a client can authenticate themselves using Reddit's API
 */
enum class AuthMethod constructor(
    /** Returns true if this AuthMethod does not require a user */
    val isUserless: Boolean = false
) {
    /** Runs as a part of a web service on a server you control. Can keep a secret */
    WEBAPP,

    /**
     * Runs on devices you don't control, such as the user's phone. Cannot keep a secret, and therefore, does not
     * receive one.
     */
    APP,

    /**
     * Runs on hardware you control, such as your own laptop or server. Can keep a secret. Only has access to your
     * account.
     */
    SCRIPT,

    /**
     * OAuth2 authentication without a logged-in user. Web or script apps should use this type, which requires a secret.
     * Installed apps (which cannot keep a secret) should use [USERLESS_APP].
     */
    USERLESS(true),

    /**
     * OAuth2 authentication without the context of a user. Use this over [USERLESS] if this is being used on a
     * mobile app and thus cannot retain a secret.
     */
    USERLESS_APP(true)
}
