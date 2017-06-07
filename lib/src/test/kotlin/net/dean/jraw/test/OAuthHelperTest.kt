package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.http.oauth.Credentials
import net.dean.jraw.http.oauth.OAuthHelper
import net.dean.jraw.http.oauth.StatefulAuthHelper
import net.dean.jraw.test.util.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class OAuthHelperTest: Spek({
    describe("script") {
        it("should produce a RedditClient authenticated with a script app") {
            ensureAuthenticated(OAuthHelper.script(CredentialsUtil.script, newOkHttpAdapter()))
        }

        it("should throw an exception when given a non-script Credentials") {
            expectException(IllegalArgumentException::class) {
                OAuthHelper.script(CredentialsUtil.app, newOkHttpAdapter())
            }
        }
    }

    describe("installedApp") {
        it("should return a fresh StatefulAuthHelper") {
            OAuthHelper.installedApp(CredentialsUtil.app, newOkHttpAdapter()).authStatus
                .should.equal(StatefulAuthHelper.Status.INIT)
        }

        it("should throw an Exception when given a non-installedApp Credentials") {
            expectException(IllegalArgumentException::class) {
                OAuthHelper.installedApp(CredentialsUtil.script, newOkHttpAdapter())
            }
        }
    }

    describe("applicationOnly") {
        it("should produce an authorized RedditClient for a userlessApp Credentials") {
            val credentials = Credentials.userlessApp(CredentialsUtil.app.clientId, UUID.randomUUID())

            // Create a RedditClient with application only and send a request to make sure it works properly
            ensureAuthenticated(OAuthHelper.applicationOnly(credentials, newOkHttpAdapter()))
        }

        it("should produce an authorized RedditClient for a userless Credentials") {
            val sourceCreds = CredentialsUtil.script
            val creds = Credentials.userless(sourceCreds.clientId, sourceCreds.clientSecret, UUID.randomUUID())

            ensureAuthenticated(OAuthHelper.applicationOnly(creds, newOkHttpAdapter()))
        }

        it("should throw an Exception when given a non-userless Credentials") {
            expectException(IllegalArgumentException::class) {
                OAuthHelper.applicationOnly(CredentialsUtil.script, newOkHttpAdapter())
            }
        }
    }
})
