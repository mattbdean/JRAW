package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.test.TestConfig.reddit
import net.dean.jraw.test.TestConfig.redditUserless
import net.dean.jraw.test.expectException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class SelfUserReferenceTest : Spek({
    describe("<init>") {
        it("should throw an IllegalStateException when trying to create an instance without a logged-in user") {
            expectException(IllegalStateException::class) {
                redditUserless.me()
            }
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
    }
})
