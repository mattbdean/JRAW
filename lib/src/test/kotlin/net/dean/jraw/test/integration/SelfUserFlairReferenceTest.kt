package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.models.CurrentFlair
import net.dean.jraw.models.Flair
import net.dean.jraw.test.TestConfig.reddit
import net.dean.jraw.test.TestConfig.redditUserless
import net.dean.jraw.test.expectException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class SelfUserFlairReferenceTest : Spek({
    val sr = reddit.subreddit("jraw_testing2")
    val flairRef = sr.selfUserFlair()

    val flairOptions: List<Flair> by lazy {
        sr.userFlairOptions()
    }

    describe("setFlairEnabled/enableFlair/disableFlair") {
        it("should enable or disable flair") {
            flairRef.enableFlair()
            sr.about().isFlairEnabledForUser.should.be.`true`

            flairRef.disableFlair()
            sr.about().isFlairEnabledForUser.should.be.`false`

            flairRef.setFlairEnabled(true)
            sr.about().isFlairEnabledForUser.should.be.`true`
        }
    }

    describe("current") {
        it("should return a CurrentFlair instance") {
            flairRef.current().should.be.an.instanceof(CurrentFlair::class.java)
        }

        it("should throw an IllegalStateException when there is no authenticated user") {
            expectException(IllegalStateException::class) {
                redditUserless.subreddit("foo").selfUserFlair()
            }
        }
    }

    describe("updateTo") {
        it("should update the flair") {
            val newFlair = flairOptions.first()
            flairRef.updateTo(newFlair.id)
            flairRef.current().id.should.equal(newFlair.id)
        }

        it("should update the flair with custom text when it's editable") {
            val newFlair = flairOptions.first { it.isTextEditable }
            val flairText = "edited flair text"
            flairRef.updateTo(newFlair.id, flairText)

            val current = flairRef.current()
            current.id.should.equal(newFlair.id)
            current.text.should.equal(flairText)
        }
    }

    describe("remove") {
        it("should remove the flair") {
            if (!flairRef.current().isPresent)
                // Make sure we have some flair set already
                flairRef.updateTo(flairOptions.first().id)

            flairRef.current().isPresent.should.be.`true`
            flairRef.remove()
            flairRef.current().isPresent.should.be.`false`
        }
    }
})
