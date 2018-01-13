package net.dean.jraw.test.integration

import com.winterbe.expekt.should
import net.dean.jraw.test.CredentialsUtil.moderationSubreddit
import net.dean.jraw.test.TestConfig.reddit
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.*

class OtherUserFlairReferenceTest : Spek ({
    val moddedSubreddit = moderationSubreddit
    val otherUser = reddit.user("_vargas_")

    describe("otherUserFlairUpdate") {
        val newCssClass = "jrawTestCssClass${Random().nextInt()}"
        val newText = "jrawTestText${Random().nextInt()}"

        it("should update the other user flair") {
            otherUser.flairOn(moddedSubreddit).updateToCssClass(newCssClass, newText)
        }
        it("should have an effect on his model") {
            val updatedFlair = otherUser.flairOn(moddedSubreddit).current()
            updatedFlair.cssClass.should.equal(newCssClass)
            updatedFlair.text.should.equal(newText)
        }
    }
})
