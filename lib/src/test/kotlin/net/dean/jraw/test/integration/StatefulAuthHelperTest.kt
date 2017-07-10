package net.dean.jraw.test.integration

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlButton
import com.gargoylesoftware.htmlunit.html.HtmlElement
import com.gargoylesoftware.htmlunit.html.HtmlInput
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.winterbe.expekt.should
import net.dean.jraw.RedditClient
import net.dean.jraw.oauth.OAuthException
import net.dean.jraw.oauth.OAuthHelper
import net.dean.jraw.oauth.StatefulAuthHelper
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

private fun createWebClient(): WebClient {
    val client = WebClient()

    // Reddit does some weird things with JS, but it's not necessary to be emulating them for this test
    client.options.isJavaScriptEnabled = false
    // Turn off CSS because Jacoco complains that one of the classes HtmlUnit it pulls in is too long (like ~5000 lines)
    // com.steadystate.css.parser.SACParserCSS3TokenManager
    client.options.isCssEnabled = false
    // Save some time
    client.options.isDownloadImages = false

    return client
}

private fun getUrlFrom(page: HtmlPage): String = page.webResponse.webRequest.url.toExternalForm()

private fun doBrowserLogin(vararg scopes: String = arrayOf("identity")): Pair<StatefulAuthHelper, HtmlPage> {
    val helper = OAuthHelper.interactive(newOkHttpAdapter(), CredentialsUtil.app, InMemoryTokenStore())

    // Test state change once we get the authorization URL
    helper.authStatus.should.equal(StatefulAuthHelper.Status.INIT)
    val url = helper.getAuthorizationUrl(permanent = true, useMobileSite = false, scopes = *scopes)
    helper.authStatus.should.equal(StatefulAuthHelper.Status.WAITING_FOR_CHALLENGE)

    val client = createWebClient()

    // First we're gonna log in with the testing user credentials
    val loginPage = client.getPage<HtmlPage>(url)
    val loginForm = loginPage.forms.first { it.id == "login-form" }
    loginForm.getInputByName<HtmlInput>("user").valueAttribute = CredentialsUtil.script.username
    loginForm.getInputByName<HtmlInput>("passwd").valueAttribute = CredentialsUtil.script.password

    // Submit the form so we get redirected to the page where we can authorize our app
    val authorizePage: HtmlPage = findChild<HtmlButton>(loginForm, "button", "type" to "submit").click()
    return helper to authorizePage
}

fun emulateBrowserAuth(vararg scopes: String = arrayOf("identity")): RedditClient {
    val (helper, authorizePage) = doBrowserLogin(*scopes)
    val redirectPage: HtmlPage = findChild<HtmlInput>(authorizePage.body, "input", "name" to "authorize").click()

    val reddit = helper.onUserChallenge(getUrlFrom(redirectPage))
    helper.authStatus.should.equal(StatefulAuthHelper.Status.AUTHORIZED)
    return reddit
}

private fun <E : HtmlElement> findChild(parent: HtmlElement, elName: String, attribute: Pair<String, String>): E {
    val elements: List<E> = parent.getElementsByAttribute(elName, attribute.first, attribute.second)
    if (elements.isEmpty())
        throw NoSuchElementException("Could not find element for selector '$elName[${attribute.first}=\"${attribute.second}\"']")

    return elements[0]
}

