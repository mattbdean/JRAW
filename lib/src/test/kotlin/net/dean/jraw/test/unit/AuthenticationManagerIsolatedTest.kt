package net.dean.jraw.test.unit

import com.winterbe.expekt.should
import net.dean.jraw.JrawUtils
import net.dean.jraw.http.oauth.AuthenticationManager
import net.dean.jraw.test.CredentialsUtil
import net.dean.jraw.test.MockHttpAdapter
import net.dean.jraw.test.createMockOAuthData
import net.dean.jraw.test.expectException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class AuthenticationManagerIsolatedTest : Spek({
    val mockAdapter = MockHttpAdapter()

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

        it("should return a new OAuthData when called for script apps") {
            val authManager = AuthenticationManager(mockAdapter, CredentialsUtil.script)
            authManager._current = createMockOAuthData()

            // Make some baseline assertions
            authManager.canRenew().should.be.`true`
            authManager.needsRenewing().should.be.`false`
            authManager.tokenExpiration.should.not.be.`null`

            // Save the previous expiration date and enqueue the refresh response
            val prevExpiration = authManager.tokenExpiration!!

            // Simulate refreshing the token
            mockAdapter.enqueue(mockOAuthDataResponse())
            authManager.renew()

            // Expect the manager's state to have updated
            authManager.tokenExpiration.should.be.above(prevExpiration)
            authManager.canRenew().should.be.`true`
            authManager.needsRenewing().should.be.`false`

            // Ensure the manager simply requested another token the same way it was initially requested
            val reauthRequest = mockAdapter.mockServer.takeRequest()
            reauthRequest.path.should.equal("/api/v1/access_token")
            val formBody = JrawUtils.parseUrlEncoded(reauthRequest.body.readUtf8())
            formBody.should.contain("grant_type" to "password")
        }

        it("should use the refresh token for installed/web apps") {
            val authManager = AuthenticationManager(mockAdapter, CredentialsUtil.app)
            val oauthData = createMockOAuthData(includeRefreshToken = true)
            // Ensure refreshToken gets updated once _current is set
            authManager.refreshToken.should.be.`null`
            authManager._current = oauthData
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
             val authManager = AuthenticationManager(mockAdapter, CredentialsUtil.app)
             authManager._current = createMockOAuthData(includeRefreshToken = false)

             authManager.canRenew().should.be.`false`
             expectException(IllegalStateException::class) {
                 authManager.renew()
             }
         }

        afterEachTest {
            mockAdapter.reset()
        }
    }

    describe("needsRefresh()") {
        it("should return true when it has no OAuthData") {
            AuthenticationManager(mockAdapter, CredentialsUtil.script).needsRenewing().should.be.`true`
        }

        it("should return true when the tokenExpiration is in the past") {
            val auth = AuthenticationManager(mockAdapter, CredentialsUtil.script)
            auth.tokenExpiration = Date(Date().time - 1)
            auth.needsRenewing().should.be.`true`
        }
    }
})
