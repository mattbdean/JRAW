package net.dean.jraw.test

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import net.dean.jraw.oauth.Credentials
import okio.Okio
import java.util.*

object CredentialsUtil {
    val script: Credentials
    val app: Credentials
    val applicationOnly: Credentials
    val moderationSubreddit: String

    init {
        val creds = getCredentials()
        val (script, app) = creds.script to creds.app
        this.script = Credentials.script(script.username, script.password, script.clientId, script.clientSecret)
        this.app = Credentials.installedApp(app.clientId, app.redirectUrl)
        this.moderationSubreddit = creds.moderationSubreddit
        this.applicationOnly = Credentials.userless(script.clientId, script.clientSecret, UUID.randomUUID())
    }

    private fun isTravis() =
        System.getenv("TRAVIS") != null && System.getenv("TRAVIS")!!.toBoolean()

    private fun getCredentials(): TestingCredentials =
        if (isTravis()) getTravisCredentials() else getLocalCredentials()

    private fun getTravisCredentials(): TestingCredentials = TestingCredentials(
        script = ScriptStub(
            username = getenv("SCRIPT_USERNAME"),
            password = getenv("SCRIPT_PASSWORD"),
            clientSecret = getenv("SCRIPT_CLIENT_SECRET"),
            clientId = getenv("SCRIPT_CLIENT_ID")
        ),
        app = AppStub(
            clientId = getenv("APP_CLIENT_ID"),
            redirectUrl = getenv("APP_REDIRECT_URL")
        ),
        moderationSubreddit = "jraw_testing2"
    )

    private fun getLocalCredentialStream() = CredentialsUtil::class.java.getResourceAsStream("/credentials.json") ?:
        throw SetupRequiredException("Could not load credentials.json")

    private fun getLocalCredentials(): TestingCredentials {
        try {
            val source = Okio.buffer(Okio.source(getLocalCredentialStream()))
            return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
                .adapter<TestingCredentials>(TestingCredentials::class.java).fromJson(source)!!
        } catch (e: Exception) {
            System.err.println("${e.javaClass.name}: ${e.message}")
            throw e
        }
    }

    private fun getenv(name: String) = System.getenv(name) ?:
        throw IllegalStateException("Expecting environmental variable $name to exist")
}
