package net.dean.jraw.test

import net.dean.jraw.http.OkHttpAdapter
import net.dean.jraw.http.oauth.OAuthHelper
import net.dean.jraw.test.util.CredentialsUtil
import net.dean.jraw.test.util.TestConfig
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class OAuthHelperTest: Spek({
    describe("script") {
        it("should produce a RedditClient authenticated with a script app") {
            val client = OAuthHelper.script(CredentialsUtil.script, OkHttpAdapter(TestConfig.userAgent))
            // Just make sure it doesn't fail
            client.me()
        }
    }
})
