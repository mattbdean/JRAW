package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.JrawUtils
import net.dean.jraw.oauth.AuthManager
import net.dean.jraw.oauth.AuthMethod
import net.dean.jraw.oauth.Credentials
import net.dean.jraw.oauth.TokenPersistenceStrategy
import net.dean.jraw.test.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*
import kotlin.properties.Delegates

class AuthManagerTest : Spek({
    val mockAdapter = MockNetworkAdapter()

    fun mockOAuthDataResponse() = """
{
    "access_token": "accessToken",
    "token_type": "bearer",
    "expires_in": 3600,
    "scope": "*"
}
"""

    describe("renew") {
        beforeEachTest {
            mockAdapter.start()
        }

        it("should refresh the OAuthData for script apps") {
            val authManager = AuthManager(mockAdapter, createMockCredentials(AuthMethod.SCRIPT))
            authManager.update(createMockOAuthData())

            // Make some baseline assertions
            authManager.canRenew().should.be.`true`
            authManager.needsRenewing().should.be.`false`
            authManager.current!!.expiration.should.not.be.`null`

            // Save the previous expiration date and enqueue the refresh response
            val prevExpiration = authManager.current!!.expiration

            // Simulate refreshing the token
            mockAdapter.enqueue(mockOAuthDataResponse())
            authManager.renew()

            // Expect the manager's state to have updated
            authManager.current!!.expiration.should.be.above(prevExpiration)
            authManager.canRenew().should.be.`true`
            authManager.needsRenewing().should.be.`false`

            // Ensure the manager simply requested another token the same way it was initially requested
            val reauthRequest = mockAdapter.mockServer.takeRequest()
            reauthRequest.path.should.equal("/api/v1/access_token")
            val formBody = JrawUtils.parseUrlEncoded(reauthRequest.body.readUtf8())
            formBody.should.contain("grant_type" to "password")
        }

        it("should use the refresh token for installed/web apps") {
            val authManager = AuthManager(mockAdapter, CredentialsUtil.app)
            val oauthData = createMockOAuthData(includeRefreshToken = true)
            // Ensure refreshToken gets updated once _current is set
            authManager.refreshToken.should.be.`null`
            authManager.update(oauthData)
            authManager.refreshToken.should.equal(oauthData.refreshToken)

            // Simulate the token renewal. This response will NOT have a refresh token included in it (like the real
            // reddit API)
            mockAdapter.enqueue(mockOAuthDataResponse())
            authManager.renew()

            // Even though the new response didn't include a refresh token, the refresh token should remain the same.
            authManager.refreshToken.should.equal(oauthData.refreshToken)

            // Ensure we're using the refresh token to request a new access token
            val reauthRequest = mockAdapter.mockServer.takeRequest()
            reauthRequest.path.should.equal("/api/v1/access_token")
            val formBody = JrawUtils.parseUrlEncoded(reauthRequest.body.readUtf8())
            formBody.should.contain("grant_type" to "refresh_token")
            formBody.should.contain("refresh_token" to authManager.refreshToken!!)
        }

        it("should fail when a non-script app does not have a refresh token") {
            val authManager = AuthManager(mockAdapter, CredentialsUtil.app)
            authManager.update(createMockOAuthData(includeRefreshToken = false))

            authManager.canRenew().should.be.`false`
            expectException(IllegalStateException::class) {
                authManager.renew()
            }
        }

        afterEachTest {
            mockAdapter.reset()
        }
    }

    describe("tokenPersistenceStrategy") {
        val username = "some_user"
        var authManager: AuthManager by Delegates.notNull()
        val mockStore = InMemoryTokenStore()

        beforeEachTest {
            authManager = AuthManager(NoopNetworkAdapter, createMockCredentials(AuthMethod.APP))
            authManager.currentUsername = username
            authManager.tokenStore = mockStore
            mockStore.reset()
        }

        it("should only save the current OAuthData and refresh token (when available)") {
            // Make sure we're testing the write strategy
            authManager.tokenPersistenceStrategy = TokenPersistenceStrategy.ALL

            // Make some baseline assertions
            mockStore.fetchCurrent(username).should.be.`null`
            mockStore.fetchRefreshToken(username).should.be.`null`

            // This should trigger storing only the current OAuthData
            var data = createMockOAuthData(includeRefreshToken = false)
            authManager.update(data)

            // Make sure it was saved
            mockStore.fetchCurrent(username).should.equal(data)

            mockStore.reset()
            // Including a refresh token in the OAuthData should trigger saving both the OAuthData and the refresh token
            data = createMockOAuthData(includeRefreshToken = true)
            authManager.update(data)
            mockStore.fetchCurrent(username).should.equal(data)
            mockStore.fetchRefreshToken(username).should.equal(data.refreshToken)
        }

        it("should call the TokenStore for refresh tokens only for the REFRESH_ONLY strategy") {
            authManager.tokenPersistenceStrategy = TokenPersistenceStrategy.REFRESH_ONLY

            // Baseline
            mockStore.fetchCurrent(username).should.be.`null`
            mockStore.fetchRefreshToken(username).should.be.`null`

            var data = createMockOAuthData(includeRefreshToken = false)
            authManager.update(data)

            // We're not saving OAuthData and there was no refresh token
            mockStore.fetchCurrent(username).should.be.`null`
            mockStore.fetchRefreshToken(username).should.be.`null`

            data = createMockOAuthData(includeRefreshToken = true)
            authManager.update(data)

            // Should have saved only the refresh token
            mockStore.fetchCurrent(username).should.be.`null`
            mockStore.fetchRefreshToken(username).should.equal(data.refreshToken)
        }
    }

    describe("currentUsername (both the property and the method)") {
        it("should return the name of the authenticated user") {
            val authManager = AuthManager(NoopNetworkAdapter, createMockCredentials(AuthMethod.SCRIPT))
            authManager.currentUsername = "some_username"
            authManager.currentUsername().should.equal("some_username")
        }

        it("should return a special value when using userless credentials") {
            val authManager = AuthManager(NoopNetworkAdapter, Credentials.userlessApp("", UUID.randomUUID()))
            authManager.currentUsername.should.be.`null`
            authManager.currentUsername().should.equal(AuthManager.USERNAME_USERLESS)
        }

        it("should return a special value for non-userless credentials for unknown usernames") {
            val authManager = AuthManager(NoopNetworkAdapter, Credentials.installedApp("", ""))
            authManager.currentUsername.should.be.`null`
            authManager.currentUsername().should.equal(AuthManager.USERNAME_UNKOWN)
        }
    }

    describe("needsRefresh()") {
        it("should return true when it has no OAuthData") {
            AuthManager(NoopNetworkAdapter, CredentialsUtil.script).needsRenewing().should.be.`true`
        }

        it("should return true when the tokenExpiration is in the past") {
            val auth = AuthManager(NoopNetworkAdapter, CredentialsUtil.script)
            auth.update(createMockOAuthData().withExpiration(Date(Date().time - 1)))
            auth.needsRenewing().should.be.`true`
        }
    }

    describe("revokeAccessToken/revokeRefreshToken") {
        beforeEachTest {
            mockAdapter.start()
        }

        it("should deuathenticated the RedditClient and remove the relevant data from the TokenStore") {
            val username = "foo"
            val auth = AuthManager(mockAdapter, CredentialsUtil.script)
            auth.tokenStore = InMemoryTokenStore()

            // Put initial OAuthData and refresh token into TokenStore
            auth.currentUsername = username
            auth.update(createMockOAuthData(includeRefreshToken = true))

            // Not actual response
            mockAdapter.enqueue("{}")
            auth.revokeAccessToken()
            auth.current.should.be.`null`
            auth.tokenStore.fetchCurrent(username).should.be.`null`

            // Again, not actual response
            mockAdapter.enqueue("{}")
            auth.revokeRefreshToken()
            auth.refreshToken.should.be.`null`
            auth.tokenStore.fetchRefreshToken(username).should.be.`null`
        }

        it("should do nothing if there isn't any data") {
            val auth = AuthManager(NoopNetworkAdapter, createMockCredentials(AuthMethod.SCRIPT))
            auth.current.should.be.`null`
            auth.refreshToken.should.be.`null`

            // Normally these would sent HTTP requests, but they shouldn't considering they have nothing to revoke
            auth.revokeAccessToken()
            auth.revokeRefreshToken()
        }

        afterEachTest {
            mockAdapter.reset()
        }
    }
})
