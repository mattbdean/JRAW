package net.dean.jraw.test.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.dean.jraw.http.oauth.Credentials

object CredentialsUtil {
    val script: Credentials
    val app: Credentials

    init {
        val creds = getCredentials()
        val (script, app) = creds.script to creds.app
        this.script = Credentials.script(script.username, script.password, script.clientId, script.clientSecret)
        this.app = Credentials.installedApp(app.clientId, app.redirectUrl)
    }

    private fun isTravis() =
        System.getenv("TRAVIS") != null && System.getenv("TRAVIS").toBoolean()

    private fun getCredentials(): TestingCredentials =
        if (isTravis()) getTravisCredentials() else getLocalCredentials()

    private fun getTravisCredentials(): TestingCredentials = TestingCredentials(
        ScriptStub(
            username = getenv("SCRIPT_USERNAME"),
            password = getenv("SCRIPT_PASSWORD"),
            clientSecret = getenv("SCRIPT_CLIENT_SECRET"),
            clientId = getenv("SCRIPT_CLIENT_ID")
        ),
        AppStub(
            clientId = getenv("APP_CLIENT_ID"),
            redirectUrl = getenv("APP_REDIRECT_URL")
        )
    )

    private fun getLocalCredentialStream() = CredentialsUtil::class.java.getResourceAsStream("/credentials.json") ?:
        throw SetupRequiredException("Could not load credentials.json")

    private fun getLocalCredentials(): TestingCredentials =
        jacksonObjectMapper().readValue<TestingCredentials>(getLocalCredentialStream())

    private fun getenv(name: String) = System.getenv(name) ?:
        throw IllegalStateException("Expecting environmental variable $name to exist")
}
