package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.oauth.AccountHelper
import net.dean.jraw.oauth.AuthManager
import net.dean.jraw.oauth.AuthMethod
import net.dean.jraw.oauth.NoopTokenStore
import net.dean.jraw.test.*
import okhttp3.HttpUrl
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*
import kotlin.properties.Delegates

class AccountHelperTest : Spek({
    val username = "foo"
    val tokenStore = InMemoryTokenStore()
    val mockAdapter = MockNetworkAdapter()
    val uuid = UUID.randomUUID()
    val creds = createMockCredentials(AuthMethod.APP)

    val oauthDataJson = """{"expires_in":3600, "access_token":"<token>", "token_type": "bearer", "scope": "*"}"""

    beforeEachTest {
        tokenStore.reset()
        mockAdapter.start()
    }

    describe("<init>") {
        it("should throw a IllegalArgumentException if given the wrong type of Credentials") {
            val (valid, invalid) = AuthMethod.values().partition { it == AuthMethod.APP || it == AuthMethod.WEBAPP }

            for (m in valid)
                AccountHelper(NoopNetworkAdapter, createMockCredentials(m), NoopTokenStore(), uuid)

            for (m in invalid)
                expectException(IllegalArgumentException::class) {
                    AccountHelper(NoopNetworkAdapter, createMockCredentials(m), NoopTokenStore(), uuid)
                }
        }
    }

    describe("switchToUserless") {
        it("should use unexpired OAuthData when available") {
            tokenStore.storeLatest(AuthManager.USERNAME_USERLESS, createMockOAuthData(false))

            val h = AccountHelper(NoopNetworkAdapter, creds, tokenStore, uuid)
            // Should not require an HTTP request
            val reddit = h.switchToUserless()
            reddit.authManager.currentUsername().should.equal(AuthManager.USERNAME_USERLESS)
            reddit.should.be.of.identity(h.reddit)
        }

        it("should fetch new OAuthData when there is no unexpired data in the store") {
            val h = AccountHelper(mockAdapter, creds, NoopTokenStore(), uuid)

            mockAdapter.enqueue(oauthDataJson)

            h.switchToUserless()
            mockAdapter.mockServer.requestCount.should.equal(1)
        }
    }

    describe("trySwitchToUser") {
        var helper: AccountHelper by Delegates.notNull()

        beforeEachTest {
            helper = AccountHelper(NoopNetworkAdapter, creds, tokenStore, uuid)
        }

        it("should return null when there is no data to use") {
            helper.trySwitchToUser("some random user").should.be.`null`
        }

        it("should use an unexpired OAuthData when available") {
            tokenStore.storeLatest(username, createMockOAuthData())
            helper.trySwitchToUser(username).should.not.be.`null`
        }

        it("should use a refresh token when available") {
            val refreshToken = "<refresh token>"
            tokenStore.storeRefreshToken(username, refreshToken)

            val r = helper.trySwitchToUser(username)
            r.should.not.be.`null`
            r!!.authManager.refreshToken.should.equal(refreshToken)

            // When only a refresh token is available AccountHelper gives the RedditClient a "fake" OAuthData instance
            // that's already expired with a valid refresh token
            r.authManager.current.should.not.be.`null`
            r.authManager.current!!.isExpired().should.be.`true`

            // Make sure the AccountHelper forces the client to renew its access token on the first request
            r.forceRenew.should.be.`true`
        }

        it("should update the helper's RedditClient reference on success") {
            tokenStore.storeLatest(username, createMockOAuthData())
            val r = helper.trySwitchToUser(username)!!
            (r === helper.reddit).should.be.`true`
            tokenStore.reset()

            tokenStore.storeRefreshToken(username, "<refresh token>")
            val r2 = helper.trySwitchToUser(username)!!
            (r2 === helper.reddit).should.be.`true`
        }
    }

    describe("switchToUser") {
        it("should throw an IllegalStateException when trySwitchToUser returns null") {
            val helper = AccountHelper(NoopNetworkAdapter, creds, tokenStore, uuid)

            helper.trySwitchToUser(username).should.be.`null`
            expectException(IllegalStateException::class) {
                helper.switchToUser(username)
            }
        }
    }

    describe("switchToNewUser") {
        it("should update the client after authentication") {
            val helper = AccountHelper(mockAdapter, creds, tokenStore, uuid)

            val statefulHelper = helper.switchToNewUser()
            val url = HttpUrl.parse(statefulHelper.getAuthorizationUrl(scopes = "foo"))!!
            val state = url.queryParameter("state")!!

            val mockedRedirectUrl = "https://google.com/?state=$state&code=mocked_code"

            mockAdapter.enqueue(oauthDataJson)
            // Fake the /api/v1/me call with only the important data since we can't specify an override username
            mockAdapter.enqueue("""{"name": "foo"}""")

            val reddit = statefulHelper.onUserChallenge(mockedRedirectUrl)
            // The onAuthenticated callback should assign helper's client to the newly authenticated client
            (reddit === helper.reddit).should.be.`true`
        }
    }

    describe("isAuthenticated") {
        it("should return false when there is no managed RedditClient") {
            val helper = AccountHelper(mockAdapter, creds, tokenStore, uuid)

            // No RedditClient instance yet
            helper.isAuthenticated().should.be.`false`
        }

        it("should return true after switching to a user") {
            val helper = AccountHelper(mockAdapter, creds, tokenStore, uuid)

            // Store some unexpired mock OAuthData
            tokenStore.storeLatest(username, createMockOAuthData())

            // Since we have unexpired data, isAuthenticated() should return true
            helper.switchToUser(username)
            helper.isAuthenticated().should.be.`true`

        }

        it("should return true if there is no OAuthData but there is a refresh token") {
            val helper = AccountHelper(mockAdapter, creds, tokenStore, uuid)
            tokenStore.storeRefreshToken(username, "<refresh_token>")

            // Having the refresh token will cause authManager.needsRenewing() to return true
            helper.switchToUser(username)
            helper.isAuthenticated().should.be.`true`
        }
    }

    afterEachTest {
        mockAdapter.reset()
    }
})
