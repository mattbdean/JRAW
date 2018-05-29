package net.dean.jraw.test.integration

import com.gargoylesoftware.htmlunit.html.HtmlInput
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.winterbe.expekt.should
import net.dean.jraw.Endpoint
import net.dean.jraw.oauth.OAuthException
import net.dean.jraw.test.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.xit

class StatefulAuthHelperTest : Spek({
    // TODO inconsistent test b/c of emulateBrowserAuth()
    xit("should provide helpers to point the user in the right direction") {
        val reddit = emulateBrowserAuth("identity")
        ensureAuthenticated(reddit)
        // GET /api/v1/me requires the identity scope
        reddit.canAccess(Endpoint.GET_ME).should.be.`true`
        // GET [/r/subreddit]/stylesheet requires the modconfig scope, which we didn't request
        reddit.canAccess(Endpoint.GET_STYLESHEET).should.be.`false`

        val manager = reddit.authManager
        manager.refreshToken.should.not.be.`null`
        manager.canRenew().should.be.`true`
        manager.needsRenewing().should.be.`false`

        // refreshToken should remain the same
        val refreshToken = manager.refreshToken
        manager.renew()
        manager.refreshToken.should.equal(refreshToken)
    }

    xit("should throw an OAuthException when the user hits the 'decline' button instead") {
        val (helper, authorizePage) = doBrowserLogin()
        val redirectPage: HtmlPage = findChild<HtmlInput>(authorizePage.body, "input", "value" to "Decline").click()

        // test isFinalRedirectUrl
        val url = redirectPage.url.toExternalForm()
        helper.isFinalRedirectUrl(url).should.be.`true`

        expectException(OAuthException::class) {
            helper.onUserChallenge(url)
        }
    }
})
