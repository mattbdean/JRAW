package net.dean.jraw.test.integration

import com.gargoylesoftware.htmlunit.html.HtmlInput
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.winterbe.expekt.should
import net.dean.jraw.oauth.OAuthException
import net.dean.jraw.test.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class StatefulAuthHelperTest : Spek({
    it("should provide helpers to point the user in the right direction") {
        val reddit = emulateBrowserAuth()
        ensureAuthenticated(reddit)

        val manager = reddit.authManager
        manager.refreshToken.should.not.be.`null`
        manager.canRenew().should.be.`true`
        manager.needsRenewing().should.be.`false`

        // refreshToken should remain the same
        val refreshToken = manager.refreshToken
        manager.renew()
        manager.refreshToken.should.equal(refreshToken)
    }

    it("should throw an OAuthException when the user hits the 'decline' button instead") {
        val (helper, authorizePage) = doBrowserLogin()
        val redirectPage: HtmlPage = findChild<HtmlInput>(authorizePage.body, "input", "value" to "Decline").click()

        expectException(OAuthException::class) {
            helper.onUserChallenge(getUrlFrom(redirectPage))
        }
    }
})
