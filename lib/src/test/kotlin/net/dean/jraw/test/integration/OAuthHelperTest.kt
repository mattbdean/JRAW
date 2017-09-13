package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.models.OAuthData
import net.dean.jraw.oauth.AuthMethod
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.OAuthHelper
import net.dean.jraw.oauth.StatefulAuthHelper
import net.dean.jraw.test.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class OAuthHelperTest: Spek({
    describe("automatic") {
        it("should produce a RedditClient authenticated for a script app") {
            ensureAuthenticated(OAuthHelper.automatic(newOkHttpAdapter(), CredentialsUtil.script))
        }

        it("should produce a RedditClient authenticated for a userless Credentials") {
            ensureAuthenticated(OAuthHelper.automatic(newOkHttpAdapter(), CredentialsUtil.script))
        }

        it("should produce an authorized RedditClient for a userlessApp Credentials") {
            val credentials = Credentials.userlessApp(CredentialsUtil.app.clientId, UUID.randomUUID())

            // Create a RedditClient with application only and send a request to make sure it works properly
            val reddit = OAuthHelper.automatic(newOkHttpAdapter(), credentials)
            ensureAuthenticated(reddit)
            reddit.authManager.renew()
            ensureAuthenticated(reddit)

        }

        it("should produce an authorized RedditClient for a userless Credentials") {
            val reddit = OAuthHelper.automatic(newOkHttpAdapter(), CredentialsUtil.applicationOnly)
            ensureAuthenticated(reddit)
            reddit.authManager.renew()
            ensureAuthenticated(reddit)
        }

        it("should throw an Exception when given Credentials for an app") {
            expectException(IllegalArgumentException::class) {
                OAuthHelper.automatic(newOkHttpAdapter(), CredentialsUtil.app)
            }
        }
    }

    describe("interactive") {
        it("should return a fresh StatefulAuthHelper") {
            OAuthHelper.interactive(newOkHttpAdapter(), CredentialsUtil.app).authStatus
                .should.equal(StatefulAuthHelper.Status.INIT)
        }

        it("should throw an Exception when given a non-installedApp Credentials") {
            expectException(IllegalArgumentException::class) {
                OAuthHelper.interactive(newOkHttpAdapter(), CredentialsUtil.script)
            }
        }
    }

    describe("fromTokenStore") {
        val tokenStore = InMemoryTokenStore()
        val username = CredentialsUtil.script.username!!

        beforeEachTest {
            tokenStore.reset()
        }

        it("should throw an IllegalStateException when there is no data for a given username") {
            expectException(IllegalStateException::class) {
                OAuthHelper.fromTokenStore(NoopNetworkAdapter, createMockCredentials(AuthMethod.SCRIPT), tokenStore, username)
            }
        }

        it("should create an authenticated client from non-expired OAuthData") {
            val r = OAuthHelper.automatic(newOkHttpAdapter(), CredentialsUtil.script, tokenStore)
            ensureAuthenticated(r)
            tokenStore.fetchCurrent(username).should.not.be.`null`

            val fromCache = OAuthHelper.fromTokenStore(newOkHttpAdapter(), CredentialsUtil.script, tokenStore, CredentialsUtil.script.username!!)
            fromCache.authManager.current.should.not.be.`null`
            fromCache.authManager.current.should.equal(tokenStore.fetchCurrent(username))
            fromCache.authManager.currentUsername.should.equal(username)
            ensureAuthenticated(fromCache)
        }

        it("should throw an exception when there is only expired OAuthData available") {
            tokenStore.storeCurrent(username, OAuthData.create(
                /* accessToken  = */ "",
                /* scopes  = */ listOf(),
                /* refreshToken  = */ null,
                // Expired 1 ms in past
                /* expiration  = */ Date(Date().time - 1)
            ))

            expectException(IllegalStateException::class) {
                OAuthHelper.fromTokenStore(NoopNetworkAdapter, createMockCredentials(AuthMethod.SCRIPT), tokenStore, username)
            }
        }

        it("should create an authenticated client with only the refresh token") {
            val reddit = emulateBrowserAuth()
            ensureAuthenticated(reddit)
            val store = reddit.authManager.tokenStore as InMemoryTokenStore
            store.resetDataOnly()

            store.fetchRefreshToken(username).should.not.be.`null`
            val reddit2 = OAuthHelper.fromTokenStore(newOkHttpAdapter(), CredentialsUtil.app, store, username)
            reddit2.requireAuthenticatedUser()
            ensureAuthenticated(reddit2)
        }
    }
})
