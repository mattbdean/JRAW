package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.ApiException
import net.dean.jraw.http.NetworkException
import net.dean.jraw.test.util.CredentialsUtil
import net.dean.jraw.test.util.TestConfig
import net.dean.jraw.test.util.TestConfig.reddit
import net.dean.jraw.test.util.TestConfig.redditUserless
import net.dean.jraw.test.util.expectException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class UserReferenceTest : Spek({
    describe("about") {
        it("should return an Account") {
            // We authenticated `reddit` using the script credentials, should have the same username
            reddit.me().about().name.should.equal(CredentialsUtil.script.username)

            val name = "_vargas_"
            reddit.user(name).about().name.should.equal(name)
        }
    }

    describe("trophies") {
        it("should return a List of Trophies") {
            // Just make sure it deserializes
            reddit.me().trophies()
            reddit.user("Shitty_Watercolour").trophies()
        }
    }

    describe("prefs") {
        it("should return a Map<String, Any>") {
            val prefs = reddit.me().prefs()
            prefs.should.have.size.above(0)
            prefs["over_18"].should.not.be.`null`

            expectException(ApiException::class) {
                redditUserless.user("_vargas_").prefs()
            }
        }
    }
})
