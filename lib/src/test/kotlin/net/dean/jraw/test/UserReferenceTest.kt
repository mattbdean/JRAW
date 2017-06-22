package net.dean.jraw.test

import com.winterbe.expekt.should
import net.dean.jraw.ApiException
import net.dean.jraw.test.util.CredentialsUtil
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

    describe("prefs and patchPrefs") {
        it("should return a Map<String, Any>") {
            val prefs = reddit.me().prefs()
            prefs.should.have.size.above(0)
            // This one has been here since forever, if this one isn't here either reddit has undergone a major API
            // change or we're making the request wrong.
            prefs["over_18"].should.not.be.`null`
        }

        it("should update the preferences") {
            val me = reddit.me()

            // Go with something that's 1) pretty much guaranteed to be there and 2) a boolean so we can sipmly toggle
            // the value
            val key = "over_18"
            val newVal = !(me.prefs()[key] as Boolean)
            val newPrefsPatch = mapOf(key to newVal)
            me.patchPrefs(newPrefsPatch)[key].should.equal(newVal)
        }

        it("should throw an ApiException when the user isn't 'me'") {
            expectException(ApiException::class) {
                redditUserless.me().prefs()
            }

            val oldPrefs = reddit.me().prefs()
            expectException(ApiException::class) {
                redditUserless.me().patchPrefs(oldPrefs)
            }
        }
    }
})
